package me.zhengjie.modules.activiti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 *
 * @date:2021/7/17 5:33 下午
 * @author: caoqingyuan
 */
@Getter
@Setter
public class ModelSaveDto {
  private String name;
  private String description;
  @JsonProperty("json_xml")
  private String jsonXml;
  @JsonProperty("svg_xml")
  private String svgXml;
}
