package me.zhengjie.modules.wkc.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.Resource;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.repository.WkcJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:
 *
 * @date:2021/11/24 16:05
 * @author: caoqingyuan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@Transactional
class MagnetJobTypeHandlerTest  {

  @Autowired
  MagnetJobTypeHandler magnetJobTypeHandler;
  @Autowired
  WkcJobRepository wkcJobRepository;

  @Resource
  BTJobTypeHandler btJobTypeHandler;
  @Test
  void handle() {
    WkcJob wkcJob=wkcJobRepository.getOne(7274);
    magnetJobTypeHandler.handle(wkcJob);
  }

  @Test
  void testHandle() {
    WkcJob wkcJob=new WkcJob();
    wkcJob.setUrl("http://mp4ppp.com/fs/Public/BB32251004F1422A9BE4384CEB74F004");
    btJobTypeHandler.handle(wkcJob);
  }
}