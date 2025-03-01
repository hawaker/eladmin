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
package me.zhengjie.modules.wkc.repository;

import java.util.List;
import me.zhengjie.modules.wkc.domain.WkcJob;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
* @website https://el-admin.vip
* @author caoqingyuan
* @date 2021-07-12
**/
public interface WkcJobRepository extends JpaRepository<WkcJob, Integer>, JpaSpecificationExecutor<WkcJob> {

  List<WkcJob> queryByStatus(Integer status);

  List<WkcJob> queryByUserIdAndTypeAndUrl(Long userId,String type,String url);

  @Query("select e from WkcJob e where e.status=:status")
  List<WkcJob> queryByStatusLimit(@Param("status") Integer status, Pageable pageable);
}