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
package me.zhengjie.modules.wkc.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.wkc.domain.WkcUser;
import me.zhengjie.modules.wkc.dto.account.UserDto;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.service.dto.WkcUserQueryCriteria;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hawaker
 * @website https://el-admin.vip
 * @date 2021-06-21
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "wkcUser管理")
@RequestMapping("/api/wkcUser")
public class WkcUserController {

  private final WkcUserService wkcUserService;

  @Log("导出数据")
  @ApiOperation("导出数据")
  @GetMapping(value = "/download")
  @PreAuthorize("@el.check('wkcUser:list')")
  public void download(HttpServletResponse response, WkcUserQueryCriteria criteria)
      throws IOException {
    wkcUserService.download(wkcUserService.queryAll(criteria), response);
  }

  @GetMapping
  @Log("查询wkcUser")
  @ApiOperation("查询wkcUser")
  @PreAuthorize("@el.check('wkcUser:list')")
  public ResponseEntity<Object> query(WkcUserQueryCriteria criteria, Pageable pageable) {
    Long userId = SecurityUtils.getCurrentUserId();
    criteria.setBindUser(userId);
    return new ResponseEntity<>(wkcUserService.queryAll(criteria, pageable), HttpStatus.OK);
  }

  @PostMapping
  @Log("新增wkcUser")
  @ApiOperation("新增wkcUser")
  @PreAuthorize("@el.check('wkcUser:add')")
  public ResponseEntity<Object> create(@Validated @RequestBody WkcUser resources) {
    Long userId = SecurityUtils.getCurrentUserId();
    resources.setBindUser(userId);
    return new ResponseEntity<>(wkcUserService.create(resources), HttpStatus.CREATED);
  }

  @PutMapping
  @Log("修改wkcUser")
  @ApiOperation("修改wkcUser")
  @PreAuthorize("@el.check('wkcUser:edit')")
  public ResponseEntity<Object> update(@Validated @RequestBody WkcUser resources) {
    wkcUserService.update(resources);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Log("删除wkcUser")
  @ApiOperation("删除wkcUser")
  @PreAuthorize("@el.check('wkcUser:del')")
  @DeleteMapping
  public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
    wkcUserService.deleteAll(ids);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Log("登录")
  @ApiOperation("登录")
  @PreAuthorize("@el.check('wkcUser:login')")
  @GetMapping("/login")
  public ResponseEntity<Object> login(@RequestParam Integer id) {
    UserDto userDto=wkcUserService.login(id);
    return new ResponseEntity<>(userDto,HttpStatus.OK);
  }

  @Log("获取玩客云节点")
  @ApiOperation("获取玩客云节点")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/peer")
  public ResponseEntity<Object> peer(@RequestParam Integer id) {
    return new ResponseEntity<>(wkcUserService.getPeerInfo(id), HttpStatus.OK);
  }


  @Log("获取玩客云节点设备")
  @ApiOperation("获取玩客云节点设备")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/usb")
  public ResponseEntity<Object> usb(@RequestParam Integer id, @RequestParam String deviceId) {
    return new ResponseEntity<>(wkcUserService.getUSBInfo(id, deviceId), HttpStatus.OK);
  }


  @Log("设置用户默认节点和设备")
  @ApiOperation("获取玩客云节点设备")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/defaultDevice")
  public ResponseEntity<Object> defaultDevice(@RequestParam Integer id,
      @RequestParam String deviceId,@RequestParam String peerId, @RequestParam String uuid, @RequestParam String path) {
    wkcUserService.setUserDefaultDevice(id, deviceId, peerId, uuid, path);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Log("获取用户所选节点任务")
  @ApiOperation("获取用户所选节点任务")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/tasks")
  public ResponseEntity<Object> tasks(@RequestParam Integer id, @RequestParam String peerId,
      @RequestParam Integer position, @RequestParam Integer number) {
    return new ResponseEntity<>(wkcUserService.queryUserTasks(id, peerId, position, number),
        HttpStatus.OK);
  }

  @Log("暂停玩客云任务")
  @ApiOperation("暂停玩客云任务")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/pauseTask")
  public ResponseEntity<Object> pauseTask(@RequestParam Integer id, @RequestParam String peerId,
      @RequestParam String taskId) {
    return new ResponseEntity<>(wkcUserService.pauseTask(id, peerId, taskId), HttpStatus.OK);
  }

  @Log("开始玩客云任务")
  @ApiOperation("开始玩客云任务")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/startTask")
  public ResponseEntity<Object> startTask(@RequestParam Integer id, @RequestParam String peerId,
      @RequestParam String taskId) {
    return new ResponseEntity<>(wkcUserService.startTask(id, peerId, taskId), HttpStatus.OK);
  }

  @Log("删除玩客云任务")
  @ApiOperation("删除玩客云任务")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/delTask")
  public ResponseEntity<Object> delTask(@RequestParam Integer id, @RequestParam String peerId,
      @RequestParam String taskId, @RequestParam Boolean deleteFile,
      @RequestParam Boolean recycleTask) {
    return new ResponseEntity<>(wkcUserService.delTask(id, peerId, taskId, deleteFile, recycleTask),
        HttpStatus.OK);
  }

  @Log("创建玩客云任务")
  @ApiOperation("创建玩客云任务")
  @PreAuthorize("@el.check('wkcUser:peer')")
  @GetMapping("/createTask")
  public ResponseEntity<Object> createTask(@RequestParam Integer id, @RequestParam String peerId,
      @RequestParam String name, @RequestParam String path, @RequestParam String url) {
    return new ResponseEntity<>(wkcUserService.createTask(id, peerId, path, name, url), HttpStatus.OK);
  }

}