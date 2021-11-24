package me.zhengjie.modules.wkc.service.impl;

import static org.junit.jupiter.api.Assertions.*;

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
  @Test
  void handle() {
    WkcJob wkcJob=wkcJobRepository.getOne(7089);
    magnetJobTypeHandler.handle(wkcJob);
  }
}