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
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.WkcJobService;
import me.zhengjie.modules.wkc.service.dto.WkcJobQueryCriteria;
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
* @author caoqingyuan
* @date 2021-07-12
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "wkc/job管理")
@RequestMapping("/api/wkcJob")
public class WkcJobController {

    private final WkcJobService wkcJobService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wkcJob:list')")
    public void download(HttpServletResponse response, WkcJobQueryCriteria criteria) throws IOException {
        wkcJobService.download(wkcJobService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询wkc/job")
    @ApiOperation("查询wkc/job")
    @PreAuthorize("@el.check('wkcJob:list')")
    public ResponseEntity<Object> query(WkcJobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wkcJobService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增wkc/job")
    @ApiOperation("新增wkc/job")
    @PreAuthorize("@el.check('wkcJob:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WkcJob resources){
        return new ResponseEntity<>(wkcJobService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改wkc/job")
    @ApiOperation("修改wkc/job")
    @PreAuthorize("@el.check('wkcJob:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WkcJob resources){
        wkcJobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除wkc/job")
    @ApiOperation("删除wkc/job")
    @PreAuthorize("@el.check('wkcJob:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
        wkcJobService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}