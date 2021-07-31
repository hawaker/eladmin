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
package me.zhengjie.modules.wkc.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import lombok.Data;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author hawaker
* @date 2021-06-21
**/
@Data
public class WkcUserDto implements Serializable {

    private Integer id;

    /** 手机号 */
    private String phone;

    /** 关联用户 */
    private String userId;

    /** 密码 */
    private String password;

    /** 玩客云token */
    private String token;

    private Integer accountType;

    private Integer enableHomeShare;

    private Integer bindPwd;

    private Integer phoneArea;

    /** 归属用户 */
    private Long bindUser;

    private String defaultDeviceId;

    private String defaultUsbUuid;

    private String defaultUsbPath;

    private String defaultPeerId;
}