package me.zhengjie.modules.activiti.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.modules.activiti.dto.ModelSaveDto;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @date:2021/7/17 4:07 下午
 * @author: caoqingyuan
 */
@RestController
public class ModelEditorController {

  protected static final Logger log = LoggerFactory.getLogger(ModelEditorController.class);
  @Autowired
  private RepositoryService repositoryService;
  @Autowired
  private ObjectMapper objectMapper;

  @GetMapping("/model/{modelId}/json")
  public void getEditorJson(@PathVariable String modelId, HttpServletResponse servletResponse)
      throws IOException {
    ObjectNode modelNode = null;
    Model model = this.repositoryService.getModel(modelId);
    if (model != null) {
      try {
        if (StringUtils.isNotEmpty(model.getMetaInfo())) {
          modelNode = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());
        } else {
          modelNode = this.objectMapper.createObjectNode();
          modelNode.put("name", model.getName());
        }
        modelNode.put("modelId", model.getId());
        byte[] buf1 = this.repositoryService.getModelEditorSource(model.getId());
        String buf = new String(buf1, "utf-8");
        ObjectNode editorJsonNode = (ObjectNode) this.objectMapper.readTree(buf);
        modelNode.put("model", editorJsonNode);
      } catch (Exception var5) {
        log.error("Error creating model JSON", var5);
        throw new ActivitiException("Error creating model JSON", var5);
      }
    }
    servletResponse.setContentType("application/json; charset=UTF-8");
    servletResponse.getWriter().write(objectMapper.writeValueAsString(modelNode));
    servletResponse.flushBuffer();
    return;
  }

  @RequestMapping("/model/editor/stencilset")
  public void getStencilset(HttpServletResponse servletResponse) {
    InputStream stencilsetStream = this.getClass().getClassLoader()
        .getResourceAsStream("stencilset.json");
    try {
      servletResponse.setContentType("application/json; charset=UTF-8");
      servletResponse.getWriter().write(IOUtils.toString(stencilsetStream, "utf-8"));
      servletResponse.flushBuffer();
    } catch (Exception var3) {
      throw new ActivitiException("Error while loading stencil set", var3);
    }
  }


  @PutMapping("/model/{modelId}/save")
  public ResponseEntity saveModel(@PathVariable String modelId
      , @RequestBody ModelSaveDto modelSaveDto) throws JsonProcessingException {
    try {
      Model model = this.repositoryService.getModel(modelId);
      ObjectNode modelJson = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());
      modelJson.put("name", modelSaveDto.getName());
      modelJson.put("description", modelSaveDto.getDescription());
      model.setMetaInfo(modelJson.toString());
      model.setName(modelSaveDto.getName());
      this.repositoryService.saveModel(model);
      this.repositoryService
          .addModelEditorSource(model.getId(), (modelSaveDto.getJsonXml()).getBytes("utf-8"));
      InputStream svgStream = new ByteArrayInputStream(
          (modelSaveDto.getSvgXml()).getBytes("utf-8"));
      TranscoderInput input = new TranscoderInput(svgStream);
      PNGTranscoder transcoder = new PNGTranscoder();
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);
      transcoder.transcode(input, output);
      byte[] result = outStream.toByteArray();
      this.repositoryService.addModelEditorSourceExtra(model.getId(), result);
      outStream.close();
    } catch (Exception var11) {
      log.error("Error saving model", var11);
      throw new ActivitiException("Error saving model", var11);
    }
    return new ResponseEntity(HttpStatus.OK);
  }
}

