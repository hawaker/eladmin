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

import lombok.Data;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author hawaker
* @date 2021-11-17
**/
@Data
public class WkcTaskDto implements Serializable {

    /** 主键 */
    private Integer id;

    /** 玩客云的任务ID */
    private String wkcId;

    /** 状态 */
    private Integer state;

    /** 地址 */
    private String url;

    /** 大小 */
    private String size;

    /** 创建时间 */
    private Integer createTime;

    private Integer failCode;

    private Integer speed;

    private Integer downTime;

    private Integer complateTime;

    private Integer type;

    private String name;

    private Integer progress;

    private Integer exist;

    private Integer remainTime;

    private Integer errorCount;
}