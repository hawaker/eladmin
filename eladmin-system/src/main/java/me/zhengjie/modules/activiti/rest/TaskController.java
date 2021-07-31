/**
 * Created by Jellyleo on 2019年12月16日 Copyright © 2019 jellyleo.com All rights reserved.
 */
package me.zhengjie.modules.activiti.rest;

import cn.hutool.core.lang.Assert;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.activiti.dto.TaskQueryCriteria;
import me.zhengjie.modules.activiti.util.BeanUtil;
import me.zhengjie.utils.PageUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/activiti/task")
@Slf4j
public class TaskController extends BaseController {

  /**
   * 功能描述:查询任务
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<Object> taskQuery(TaskQueryCriteria criteria, Pageable pageable) {
    TaskQuery taskQuery = taskService.createTaskQuery();
    if (null != criteria.getProcessInstanceId()) {
      taskQuery.processDefinitionId(criteria.getProcessInstanceId());
    }
    if (null != criteria.getAssignee()) {
      taskQuery.taskAssignee(criteria.getAssignee());
    }
    long count = taskQuery.count();
    List list = taskQuery
        .listPage(pageable.getPageSize() * pageable.getPageNumber(), pageable.getPageSize());
    return ok(PageUtil.toPage(BeanUtil.beanListToMapList(list), count));
  }


  /**
   * 功能描述:查询进行中任务
   *
   * @param processInstanceId 实例ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @GetMapping(value = "/{processInstanceId}/run")
  @ResponseBody
  public ResponseEntity<Object> getRun(@PathVariable String processInstanceId) {
    ProcessInstance process = runtimeService.createProcessInstanceQuery()// 获取查询对象
        .processInstanceId(processInstanceId)// 根据id查询流程实例
        .singleResult();// 获取查询结果,如果为空,说明这个流程已经执行完毕,否则,获取任务并执行
    return ok(BeanUtil.beanToMapByName(process));
  }


  /**
   * 功能描述:完成任务
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{taskId}/complete")
  @ResponseBody
  public ResponseEntity<Object> complete(@PathVariable String taskId,
      @RequestBody(required = false) LinkedHashMap variable) {
    // 设置流程参数（多）
    taskService.setVariables(taskId, variable);
    // 若是委托任务，请先解决委托任务
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    Assert.isTrue(!DelegationState.PENDING.equals(task.getDelegationState()),
        "任务已委托,请先解决委托任务");
    taskService.complete(taskId);
    return ok(BeanUtil.beanToMapByName(task));
  }

  /**
   * 功能描述:任务分配
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{taskId}/assignee")
  @ResponseBody
  public ResponseEntity<Object> assignee(@PathVariable String taskId,
      @RequestParam String assignee) {
    taskService.setAssignee(taskId, assignee);
    return ok();
  }

  /**
   * 功能描述:解决委托任务
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "/resolve")
  @ResponseBody
  public ResponseEntity<Object> resolve(@RequestParam String taskId,
      @RequestBody(required = false) LinkedHashMap variable) {
    // 设置流程参数（多）
    taskService.setVariables(taskId, variable);

    // 根据taskId提取任务
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    Assert.notNull(task.getOwner(), "处理人为空");
    Assert.isTrue(!task.getOwner().equals("null"), "处理人为null");

    DelegationState delegationState = task.getDelegationState();
    Assert.isTrue(DelegationState.RESOLVED != delegationState, "已经解决,请确认");
    Assert.isTrue(DelegationState.PENDING == delegationState, "没有处理!");
    // 如果是委托任务需要做处理
    taskService.resolveTask(taskId, variable);
    return ok();
  }

}
