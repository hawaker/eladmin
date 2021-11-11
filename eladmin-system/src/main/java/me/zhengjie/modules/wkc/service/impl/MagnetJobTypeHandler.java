package me.zhengjie.modules.wkc.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.dto.control.PartitionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;
import me.zhengjie.modules.wkc.service.HandleJobType;
import me.zhengjie.modules.wkc.service.JobTypeHandler;
import me.zhengjie.modules.wkc.service.WkcJobService;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.service.dto.WkcUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * Description:
 *
 * @date:2021/8/1 12:23 上午
 * @author: caoqingyuan
 */
@Slf4j
@HandleJobType("magnet")
public class MagnetJobTypeHandler implements JobTypeHandler {

  @Autowired
  WkcUserService wkcUserService;
  @Autowired
  WkcJobService wkcJobService;

  @Override
  public void handle(WkcJob job) {
    WkcUserDto user = wkcUserService.findById(job.getWkcUserId());
    if (user.getDefaultDeviceId() == null || user.getDefaultDeviceId().isEmpty()) {
      log.error("用户[{}]未设置默认设备", user.getPhone());
      job.setExceptionMsg("用户未设置默认设备");
      wkcJobService.update(job);
      return;
    }
    List<PartitionDto> usbs=wkcUserService.getUSBInfo(job.getWkcUserId(),user.getDefaultDeviceId());
    if (CollectionUtils.isEmpty(usbs)){
      log.error("用户[{}]默认设备未在线", user.getPhone());
      job.setExceptionMsg("用户默认设备未在线");
      wkcJobService.update(job);
      return;
    }
    UrlResolveDto urlResolveDto = wkcUserService
        .resolveUrl(job.getWkcUserId(), user.getDefaultPeerId(), job.getUrl());
    if (!urlResolveDto.success()) {
      log.error("url[{}]解析失败", job.getUrl(), urlResolveDto);
      job.setExceptionMsg("url解析失败!");
      wkcJobService.update(job);
      return;
    }
    TaskDto taskDto = urlResolveDto.getTaskDto();
    if (urlResolveDto.getInfoHash() == null || urlResolveDto.getInfoHash().isEmpty()) {
      log.error("MAGNET[{}]未找到HashInfo", job.getUrl());
      job.setExceptionMsg("未获取到hashInfo");
      wkcJobService.update(job);
      return;
    }

    if (user.getDefaultUsbUuid() == null) {
      log.error("用户{}的默认下载磁盘为空", user.getPhone());
      job.setExceptionMsg("默认下载磁盘为空");
      wkcJobService.update(job);
      return;
    }

    String path = user.getDefaultUsbPath() + "/onecloud/tddownload";
    TaskActionDto taskActionDto = wkcUserService.createTask(job.getWkcUserId(),
        user.getDefaultPeerId(), path, taskDto.getName(), taskDto.getUrl());
    if (!taskActionDto.success()) {
      job.setExceptionMsg("下载失败!");
      wkcJobService.update(job);
      return;
    }
    job.setStatus(1);
    job.setName(taskDto.getName());
    log.info("处理任务成功[{}][{}]", job.getType(), job.getUrl());
    wkcJobService.update(job);
  }
}
