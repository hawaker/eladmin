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
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.WkcUserService;
import me.zhengjie.modules.wkc.service.dto.WkcUserDto;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.wkc.repository.WkcJobRepository;
import me.zhengjie.modules.wkc.service.WkcJobService;
import me.zhengjie.modules.wkc.service.dto.WkcJobDto;
import me.zhengjie.modules.wkc.service.dto.WkcJobQueryCriteria;
import me.zhengjie.modules.wkc.service.mapstruct.WkcJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author caoqingyuan
* @date 2021-07-12
**/
@Service
@RequiredArgsConstructor
public class WkcJobServiceImpl implements WkcJobService {

    private final WkcJobRepository wkcJobRepository;
    private final WkcJobMapper wkcJobMapper;
    @Autowired
    WkcUserService wkcUserService;

    @Override
    public Map<String,Object> queryAll(WkcJobQueryCriteria criteria, Pageable pageable){
        Page<WkcJob> page = wkcJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wkcJobMapper::toDto));
    }

    @Override
    public List<WkcJobDto> queryAll(WkcJobQueryCriteria criteria){
        return wkcJobMapper.toDto(wkcJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WkcJobDto findById(Integer id) {
        WkcJob wkcJob = wkcJobRepository.findById(id).orElseGet(WkcJob::new);
        ValidationUtil.isNull(wkcJob.getId(),"WkcJob","id",id);
        return wkcJobMapper.toDto(wkcJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WkcJobDto create(WkcJob resources) {
        return wkcJobMapper.toDto(wkcJobRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WkcJob resources) {
        WkcJob wkcJob = wkcJobRepository.findById(resources.getId()).orElseGet(WkcJob::new);
        ValidationUtil.isNull( wkcJob.getId(),"WkcJob","id",resources.getId());
        wkcJob.copy(resources);
        wkcJobRepository.save(wkcJob);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            wkcJobRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WkcJobDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WkcJobDto wkcJob : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("任务类型", wkcJob.getType());
            map.put("网址", wkcJob.getUrl());
            map.put(" createTime",  wkcJob.getCreateTime());
            map.put("归属用户", wkcJob.getUserId());
            map.put("归属玩客云用户", wkcJob.getWkcUserId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public WkcJob createJob(Long userId, Integer wkcUserId, String type, String url,Integer parentId) {
        WkcJob wkcJob= new WkcJob();
        wkcJob.setUrl(url);
        wkcJob.setType(type);
        wkcJob.setWkcUserId(wkcUserId);
        wkcJob.setUserId(userId);
        wkcJob.setParentId(parentId);
        wkcJob.setStatus(0);
        wkcJobRepository.save(wkcJob);
        return wkcJob;
    }


    @Override
    public WkcJob createJob(Integer wkcUserId, String type, String url,Integer parentId) {
        WkcUserDto wkcUserDto=wkcUserService.findById(wkcUserId);
        Assert.notNull(wkcUserDto,"未找到用户:"+wkcUserId);
        return this.createJob(wkcUserDto.getBindUser(),wkcUserId,type,url,parentId);
    }

    @Override
    public List<WkcJob> queryByStatus(Integer status) {
        return wkcJobRepository.queryByStatus(status);
    }
}