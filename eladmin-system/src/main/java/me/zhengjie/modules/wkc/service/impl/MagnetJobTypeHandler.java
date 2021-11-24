package me.zhengjie.modules.wkc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.service.DictDetailService;
import me.zhengjie.modules.system.service.dto.DictDetailDto;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.dto.control.PartitionDto;
import me.zhengjie.modules.wkc.dto.remote.FileDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;
import me.zhengjie.modules.wkc.service.HandleJobType;
import me.zhengjie.modules.wkc.service.JobTypeHandler;
import me.zhengjie.modules.wkc.service.WkcJobService;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.service.dto.WkcUserDto;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.TemplateUtils;
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

  @Autowired
  DictDetailService dictDetailService;

  @Override
  public void handle(WkcJob job) {
    WkcUserDto user = wkcUserService.findById(job.getWkcUserId());
    if (user.getDefaultDeviceId() == null || user.getDefaultDeviceId().isEmpty()) {
      log.error("用户[{}]未设置默认设备", user.getPhone());
      job.setExceptionMsg("用户未设置默认设备");
      job.setStatus(-1);
      wkcJobService.update(job);
      return;
    }
    List<PartitionDto> usbs=wkcUserService.getUSBInfo(job.getWkcUserId(),user.getDefaultDeviceId());
    if (CollectionUtils.isEmpty(usbs)){
      log.error("用户[{}]默认设备未在线", user.getPhone());
      job.setExceptionMsg("用户默认设备未在线");
      job.setStatus(-1);
      wkcJobService.update(job);
      return;
    }
    UrlResolveDto urlResolveDto = wkcUserService
        .resolveUrl(job.getWkcUserId(), user.getDefaultPeerId(), job.getUrl());
    if (!urlResolveDto.success()) {
      log.error("url[{}]解析失败", job.getUrl(), urlResolveDto);
      job.setStatus(-1);
      job.setExceptionMsg("url解析失败!");
      wkcJobService.update(job);
      return;
    }
    TaskDto taskDto = urlResolveDto.getTaskDto();
    if (urlResolveDto.getInfoHash() == null || urlResolveDto.getInfoHash().isEmpty()) {
      log.error("MAGNET[{}]未找到HashInfo", job.getUrl());
      job.setStatus(-1);
      job.setExceptionMsg("未获取到hashInfo");
      wkcJobService.update(job);
      return;
    }

    if (user.getDefaultUsbUuid() == null) {
      log.error("用户{}的默认下载磁盘为空", user.getPhone());
      job.setStatus(-1);
      job.setExceptionMsg("默认下载磁盘为空");
      wkcJobService.update(job);
      return;
    }
    List<DictDetailDto> dictDetails=dictDetailService.getDictByName("WKC");
    if (!CollectionUtils.isEmpty(dictDetails)){
      taskDto.getSubList().stream().forEach(s -> {
        for (DictDetailDto dictDetailDto : dictDetails) {
          String buf = TemplateUtils.process(fileToMap(s), dictDetailDto.getValue());
          if (!StringUtils.isAllBlank(buf)) {
            log.info("任务ID:{},文件名:{},命中配置:{}",job.getId(),s.getName(),dictDetailDto.getLabel());
            s.setSelected(0);
            return;
          }
        }
        s.setSelected(1);
      });
    }


    String path = user.getDefaultUsbPath() + "/onecloud/tddownload";
    TaskActionDto taskActionDto = wkcUserService.createTask(job.getWkcUserId(),
        user.getDefaultPeerId(), path, taskDto.getName(), taskDto.getUrl(), taskDto.getSubList());
    if (!taskActionDto.success()) {
      job.setStatus(-1);
      job.setExceptionMsg("下载失败!");
      wkcJobService.update(job);
      return;
    }
    job.setStatus(1);
    job.setName(taskDto.getName());
    log.info("处理任务成功[{}][{}]", job.getType(), job.getUrl());
    wkcJobService.update(job);
  }

  private static Map<String,Object> fileToMap(FileDto fileDto){
    Map<String,Object> map = new HashMap<>();
    map.put("name",fileDto.getName());
    map.put("id",fileDto.getId());
    map.put("selected",fileDto.getSelected());
    map.put("size",fileDto.getSize());
    return map;
  }

  public static void main(String[] args) {
    FileDto fileDto=new FileDto();
    fileDto.setName("avman.app_huntb00140/FGO命運1080p-23.mp4");
    fileDto.setSelected(0);
    fileDto.setSize(1368443);
    fileDto.setId("1");

    String buf1=TemplateUtils.process(fileToMap(fileDto),"<#if name?contains('FGO')>1</#if>");
    System.out.println(buf1);

    String buf=TemplateUtils.process(fileToMap(fileDto),"<#if name?ends_with('.apk')>1</#if>");
    System.out.println(buf);

  }
}
