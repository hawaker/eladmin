package me.zhengjie.modules.wkc.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.WkcJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @date:2021/8/1 12:45 上午
 * @author: caoqingyuan
 */
@Component
@Slf4j
public class JobExecutor {
  @Autowired
  WkcJobService wkcJobService;
  @Autowired
  JobTypeProxyService jobTypeProxyService;

  @Scheduled(cron = "0/20 * * * * ?")
  public void handleJob() {
    log.info("检查定时任务[job],开始");
    List<WkcJob> jobs = wkcJobService.queryByStatus(0);
    log.info("检查定时任务[job],size:[{}]",jobs.size());
    jobs.forEach(job -> {
      jobTypeProxyService.handle(job);
    });
    log.info("检查定时任务[job],结束");
  }
}
