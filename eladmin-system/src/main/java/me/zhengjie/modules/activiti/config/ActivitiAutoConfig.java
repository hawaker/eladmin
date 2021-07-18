package me.zhengjie.modules.activiti.config;

import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @date:2021/7/14 7:20 下午
 * @author: caoqingyuan
 */
@Configuration
public class ActivitiAutoConfig {

  @Bean
  public ProcessDiagramGenerator getProcessDiagramGenerator(){
    return new DefaultProcessDiagramGenerator();
  }
}
