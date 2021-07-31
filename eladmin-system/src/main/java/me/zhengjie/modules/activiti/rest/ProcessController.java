package me.zhengjie.modules.activiti.rest;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.activiti.dto.ProcessQueryCriteria;
import me.zhengjie.modules.activiti.util.BeanUtil;
import me.zhengjie.utils.PageUtil;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/api/activiti/process")
public class ProcessController extends BaseController {



  /**
   * 功能描述:classpath部署流程
   *
   * @param name     流程名称
   * @param resource 流程资源名称 到.bpmn
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "/deploy")
  @ResponseBody
  public ResponseEntity<Object> deploy(@RequestParam String name, @RequestParam String resource) {
    // 创建一个部署对象
    Deployment deploy = repositoryService.createDeployment().name(name)
        .addClasspathResource("processes/" + resource).deploy();
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
  @PostMapping(value = "/deploy/zip")
  @ResponseBody
  public ResponseEntity<Object> deployZip(@RequestParam String name, @RequestParam String zip) {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream("processes/" + zip);
    ZipInputStream zipInputStream = new ZipInputStream(in);
    Deployment deployment = repositoryService// 与流程定义和部署对象相关的Service
        .createDeployment()// 创建一个部署对象
        .name(name)// 添加部署名称
        .addZipInputStream(zipInputStream)// 完成zip文件的部署
        .deploy();// 完成部署
    return new ResponseEntity<>(deployment, HttpStatus.OK);
  }

  /**
   * 功能描述:查询流程定义
   *
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<Object> query(ProcessQueryCriteria criteria, Pageable pageable) {
    ProcessDefinitionQuery processDefinitionQuery = repositoryService
        .createProcessDefinitionQuery();
    if (null != criteria && criteria.getName() != null) {
      processDefinitionQuery.processDefinitionKey(criteria.getName());
    }
    if (null != criteria && criteria.getActivited() != null && criteria.getActivited()) {
      // 只查激活的
      processDefinitionQuery.active();
    }
    // 创建一个流程定义查询
    /* 指定查询条件,where条件 */
    // .deploymentId(deploymentId)//使用部署对象ID查询
    // .processDefinitionId(processDefinitionId)//使用流程定义ID查询
    // .processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
    // .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询
    long count = processDefinitionQuery.count();
    /* 排序 */
    // .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列
    List<ProcessDefinition> list = processDefinitionQuery
        .orderByProcessDefinitionVersion()
        .asc()// 按照版本的升序排列
        .listPage(pageable.getPageSize() * pageable.getPageNumber(), pageable.getPageSize());
    // 返回一个集合列表，封装流程定义
    // .singleResult();//返回唯一结果集
    // .count();//返回结果集数量
    // .listPage(firstResult, maxResults)//分页查询

    if (CollectionUtils.isEmpty(list)) {
      return new ResponseEntity<>(new Integer[]{}, HttpStatus.OK);
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
    return new ResponseEntity<>(PageUtil.toPage(BeanUtil.beanListToMapList(list), count),
        HttpStatus.OK);
  }


  /**
   * 功能描述:删除流程定义
   *
   * @param deploymentId 流程Id
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @DeleteMapping(value = "{deploymentId}/delete")
  @ResponseBody
  public ResponseEntity<Object> deleteDeployment(@PathVariable String deploymentId) {
    try {
      //不带级联的删除：只能删除没有启动的流程，如果流程启动，则抛出异常
      //repositoryService.deleteDeployment(deploymentId);
      // 能级联的删除：能删除启动的流程，会删除和当前规则相关的所有信息，正在执行的信息，也包括历史信息
      repositoryService.deleteDeployment(deploymentId, true);
    } catch (Exception e) {
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(true, HttpStatus.OK);
  }


  /**
   * 功能描述:流程定义挂起
   *
   * @param processDefinitionId def
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{processDefinitionId}/suspend")
  @ResponseBody
  public ResponseEntity<Object> suspendProcessDefinition(@PathVariable String processDefinitionId) {
    // 根据一个流程定义的id挂起该流程实例
    repositoryService.suspendProcessDefinitionById(processDefinitionId);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  /**
   * 功能描述:流程定义激活
   *
   * @param processDefinitionId 定义ID
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  @PostMapping(value = "{processDefinitionId}/activate")
  @ResponseBody
  public ResponseEntity<Object> activateProcessDefinition(
      @PathVariable String processDefinitionId) {
    // 根据一个流程定义的id挂起该流程实例
    repositoryService.activateProcessDefinitionById(processDefinitionId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}