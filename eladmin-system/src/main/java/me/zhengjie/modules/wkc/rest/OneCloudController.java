package me.zhengjie.modules.wkc.rest;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.wkc.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  RedisTemplate<String, String> redisTemplate;
  @Autowired
  AsyncService asyncService;

  @Log("检查是否需要重新登录")
  @ApiOperation("检查是否需要重新登录")
  @GetMapping(value = "/check")
  public ResponseEntity<Object> check(@RequestHeader String token) {
    String wkcUserId = redisTemplate.opsForValue().get(token);
    if (wkcUserId == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(wkcUserId, HttpStatus.OK);
  }


  @Log("创建任务")
  @ApiOperation("创建任务")
  @PostMapping(value = "/create")
  public ResponseEntity<Object> create(@RequestHeader String token,
      @RequestBody LinkedHashMap<String, Object> body) {
    String wkcUserId = redisTemplate.opsForValue().get(token);
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
