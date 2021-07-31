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
* @date 2021-06-21
**/
@Entity
@Data
@Table(name="wkc_user")
public class WkcUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "phone",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "手机号")
    private String phone;

    @Column(name = "user_id")
    @ApiModelProperty(value = "关联用户")
    private String userId;

    @Column(name = "password",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "密码")
    private String password;

    @Column(name = "token")
    @ApiModelProperty(value = "玩客云token")
    private String token;

    @Column(name = "account_type")
    @ApiModelProperty(value = "accountType")
    private Integer accountType;

    @Column(name = "enable_home_share")
    @ApiModelProperty(value = "enableHomeShare")
    private Integer enableHomeShare;

    @Column(name = "bind_pwd")
    @ApiModelProperty(value = "bindPwd")
    private Integer bindPwd;

    @Column(name = "phone_area")
    @ApiModelProperty(value = "phoneArea")
    private String phoneArea;

    @Column(name = "bind_user")
    @ApiModelProperty(value = "归属用户")
    private Long bindUser;

    @Column
    @ApiModelProperty(value = "默认设备ID")
    private String defaultDeviceId;

    @Column
    @ApiModelProperty(value = "默认USB设备UUID")
    private String defaultUsbUuid;

    @Column
    @ApiModelProperty(value = "默认USB设备存储路径")
    private String defaultUsbPath;


    @Column
    @ApiModelProperty(value = "默认节点ID")
    private String defaultPeerId;

    public void copy(WkcUser source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}