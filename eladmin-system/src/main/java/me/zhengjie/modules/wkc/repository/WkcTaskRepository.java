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
import me.zhengjie.modules.wkc.domain.WkcTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
* @website https://el-admin.vip
* @author hawaker
* @date 2021-11-17
**/
public interface WkcTaskRepository extends JpaRepository<WkcTask, Integer>, JpaSpecificationExecutor<WkcTask> {

  WkcTask getByWkcId(String wkcId);

  @Query(value = "select * from wkc_task where remote_delete !=1 and state not in :states",nativeQuery = true)
  List<WkcTask> findNotDeleteAndStateNotIn(@Param("states")List<Integer> states);

}