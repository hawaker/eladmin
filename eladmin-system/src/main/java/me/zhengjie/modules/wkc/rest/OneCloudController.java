package me.zhengjie.modules.wkc.rest;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.wkc.domain.WkcUser;
import me.zhengjie.modules.wkc.service.AsyncService;
import me.zhengjie.modules.wkc.service.WkcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "wkcUser管理")
@RequestMapping("/oneCloud")
@Slf4j
public class OneCloudController {

  @Autowired
  WkcUserService wkcUserService;
  @Autowired
  RedisTemplate<String, String> redisTemplate;
  @Autowired
  AsyncService asyncService;


  @Log("创建任务")
  @ApiOperation("创建任务")
  @PostMapping(value = "/create")
  public ResponseEntity<Object> create(@RequestHeader String uuid,
      @RequestBody LinkedHashMap<String, Object> body) {
    String wkcUserId = redisTemplate.opsForValue().get(uuid);
    if (wkcUserId == null) {
      WkcUser wkcUser=wkcUserService.findByUuid(uuid);
      if (null!=wkcUser){
        wkcUserId=wkcUser.getId().toString();
        redisTemplate.opsForValue().set(uuid,wkcUserId, 1,TimeUnit.DAYS);
      }
    }
    if (wkcUserId == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Assert.notNull(body.get("type"), "type不能为空");
    Assert.notNull(body.get("url"), "url不能为空");
    String type=body.get("type").toString();
    String url=body.get("url").toString();
    Integer wkcUser=Integer.parseInt(wkcUserId);
    asyncService.createJob(wkcUser,type,url);
    return new ResponseEntity<>(wkcUser, HttpStatus.OK);
  }
}
