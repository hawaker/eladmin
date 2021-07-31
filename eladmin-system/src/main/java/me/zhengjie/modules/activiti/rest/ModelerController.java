package me.zhengjie.modules.activiti.rest;

import com.alipay.api.domain.ModelQueryParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.activiti.dto.ModelQueryCriteria;
import me.zhengjie.utils.PageUtil;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activiti/model")
@Slf4j
public class ModelerController {

  @Autowired
  RepositoryService repositoryService;
  @Autowired
  ObjectMapper objectMapper;


  /**
   * 新建一个空模型
   *
   * @return
   * @throws UnsupportedEncodingException
   */
  @PostMapping("/create")
  public ResponseEntity newModel(@RequestBody LinkedHashMap<String,String> map) throws UnsupportedEncodingException {
    //初始化一个空模型
    Model model = repositoryService.newModel();

    String name=map.getOrDefault("name","new-process");
    String description=map.getOrDefault("description","");
    String key=map.getOrDefault("key","process");
    int revision = 1;

    ObjectNode modelNode = objectMapper.createObjectNode();
    modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
    modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
    modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

    model.setName(name);
    model.setKey(key);
    model.setMetaInfo(modelNode.toString());

    repositoryService.saveModel(model);
    String id = model.getId();

    //完善ModelEditorSource
    ObjectNode editorNode = objectMapper.createObjectNode();
    editorNode.put("id", "canvas");
    editorNode.put("resourceId", "canvas");
    ObjectNode stencilSetNode = objectMapper.createObjectNode();
    stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
    editorNode.put("stencilset", stencilSetNode);
    repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
    return  new ResponseEntity<>(id,HttpStatus.OK);
  }

  @GetMapping("/import")
  public Object importModel(@RequestParam String name, @RequestParam String path) throws Exception {
    Resource resource = new ClassPathResource(path);
    InputStream inputStream = resource.getInputStream();
    // 创建转换对象
    BpmnXMLConverter converter = new BpmnXMLConverter();

    // 创建XMLStreamReader读取XML资源
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

    // 把XML转换成BpmnModel对象

    BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
    BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    ObjectNode objectNode = bpmnJsonConverter.convertToJson(bpmnModel);

    //初始化一个空模型
    Model model = repositoryService.newModel();

    String description = "";
    int revision = 1;
    String key = "process";

    ObjectNode modelNode = objectMapper.createObjectNode();
    modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
    modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
    modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
    model.setName(name);
    model.setKey(key);
    model.setMetaInfo(modelNode.toString());

    repositoryService.saveModel(model);
    String id = model.getId();

    repositoryService.addModelEditorSource(id, objectNode.toString().getBytes("utf-8"));
    return  id;
  }


  /**
   * 发布模型为流程定义
   *
   * @param id
   * @return
   * @throws Exception
   */
  @PostMapping("{id}/deployment")
  public Object deploy(@PathVariable("id") String id) throws Exception {

    //获取模型
    Model modelData = repositoryService.getModel(id);
    byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

    if (bytes == null) {
      return false;
    }
    JsonNode modelNode = new ObjectMapper().readTree(bytes);

    BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
    if (model.getProcesses().size() == 0) {
      return false;
    }
    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

    //发布流程
    String processName = modelData.getName() + ".bpmn20.xml";
    Deployment deployment = repositoryService.createDeployment()
        .name(modelData.getName())
        .addString(processName, new String(bpmnBytes, "UTF-8"))
        .deploy();
    modelData.setDeploymentId(deployment.getId());
    repositoryService.saveModel(modelData);
    return true;
  }

  @GetMapping("/{id}")
  public Object getOne(@PathVariable("id") String id) {
    Model model = repositoryService.createModelQuery().modelId(id).singleResult();
    return model;
  }

  @GetMapping
  public Object query(ModelQueryCriteria criteria, Pageable pageable) {
    ModelQuery modelQuery=repositoryService.createModelQuery();
    if (criteria.getId()!=null){
      modelQuery.modelId(criteria.getId());
    }
    if (criteria.getName()!=null){
      modelQuery.modelName(criteria.getName());
    }
    long count = modelQuery.count();
    List<Model> list = modelQuery
        .listPage(pageable.getPageSize() * pageable.getPageNumber(), pageable.getPageSize());
    return new ResponseEntity<>(PageUtil.toPage(list,count),HttpStatus.OK);
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity deleteOne(@PathVariable("id") String id) {
    repositoryService.deleteModel(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}