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
package me.zhengjie.modules.wkc.service;

import me.zhengjie.modules.wkc.domain.WkcUser;
import me.zhengjie.modules.wkc.dto.account.AccountDto;
import me.zhengjie.modules.wkc.dto.account.AccountResponseDto;
import me.zhengjie.modules.wkc.dto.account.UserDto;
import me.zhengjie.modules.wkc.dto.control.ControlResponseDto;
import me.zhengjie.modules.wkc.dto.control.DeviceDto;
import me.zhengjie.modules.wkc.dto.control.PartitionDto;
import me.zhengjie.modules.wkc.dto.remote.DownloadListDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;
import me.zhengjie.modules.wkc.service.dto.WkcUserDto;
import me.zhengjie.modules.wkc.service.dto.WkcUserQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author hawaker
* @date 2021-06-21
**/
public interface WkcUserService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(WkcUserQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<WkcUserDto>
    */
    List<WkcUserDto> queryAll(WkcUserQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return WkcUserDto
     */
    WkcUserDto findById(Integer id);

    /**
    * 创建
    * @param resources /
    * @return WkcUserDto
    */
    WkcUserDto create(WkcUser resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(WkcUser resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Integer[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<WkcUserDto> all, HttpServletResponse response) throws IOException;

    /**
     * 用户登录
     *
     * @param wkcUserId
     */
    UserDto login(Integer wkcUserId);

    /**
     * 获取账户信息
     *
     * @param wkcUserId
     * @return
     */
    AccountDto getAccountInfo(Integer wkcUserId);

    /**
     * 获取节点信息
     *
     * @param wkcUserId
     * @return
     */
    List<DeviceDto> getPeerInfo(Integer wkcUserId);

    /**
     * 获取节点的存储信息
     *
     * @param wkcUserId
     * @param deviceId
     * @return
     */
    List<PartitionDto> getUSBInfo(Integer wkcUserId, String deviceId);

    /**
     * 设置用户默认的节点和USB设备
     *
     * @param wkcUserId 用户Id
     * @param deviceId  设备Id
     * @param uuid      usb的UUID
     * @param path      usb的存储路径
     */
    void setUserDefaultDevice(Integer wkcUserId, String deviceId,String peerId, String uuid, String path);

    /**
     * 获取用户任务
     *
     * @param wkcUserId 用户Id
     * @param peerId 节点Id
     * @param position
     * @param number
     * @return
     */
    DownloadListDto queryUserTasks(Integer wkcUserId, String peerId,Integer position, Integer number);

    /**
     * 暂停一个任务
     *
     * @param wkcUserId
     * @param peerId
     * @param taskId
     * @return
     */
    TaskActionDto pauseTask(Integer wkcUserId, String peerId, String taskId);

    /**
     * 开始一个任务
     *
     * @param wkcUserId
     * @param peerId
     * @param taskId
     * @return
     */
    TaskActionDto startTask(Integer wkcUserId, String peerId, String taskId);


    /**
     * 删除一个任务
     *
     * @param wkcUserId
     * @param peerId
     * @param taskId
     * @param deleteFile 是否删除文件
     * @param recycleTask ??待测试
     * @return
     */
    TaskActionDto delTask(Integer wkcUserId, String peerId, String taskId, Boolean deleteFile, Boolean recycleTask);

    /**
     * 创建一个任务
     * @param wkcUserId
     * @param peerId
     * @param path
     * @param name
     * @param url
     * @return
     */
    TaskActionDto createTask(Integer wkcUserId, String peerId, String path, String name,String url);


    /**
     * 解析一个任务地址
     *
     * @param wkcUserId
     * @param peerId
     * @param url
     * @return
     */
    UrlResolveDto resolveUrl(Integer wkcUserId, String peerId,String url);
}