package me.zhengjie.modules.wkc.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import me.zhengjie.modules.wkc.service.WkcTaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Description:
 *
 * @date:2021/11/17 23:02
 * @author: caoqingyuan
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class WkcTaskServiceImplTest {

  @Autowired
  WkcTaskService wkcTaskService;
  @Test
  void syncTask() {
    wkcTaskService.syncTask("1");
  }

  @Test
  void taskCheck() {
    wkcTaskService.taskCheck(1,30);
  }
}