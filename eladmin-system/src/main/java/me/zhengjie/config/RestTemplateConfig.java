package me.zhengjie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Description:
 *
 * @date:2021/7/31 11:58 下午
 * @author: caoqingyuan
 */
@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate getRestTemplate(){
    SimpleClientHttpRequestFactory clientHttpRequestFactory=new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(5000);
    clientHttpRequestFactory.setReadTimeout(5000);
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    return restTemplate;
  }
}
