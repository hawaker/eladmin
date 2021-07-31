package me.zhengjie.modules.activiti.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.activiti.dto.InstanceQueryCriteria;
import me.zhengjie.modules.activiti.util.BeanUtil;
import me.zhengjie.utils.PageUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @date:2021/7/18 6:04 下午
 * @author: caoqingyuan
 */
@RestController
@RequestMapping("/api/activiti/instance")
@Slf4j
public class InstanceController extends BaseController {


  /**
   * 功能描述:启动流程
   *
   * @param processDefinitionId 流程Id
   * @param variable            变量
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "/start")
  @ResponseBody
  public ResponseEntity<Object> start(@RequestParam String processDefinitionId,
      @RequestBody(required = false) LinkedHashMap<String, Object> variable) {
    ProcessInstance instance = runtimeService
        .startProcessInstanceById(processDefinitionId, variable);
    return new ResponseEntity<>(BeanUtil.beanToMapByName(instance), HttpStatus.OK);
  }


  /**
   * 功能描述:删除流程
   *
   * @param processInstanceId 流程ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @DeleteMapping(value = "{processInstanceId}/delete")
  @ResponseBody
  public ResponseEntity<Object> deleteProcess(@PathVariable String processInstanceId) {
    runtimeService.deleteProcessInstance(processInstanceId, "流程已完毕");
    return new ResponseEntity<>(true, HttpStatus.OK);
  }


  /**
   * 功能描述:流程实例挂起
   *
   * @param processInstanceId 实例ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{processInstanceId}/suspend")
  @ResponseBody
  public ResponseEntity<Object> suspendProcessInstance(@PathVariable String processInstanceId) {
    // 根据一个流程实例的id挂起该流程实例
    runtimeService.suspendProcessInstanceById(processInstanceId);
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    return new ResponseEntity<>(BeanUtil.beanToMapByName(processInstance), HttpStatus.OK);
  }

  /**
   * 功能描述:流程实例激活
   *
   * @param processInstanceId 实例ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{processInstanceId}/activate")
  @ResponseBody
  public ResponseEntity<Object> activateProcessInstance(@PathVariable String processInstanceId) {
    // 根据一个流程实例id激活该流程
    runtimeService.activateProcessInstanceById(processInstanceId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<Object> query(InstanceQueryCriteria criteria, Pageable pageable) {
    ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
    long count = query.count();
    List<ProcessInstance> processInstances = query
        .listPage(pageable.getPageSize() * pageable.getPageNumber(), pageable.getPageSize());
    return new ResponseEntity<>(PageUtil.toPage(
        BeanUtil.beanListToMapList(processInstances), count), HttpStatus.OK);
  }


  @GetMapping(value = "/image.svg")
  public void image(HttpServletResponse response, @RequestParam String processInstanceId) {
    log.info("查看完整流程图！流程实例ID:{}", processInstanceId);
    // 根据流程对象获取流程对象模型
    HistoricProcessInstance historicProcessInstance = historyService
        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    BpmnModel bpmnModel = repositoryService
        .getBpmnModel(historicProcessInstance.getProcessDefinitionId());
    // 获取流程中已经执行的节点，按照执行先后顺序排序
    List<HistoricActivityInstance> historicActivityInstances = historyService
        .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
        .orderByHistoricActivityInstanceId().asc().list();
    // 高亮已经执行流程节点ID集合
    List<String> highLightedFlowsIds = getHighLightedFlows(bpmnModel, historicActivityInstances);
    List<String> highLightedActivitiIds = new ArrayList<>();
    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
      highLightedActivitiIds.add(historicActivityInstance.getActivityId());
    }
    // 查询历史节点
    if (CollectionUtils.isEmpty(highLightedActivitiIds)) {
      log.info("流程实例ID:{}没有历史节点信息！", processInstanceId);
      outPutImg(response, bpmnModel, null, null);
      return;
    }
    outPutImg(response, bpmnModel, highLightedFlowsIds, highLightedActivitiIds);
  }


  private void outPutImg(HttpServletResponse response, BpmnModel bpmnModel, List flowIds,
      List executedActivityIdList) {
    InputStream imageStream = null;
    try {
      if (null == flowIds && null == executedActivityIdList) {
        imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "宋体", "宋体", "黑体");
      } else {
        imageStream = processDiagramGenerator
            .generateDiagram(bpmnModel, executedActivityIdList, flowIds,
                "宋体", "微软雅黑", "黑体", true);
      }
      response.setContentType("image/svg+xml");
// 输出资源内容到相应对象
      byte[] b = new byte[1024];
      int len;
      while ((len = imageStream.read(b, 0, 1024)) != -1) {
        response.getOutputStream().write(b, 0, len);
      }
      response.getOutputStream().flush();
    } catch (Exception e) {
      log.error("流程图输出异常！", e);
    } finally { // 流关闭
      if (null != imageStream) {
        try {
          imageStream.close();
        } catch (IOException e) {
          log.error("流程图输入流关闭异常！", e);
          imageStream = null;
        }
      }
    }
  }

  /**
   * 获取已经流转的线
   *
   * @param bpmnModel
   * @param historicActivityInstances
   * @return
   */
  private static List<String> getHighLightedFlows(BpmnModel bpmnModel,
      List<HistoricActivityInstance> historicActivityInstances) {
    // 高亮流程已发生流转的线id集合
    List<String> highLightedFlowIds = new ArrayList<>();
    // 全部活动节点
    List<FlowNode> historicActivityNodes = new ArrayList<>();
    // 已完成的历史活动节点
    List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();
    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
      FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess()
          .getFlowElement(historicActivityInstance.getActivityId(), true);
      historicActivityNodes.add(flowNode);
      if (historicActivityInstance.getEndTime() != null) {
        finishedActivityInstances.add(historicActivityInstance);
      }
    }
    FlowNode currentFlowNode = null;
    FlowNode targetFlowNode = null;
    // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
    for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
      // 获得当前活动对应的节点信息及outgoingFlows信息
      currentFlowNode = (FlowNode) bpmnModel.getMainProcess()
          .getFlowElement(currentActivityInstance.getActivityId(), true);
      List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();
      /**
       * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转： 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
       */
      if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway"
          .equals(currentActivityInstance.getActivityType())) {
        // 遍历历史活动节点，找到匹配流程目标节点的
        for (SequenceFlow sequenceFlow : sequenceFlows) {
          targetFlowNode = (FlowNode) bpmnModel.getMainProcess()
              .getFlowElement(sequenceFlow.getTargetRef(), true);
          if (historicActivityNodes.contains(targetFlowNode)) {
            highLightedFlowIds.add(targetFlowNode.getId());
          }
        }
      } else {
        List<Map<String, Object>> tempMapList = new ArrayList<>();
        for (SequenceFlow sequenceFlow : sequenceFlows) {
          for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
              Map<String, Object> map = new HashMap<>();
              map.put("highLightedFlowId", sequenceFlow.getId());
              map.put("highLightedFlowStartTime",
                  historicActivityInstance.getStartTime().getTime());
              tempMapList.add(map);
            }
          }
        }
        if (!CollectionUtils.isEmpty(tempMapList)) {
          // 遍历匹配的集合，取得开始时间最早的一个
          long earliestStamp = 0L;
          String highLightedFlowId = null;
          for (Map<String, Object> map : tempMapList) {
            long highLightedFlowStartTime = Long
                .valueOf(map.get("highLightedFlowStartTime").toString());
            if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
              highLightedFlowId = map.get("highLightedFlowId").toString();
              earliestStamp = highLightedFlowStartTime;
            }
          }
          highLightedFlowIds.add(highLightedFlowId);
        }
      }
    }
    return highLightedFlowIds;
  }
}
