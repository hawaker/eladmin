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

import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.wkc.client.WanKeCloudService;
import me.zhengjie.modules.wkc.domain.WkcUser;
import me.zhengjie.modules.wkc.dto.account.AccountDto;
import me.zhengjie.modules.wkc.dto.account.AccountResponseDto;
import me.zhengjie.modules.wkc.dto.account.UserDto;
import me.zhengjie.modules.wkc.dto.control.ControlResponseDto;
import me.zhengjie.modules.wkc.dto.control.DeviceDto;
import me.zhengjie.modules.wkc.dto.control.PartitionDto;
import me.zhengjie.modules.wkc.dto.remote.DownloadListDto;
import me.zhengjie.modules.wkc.dto.remote.FileDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;
import me.zhengjie.modules.wkc.repository.WkcUserRepository;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.service.dto.WkcUserDto;
import me.zhengjie.modules.wkc.service.dto.WkcUserQueryCriteria;
import me.zhengjie.modules.wkc.service.mapstruct.WkcUserMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author hawaker
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-06-21
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class WkcUserServiceImpl implements WkcUserService {

  private final WkcUserRepository wkcUserRepository;
  private final WkcUserMapper wkcUserMapper;
  @Autowired
  WanKeCloudService wanKeCloudService;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  private final String redisTokenPrefix="WKC_TOKEN_";

  @Override
  public Map<String, Object> queryAll(WkcUserQueryCriteria criteria, Pageable pageable) {
    Page<WkcUser> page = wkcUserRepository.findAll(
        (root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(wkcUserMapper::toDto));
  }

  @Override
  public List<WkcUserDto> queryAll(WkcUserQueryCriteria criteria) {
    return wkcUserMapper.toDto(wkcUserRepository.findAll(
        (root, criteriaQuery, criteriaBuilder) -> QueryHelp
            .getPredicate(root, criteria, criteriaBuilder)));
  }

  @Override
  @Transactional
  public WkcUserDto findById(Integer id) {
    WkcUser wkcUser = wkcUserRepository.findById(id).orElseGet(WkcUser::new);
    ValidationUtil.isNull(wkcUser.getId(), "WkcUser", "id", id);
    return wkcUserMapper.toDto(wkcUser);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public WkcUserDto create(WkcUser resources) {
    return wkcUserMapper.toDto(wkcUserRepository.save(resources));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(WkcUser resources) {
    WkcUser wkcUser = wkcUserRepository.findById(resources.getId()).orElseGet(WkcUser::new);
    ValidationUtil.isNull(wkcUser.getId(), "WkcUser", "id", resources.getId());
    wkcUser.copy(resources);
    wkcUserRepository.save(wkcUser);
  }

  @Override
  public void deleteAll(Integer[] ids) {
    for (Integer id : ids) {
      wkcUserRepository.deleteById(id);
    }
  }

  @Override
  public void download(List<WkcUserDto> all, HttpServletResponse response) throws IOException {
    List<Map<String, Object>> list = new ArrayList<>();
    for (WkcUserDto wkcUser : all) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("手机号", wkcUser.getPhone());
      map.put("关联用户", wkcUser.getUserId());
      map.put("密码", wkcUser.getPassword());
      map.put("玩客云token", wkcUser.getToken());
      map.put(" accountType", wkcUser.getAccountType());
      map.put(" enableHomeShare", wkcUser.getEnableHomeShare());
      map.put(" bindPwd", wkcUser.getBindPwd());
      map.put(" phoneArea", wkcUser.getPhoneArea());
      map.put("归属用户", wkcUser.getBindUser());
      list.add(map);
    }
    FileUtil.downloadExcel(list, response);
  }



  public WkcUser getWkcUser(Integer wkcUserId) {
    Optional<WkcUser> wkcUserOpt = wkcUserRepository.findById(wkcUserId);
    Assert.notNull(wkcUserOpt, "获取玩客云用户出错,id:" + wkcUserId);
    WkcUser wkcUser=wkcUserOpt.get();
    String token =redisTemplate.opsForValue().get(redisTokenPrefix+wkcUserId);
    if (null == token) {
      AccountResponseDto<UserDto> userDtoAccountResponseDto = wanKeCloudService
          .login(wkcUser.getPhone(), wkcUser.getPassword());
      Assert.isTrue(userDtoAccountResponseDto.success(), "用户登录失败,id:" + wkcUserId);
      UserDto userDto = userDtoAccountResponseDto.getData();
      token = userDto.getSessionId();
      redisTemplate.opsForValue().set(redisTokenPrefix + wkcUserId, token, 12, TimeUnit.HOURS);
    }
    wkcUser.setToken(token);
    return wkcUser;
  }

  @Override
  public UserDto login(Integer wkcUserId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    AccountResponseDto<UserDto> userDtoAccountResponseDto = wanKeCloudService
        .login(wkcUser.getPhone(), wkcUser.getPassword());
    Assert.isTrue(userDtoAccountResponseDto.success(), "用户登录失败,id:" + wkcUserId);
    UserDto userDto = userDtoAccountResponseDto.getData();
    wkcUser.setUserId(userDto.getUserId());
    wkcUserRepository.save(wkcUser);
    return userDto;
  }

  @Override
  public AccountDto getAccountInfo(Integer wkcUserId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    AccountResponseDto<AccountDto> userDtoAccountResponseDto = wanKeCloudService
        .getAccountInfo(wkcUser.getPhone(), wkcUser.getPassword());
    Assert.isTrue(userDtoAccountResponseDto.success(), "获取用户信息失败,id:" + wkcUserId);
    return null;
  }

  @Override
  public List<DeviceDto> getPeerInfo(Integer wkcUserId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    ControlResponseDto controlResponseDto = wanKeCloudService
        .getPeerInfo(wkcUser.getToken(), wkcUser.getUserId());
    Assert.isTrue(controlResponseDto.success(),
        "获取用户节点信息失败,id:" + wkcUserId + ",msg:" + controlResponseDto.getMsg());
    return controlResponseDto.getAppearence().getDevices();
  }

  @Override
  public List<PartitionDto> getUSBInfo(Integer wkcUserId, String deviceId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    ControlResponseDto usb = wanKeCloudService
        .getUSBInfo(wkcUser.getToken(), wkcUser.getUserId(), deviceId);
    Assert.isTrue(usb.success(), "获取用户存储设备失败:Id" + wkcUserId + ",deviceId:" + deviceId);
    return usb.getAppearence().getPartitions();
  }

  @Override
  public void setUserDefaultDevice(Integer wkcUserId, String deviceId, String peerId, String uuid, String path) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    wkcUser.setDefaultDeviceId(deviceId);
    wkcUser.setDefaultUsbUuid(uuid);
    wkcUser.setDefaultUsbPath(path);
    wkcUser.setDefaultPeerId(peerId);
    wkcUserRepository.save(wkcUser);
  }

  @Override
  public DownloadListDto queryUserTasks(Integer wkcUserId, String peerId, Integer position,
      Integer number) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    DownloadListDto downloadListDto = wanKeCloudService
        .remoteDownloadList(wkcUser.getToken(), wkcUser.getUserId(), peerId, position, number);
    Assert.isTrue(downloadListDto.success(), "获取用户任务列表失败:Id" + wkcUserId + ",peerId:" + peerId);
    return downloadListDto;
  }


  @Override
  public TaskActionDto pauseTask(Integer wkcUserId, String peerId, String taskId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    return wanKeCloudService.pause(wkcUser.getToken(), wkcUser.getUserId(), peerId, taskId);
  }

  @Override
  public TaskActionDto startTask(Integer wkcUserId, String peerId, String taskId) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    return wanKeCloudService.start(wkcUser.getToken(), wkcUser.getUserId(), peerId, taskId);
  }

  @Override
  public TaskActionDto delTask(Integer wkcUserId, String peerId, String taskId, Boolean deleteFile,
      Boolean recycleTask) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    return wanKeCloudService
        .del(wkcUser.getToken(), wkcUser.getUserId(), peerId, taskId, deleteFile, recycleTask);
  }

  @Override
  public TaskActionDto createTask(Integer wkcUserId, String peerId, String path, String name,
      String url) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    TaskDto taskDto = new TaskDto();
    taskDto.setName(name);
    taskDto.setUrl(url);
    return wanKeCloudService
        .createTask(wkcUser.getToken(), wkcUser.getUserId(), peerId, path, taskDto);
  }

  @Override
  public TaskActionDto createTask(Integer wkcUserId, String peerId, String path, String name,
      String url,List<FileDto> files) {
    if (CollectionUtils.isEmpty(files)){
      return createTask(wkcUserId,peerId,path,name,url);
    }
    WkcUser wkcUser = getWkcUser(wkcUserId);
    TaskDto taskDto = new TaskDto();
    taskDto.setName(name);
    taskDto.setUrl(url);
    taskDto.setBtSub(files.stream().map(FileDto::getId).collect(Collectors.toList()));
    return wanKeCloudService
        .createTask(wkcUser.getToken(), wkcUser.getUserId(), peerId, path, taskDto);
  }

  @Override
  public UrlResolveDto resolveUrl(Integer wkcUserId, String peerId, String url) {
    WkcUser wkcUser = getWkcUser(wkcUserId);
    return wanKeCloudService.urlResolve(wkcUser.getToken(), wkcUser.getUserId(), peerId, url);
  }


  @Override
  public WkcUser findByUuid(String uuid) {
    return wkcUserRepository.findByUuid(uuid).get();
  }


  @Override
  public void refreshUuid(Integer wkcUserId) {
    WkcUser wkcUser=getWkcUser(wkcUserId);
    if (wkcUser.getUuid()!=null){
      redisTemplate.delete(wkcUser.getUuid());
    }
    UUID uuid = UUID.randomUUID();
    wkcUser.setUuid(uuid.toString());
    update(wkcUser);
  }
}