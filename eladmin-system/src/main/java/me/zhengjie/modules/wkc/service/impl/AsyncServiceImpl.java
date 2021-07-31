package me.zhengjie.modules.wkc.service.impl;

import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.AsyncService;
import me.zhengjie.modules.wkc.service.WkcJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements AsyncService {

  @Autowired
  WkcJobService wkcJobService;
  @Override
  @Async
  public WkcJob createJob( Integer wkcUserId, String type, String url) {
    return wkcJobService.createJob(wkcUserId,type,url,null);
  }
}
