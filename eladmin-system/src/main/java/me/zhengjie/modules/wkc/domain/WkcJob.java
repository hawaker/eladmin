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
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author caoqingyuan
* @date 2021-07-12
**/
@Entity
@Data
@Table(name="wkc_job")
public class WkcJob implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "type")
    @ApiModelProperty(value = "任务类型")
    private String type;

    @Column(name = "url")
    @ApiModelProperty(value = "网址")
    private String url;

    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "user_id")
    @ApiModelProperty(value = "归属用户")
    private Long userId;

    @Column(name = "wkc_user_id")
    @ApiModelProperty(value = "归属玩客云用户")
    private Integer wkcUserId;

    @Column(name = "parent_id")
    @ApiModelProperty("异常信息")
    private Integer parentId;

    @Column(name = "exception_msg")
    @ApiModelProperty("异常信息")
    private String exceptionMsg;

    @Column(name = "status")
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("名称")
    private String name;

    public void copy(WkcJob source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}