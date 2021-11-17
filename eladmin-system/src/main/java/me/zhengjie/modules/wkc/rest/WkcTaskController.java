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

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.wkc.domain.WkcTask;
import me.zhengjie.modules.wkc.service.WkcTaskService;
import me.zhengjie.modules.wkc.service.dto.WkcTaskQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author hawaker
* @date 2021-11-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "任务管理管理")
@RequestMapping("/api/wkcTask")
public class WkcTaskController {

    private final WkcTaskService wkcTaskService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wkcTask:list')")
    public void download(HttpServletResponse response, WkcTaskQueryCriteria criteria) throws IOException {
        wkcTaskService.download(wkcTaskService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询任务管理")
    @ApiOperation("查询任务管理")
    @PreAuthorize("@el.check('wkcTask:list')")
    public ResponseEntity<Object> query(WkcTaskQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wkcTaskService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增任务管理")
    @ApiOperation("新增任务管理")
    @PreAuthorize("@el.check('wkcTask:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WkcTask resources){
        return new ResponseEntity<>(wkcTaskService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改任务管理")
    @ApiOperation("修改任务管理")
    @PreAuthorize("@el.check('wkcTask:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WkcTask resources){
        wkcTaskService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除任务管理")
    @ApiOperation("删除任务管理")
    @PreAuthorize("@el.check('wkcTask:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
        wkcTaskService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}