package me.zhengjie.modules.activiti.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.activiti.util.BeanUtil;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/api/activiti/process")
public class ProcessController extends BaseController {

  @Autowired
  ObjectMapper objectMapper;

  /**
   * 功能描述:classpath部署流程
   *
   * @param name     流程名称
   * @param resource 流程资源名称 到.bpmn
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/deploy")
  @ResponseBody
  public ResponseEntity<Object> deploy(@RequestParam String name, @RequestParam String resource) {
    // 创建一个部署对象
    Deployment deploy = repositoryService.createDeployment().name(name)
        .addClasspathResource("processes/" + resource).deploy();
//    System.out.println("部署成功:" + deploy.getId());
//    System.out.println("部署成功:" + deploy.getName());
    return new ResponseEntity<>(deploy, HttpStatus.OK);
  }

  /**
   * 功能描述:zip部署流程
   *
   * @param name 流程名称
   * @param zip  流程文件名称
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/deploy/zip")
  @ResponseBody
  public ResponseEntity<Object> deployZip(@RequestParam String name, @RequestParam String zip) {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream("processes/" + zip);
    ZipInputStream zipInputStream = new ZipInputStream(in);
    Deployment deployment = repositoryService// 与流程定义和部署对象相关的Service
        .createDeployment()// 创建一个部署对象
        .name(name)// 添加部署名称
        .addZipInputStream(zipInputStream)// 完成zip文件的部署
        .deploy();// 完成部署
//    System.out.println("部署ID：" + deployment.getId());
//    System.out.println("部署名称:" + deployment.getName());
    return new ResponseEntity<>(deployment, HttpStatus.OK);
  }

  /**
   * 功能描述:查询流程定义
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/definition/query")
  @ResponseBody
  public ResponseEntity<Object> processDefinition() {

    List<ProcessDefinition> list = repositoryService// 与流程定义和部署对象相关的Service
        .createProcessDefinitionQuery()// 创建一个流程定义查询
        /* 指定查询条件,where条件 */
        // .deploymentId(deploymentId)//使用部署对象ID查询
        // .processDefinitionId(processDefinitionId)//使用流程定义ID查询
        // .processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
        // .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

        /* 排序 */
        .orderByProcessDefinitionVersion().asc()// 按照版本的升序排列
        // .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

        .list();// 返回一个集合列表，封装流程定义
    // .singleResult();//返回唯一结果集
    // .count();//返回结果集数量
    // .listPage(firstResult, maxResults)//分页查询

    if (CollectionUtils.isEmpty(list)) {
      return new ResponseEntity<>(new Integer[]{},HttpStatus.OK);
    }
//    for (ProcessDefinition processDefinition : list) {
//      System.out.println("流程定义ID:" + processDefinition.getId());// 流程定义的key+版本+随机生成数
//      System.out
//          .println("流程定义名称:" + processDefinition.getName());// 对应HelloWorld.bpmn文件中的name属性值
//      System.out.println("流程定义的key:" + processDefinition.getKey());// 对应HelloWorld.bpmn文件中的id属性值
//      System.out
//          .println("流程定义的版本:" + processDefinition.getVersion());// 当流程定义的key值相同的情况下，版本升级，默认从1开始
//      System.out.println("资源名称bpmn文件:" + processDefinition.getResourceName());
//      System.out.println("资源名称png文件:" + processDefinition.getDiagramResourceName());
//      System.out.println("部署对象ID:" + processDefinition.getDeploymentId());
//    }
    return new ResponseEntity<>(BeanUtil.beanListToMapList(list),HttpStatus.OK);
  }


  /**
   * 功能描述:删除流程定义
   *
   * @param deploymentId 流程Id
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/deployment/delete")
  @ResponseBody
  public ResponseEntity<Object> deleteDeployment(@RequestParam String deploymentId) {

    try {
//			// 不带级联的删除：只能删除没有启动的流程，如果流程启动，则抛出异常
//			repositoryService.deleteDeployment(deploymentId);
      // 能级联的删除：能删除启动的流程，会删除和当前规则相关的所有信息，正在执行的信息，也包括历史信息
      repositoryService.deleteDeployment(deploymentId, true);
//      System.out.println("删除成功:" + deploymentId);
    } catch (Exception e) {
      return new ResponseEntity<>(false,HttpStatus.OK);
    }
    return new ResponseEntity<>(true,HttpStatus.OK);
  }

  /**
   * 功能描述:启动流程
   *
   * @param processId 流程Id
   * @param variable  变量
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/start")
  @ResponseBody
  public ResponseEntity<Object> start(@RequestParam String processId,
      @RequestParam(required = false) String variable) {
    try {
      Map<String, Object> variables = new HashMap<>();
      if (!StringUtils.isEmpty(variable)) {
//        variables = BeanUtil.readValue(variable, new TypeReference<Map<String, Object>>() {
//        });
      }

      ProcessInstance instance = runtimeService.startProcessInstanceByKey(processId, variables);
//			// Businesskey:业务标识，通常为业务表的主键，业务标识和流程实例一一对应。业务标识来源于业务系统。存储业务标识就是根据业务标识来关联查询业务系统的数据
//			ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey,
//					variables);

//      System.out.println("流程实例ID:" + instance.getId());
//      System.out.println("流程定义ID:" + instance.getProcessDefinitionId());
      return new ResponseEntity<>(BeanUtil.beanToMapByName(instance),HttpStatus.OK);
    } catch (Exception e) {
      log.error("fail", e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }


  /**
   * 功能描述:删除流程
   *
   * @param processInstanceId 流程ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/delete")
  @ResponseBody
  public ResponseEntity<Object> deleteProcess(@RequestParam String processInstanceId) {

    try {
      runtimeService.deleteProcessInstance(processInstanceId, "流程已完毕");
//      System.out.println("终止流程");
    } catch (Exception e) {
      log.error("fail", e);
      return new ResponseEntity<>(false,HttpStatus.OK);
    }
    return new ResponseEntity<>(true,HttpStatus.OK);
  }

  /**
   * 功能描述:流程实例挂起
   *
   * @param processInstanceId 实例ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/instance/suspend")
  @ResponseBody
  public ResponseEntity<Object> suspendProcessInstance(@RequestParam String processInstanceId) {
    // 根据一个流程实例的id挂起该流程实例
    runtimeService.suspendProcessInstanceById(processInstanceId);
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
//    System.out.println("流程实例ID:" + processInstance.getId());
//    System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());
//    System.out.println("流程实例状态:" + processInstance.isSuspended());
    return new ResponseEntity<>(BeanUtil.beanToMapByName(processInstance),HttpStatus.OK);
  }

  /**
   * 功能描述:流程定义挂起
   *
   * @param processDefinitionId def
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/definition/suspend")
  @ResponseBody
  public ResponseEntity<Object> suspendProcessDefinition(@RequestParam String processDefinitionId) {
    // 根据一个流程定义的id挂起该流程实例
    repositoryService.suspendProcessDefinitionByKey(processDefinitionId);
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey(processDefinitionId).singleResult();
//    System.out.println("流程定义ID:" + processDefinition.getId());
//    System.out.println("流程定义状态:" + processDefinition.isSuspended());
    return new ResponseEntity<>(processDefinition,HttpStatus.OK);
  }

  /**
   * 功能描述:流程实例激活
   *
   * @param processInstanceId 实例ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/instance/activate")
  @ResponseBody
  public ResponseEntity<Object> activateProcessInstance(@RequestParam String processInstanceId) {
    // 根据一个流程实例id激活该流程
    runtimeService.activateProcessInstanceById(processInstanceId);
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
//    System.out.println("流程实例ID:" + processInstance.getId());
//    System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());
//    System.out.println("流程实例状态:" + processInstance.isSuspended());
    return new ResponseEntity<>(BeanUtil.beanToMapByName(processInstance),HttpStatus.OK);

  }

  /**
   * 功能描述:流程定义激活
   *
   * @param processDefinitionId 定义ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @RequestMapping(value = "/definition/activate")
  @ResponseBody
  public ResponseEntity<Object> activateProcessDefinition(@RequestParam String processDefinitionId) {
    // 根据一个流程定义的id挂起该流程实例
    repositoryService.activateProcessDefinitionById(processDefinitionId);
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey(processDefinitionId).singleResult();
//    System.out.println("流程定义ID:" + processDefinition.getId());
//    System.out.println("流程定义状态:" + processDefinition.isSuspended());
    return new ResponseEntity<>(processDefinition,HttpStatus.OK);
  }


  @RequestMapping(value = "/image.svg", method = RequestMethod.GET)
  public void image(HttpServletResponse response,
      @RequestParam String processInstanceId) {
    try {
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
    } catch (Exception ex) {
      log.error("查看流程图失败", ex);
    }
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