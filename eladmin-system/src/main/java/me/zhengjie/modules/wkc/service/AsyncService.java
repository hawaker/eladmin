package me.zhengjie.modules.wkc.service;

import me.zhengjie.modules.wkc.domain.WkcJob;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
public interface AsyncService {

  /**
   * 创建任务
   *
   * @param wkcUserId
   * @param type
   * @param url
   * @return
   */
  WkcJob createJob( Integer wkcUserId, String type, String url);
}
