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
package me.zhengjie.modules.wkc.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author hawaker
* @date 2021-11-17
**/
@Entity
@Data
@Table(name="wkc_task")
public class WkcTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Integer id;

    @Column(name = "wkc_id")
    @ApiModelProperty(value = "玩客云的任务ID")
    private String wkcId;

    @Column(name = "state")
    @ApiModelProperty(value = "状态")
    private Integer state;

    @Column(name = "url")
    @ApiModelProperty(value = "地址")
    private String url;

    @Column(name = "size")
    @ApiModelProperty(value = "大小")
    private String size;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Integer createTime;

    @Column(name = "fail_code")
    @ApiModelProperty(value = "failCode")
    private Integer failCode;

    @Column(name = "speed")
    @ApiModelProperty(value = "speed")
    private Integer speed;

    @Column(name = "down_time")
    @ApiModelProperty(value = "downTime")
    private Integer downTime;

    @Column(name = "complate_time")
    @ApiModelProperty(value = "complateTime")
    private Integer complateTime;

    @Column(name = "type")
    @ApiModelProperty(value = "type")
    private Integer type;

    @Column(name = "name")
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(name = "progress")
    @ApiModelProperty(value = "万分比")
    private Integer progress;

    @Column(name = "exist")
    @ApiModelProperty(value = "文件是否存在")
    private Boolean exist;

    @Column(name = "remain_time")
    @ApiModelProperty(value = "remainTime")
    private Integer remainTime;

    @Column(name = "error_count")
    @ApiModelProperty(value = "错误次数")
    private Integer errorCount;

    @ApiModelProperty(value = "wkcUserId")
    private Integer wkcUserId;

    @ApiModelProperty(value = "远端是否删除")
    private Boolean remoteDelete;

    private Integer lastSyncTime;

    public void copy(WkcTask source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}