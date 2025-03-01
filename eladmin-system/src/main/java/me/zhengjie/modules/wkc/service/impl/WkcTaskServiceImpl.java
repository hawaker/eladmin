/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.wkc.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.date.DateUtil;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.domain.WkcTask;
import me.zhengjie.modules.wkc.domain.WkcUser;
import me.zhengjie.modules.wkc.dto.remote.DownloadListDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.repository.WkcJobRepository;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.typs.WkcTaskStateEnum;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.wkc.repository.WkcTaskRepository;
import me.zhengjie.modules.wkc.service.WkcTaskService;
import me.zhengjie.modules.wkc.service.dto.WkcTaskDto;
import me.zhengjie.modules.wkc.service.dto.WkcTaskQueryCriteria;
import me.zhengjie.modules.wkc.service.mapstruct.WkcTaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.springframework.util.CollectionUtils;

/**
 * @author hawaker
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-11-17
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class WkcTaskServiceImpl implements WkcTaskService {

  private final WkcTaskRepository wkcTaskRepository;
  private final WkcTaskMapper wkcTaskMapper;
  @Autowired
  WkcUserService wkcUserService;
  @Autowired
  WkcJobRepository wkcJobRepository;
  @Autowired
  JobTypeProxyService jobTypeProxyService;

  @Override
  public Map<String, Object> queryAll(WkcTaskQueryCriteria criteria, Pageable pageable) {
    Page<WkcTask> page = wkcTaskRepository.findAll(
        (root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(wkcTaskMapper::toDto));
  }

  @Override
  public List<WkcTaskDto> queryAll(WkcTaskQueryCriteria criteria) {
    return wkcTaskMapper.toDto(wkcTaskRepository.findAll(
        (root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder)));
  }

  @Override
  @Transactional
  public WkcTaskDto findById(Integer id) {
    WkcTask wkcTask = wkcTaskRepository.findById(id).orElseGet(WkcTask::new);
    ValidationUtil.isNull(wkcTask.getId(), "WkcTask", "id", id);
    return wkcTaskMapper.toDto(wkcTask);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public WkcTaskDto create(WkcTask resources) {
    return wkcTaskMapper.toDto(wkcTaskRepository.save(resources));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(WkcTask resources) {
    WkcTask wkcTask = wkcTaskRepository.findById(resources.getId()).orElseGet(WkcTask::new);
    ValidationUtil.isNull(wkcTask.getId(), "WkcTask", "id", resources.getId());
    wkcTask.copy(resources);
    wkcTaskRepository.save(wkcTask);
  }

  @Override
  public void deleteAll(Integer[] ids) {
    for (Integer id : ids) {
      wkcTaskRepository.deleteById(id);
    }
  }

  @Override
  public void download(List<WkcTaskDto> all, HttpServletResponse response) throws IOException {
    List<Map<String, Object>> list = new ArrayList<>();
    for (WkcTaskDto wkcTask : all) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("玩客云的任务ID", wkcTask.getWkcId());
      map.put("状态", wkcTask.getState());
      map.put("地址", wkcTask.getUrl());
      map.put("大小", wkcTask.getSize());
      map.put("创建时间", wkcTask.getCreateTime());
      map.put(" failCode", wkcTask.getFailCode());
      map.put(" speed", wkcTask.getSpeed());
      map.put(" downTime", wkcTask.getDownTime());
      map.put(" complateTime", wkcTask.getComplateTime());
      map.put(" type", wkcTask.getType());
      map.put(" name", wkcTask.getName());
      map.put(" progress", wkcTask.getProgress());
      map.put(" exist", wkcTask.getExist());
      map.put(" remainTime", wkcTask.getRemainTime());
      map.put(" errorCount", wkcTask.getErrorCount());
      list.add(map);
    }
    FileUtil.downloadExcel(list, response);
  }

  /**
   * 检查同时进行的任务数
   *
   * @param wkcUserId
   * @param taskCount
   */
  @Override
  public void taskCheck(Integer wkcUserId, Integer taskCount) {
    List<WkcTask> tasks = wkcTaskRepository.findNotDeleteAndStateNotIn(
        Arrays.asList(
            WkcTaskStateEnum.done.getId(),
            WkcTaskStateEnum.deprecated.getId()
        )
    );
    if (CollectionUtils.isEmpty(tasks)) {
      return;
    }
    log.info("进行中的任务数:{}",tasks.size());
    Map<Integer, List<WkcTask>> taskMap = tasks.stream()
        .collect(Collectors.groupingBy(WkcTask::getState));
    Integer maxDownloadingTask = 3;
    List<WkcTask> downLodingTasks = taskMap.get(WkcTaskStateEnum.downloading.getId());
    Integer downloadingCount = 0;
    if (!CollectionUtils.isEmpty(downLodingTasks)) {
      downloadingCount = downLodingTasks.size();
    }
    if (downloadingCount < maxDownloadingTask) {
      Integer startCount = startSuspend(taskMap.get(WkcTaskStateEnum.suspend.getId()),
          maxDownloadingTask - downloadingCount);
      if (startCount + downloadingCount < maxDownloadingTask) {
        startSuspend(taskMap.get(WkcTaskStateEnum.error.getId()),
            maxDownloadingTask - downloadingCount - startCount);
      }
    }
    List<WkcTask> wattingTasks = taskMap.get(WkcTaskStateEnum.waiting.getId());
    Integer wattingCount = wattingTasks == null ? 0 : wattingTasks.size();
    Integer pauseCount = 0;
    Integer speedLimit=100000;
    if (!CollectionUtils.isEmpty(downLodingTasks)) {
      for (int i = 0; i < downLodingTasks.size(); i++) {
        // 速度小于 100K 暂停任务
        WkcTask s = downLodingTasks.get(i);
        if (s.getSpeed() >= speedLimit) {
          continue;
        }
        log.info("任务名称:{},id:{},taskId:{},下载速度小于:{},暂停",s.getName(),s.getId(),s.getWkcId(),speedLimit);
        wkcUserService.pauseTask(s.getWkcUserId(), s.getPeerId(), s.getWkcId());
        s.setState(WkcTaskStateEnum.suspend.getId());
        pauseCount++;
      }
    }
    if (wattingCount<=0&&pauseCount>0){
      Integer startCount = startSuspend(taskMap.get(WkcTaskStateEnum.suspend.getId()), pauseCount);
      if (startCount + pauseCount < maxDownloadingTask) {
        startSuspend(taskMap.get(WkcTaskStateEnum.error.getId()), pauseCount-startCount);
      }
    }
  }

  /**
   * 按完成度
   *
   * @param wkcTasks
   * @param count
   */
  public Integer startSuspend(List<WkcTask> wkcTasks, Integer count) {
    if (CollectionUtils.isEmpty(wkcTasks)) {
      return 0;
    }
    if (count <= 0) {
      return 0;
    }
    Integer startCount = 0;
    List<WkcTask> buf = wkcTasks.stream()
        .sorted(
            Comparator
                .comparing(WkcTask::getErrorCount).reversed()
                .thenComparing(WkcTask::getProgress)
                .reversed()
        ).collect(Collectors.toList());
    for (int i = 0; i < count; i++) {
      WkcTask wkcTask = buf.get(i);
      wkcTask.setErrorCount(wkcTask.getErrorCount() + 1);
      wkcUserService.startTask(wkcTask.getWkcUserId(), wkcTask.getPeerId(), wkcTask.getWkcId());
      wkcTaskRepository.save(wkcTask);
      startCount++;
    }
    return startCount;
  }

  @Override
  public void syncTask(String wkcUserIdStr) {
    Integer wkcUserId = Integer.parseInt(wkcUserIdStr);
    Integer page = 0;
    Integer perPage = 10;
    WkcUser wkcUser = wkcUserService.getWkcUser(wkcUserId);
    wkcTaskRepository.updateAllNotExists();
    while (true) {
      DownloadListDto downloadListDto = wkcUserService
          .queryUserTasks(wkcUserId, wkcUser.getDefaultPeerId(), page * perPage, perPage);
      if (!downloadListDto.success()) {
        break;
      }
      List<TaskDto> tasks = downloadListDto.getTasks();
      if (CollectionUtils.isEmpty(tasks)) {
        break;
      }
      for (TaskDto task : tasks) {
        syncTask(task, wkcUserId, wkcUser.getDefaultPeerId());
      }
      page++;
    }
    taskCheck(wkcUserId, 20);
  }

  private void syncTask(TaskDto task, Integer wkcUserId, String peerId) {
    WkcTask wkcTask = wkcTaskRepository.getByWkcId(task.getId());
    if (null == wkcTask) {
      wkcTask = new WkcTask();
      wkcTask.setWkcUserId(wkcUserId);
      wkcTask.setWkcId(task.getId());
      wkcTask.setErrorCount(0);
      wkcTask.setPeerId(peerId);
    }
    wkcTask.setRemoteDelete(false);
    BeanUtils.copyProperties(task, wkcTask, new String[]{"id"});
    wkcTask.setLastSyncTime((int) Instant.now().getEpochSecond());
    if (WkcTaskStateEnum.done.getId() == task.getState() && !task.getExist()) {
      wkcUserService.delTask(wkcUserId, peerId, task.getId(), false, false);
      log.info("文件已完成,删除任务记录{}", task.getId());
    }
    wkcTaskRepository.save(wkcTask);
  }
}