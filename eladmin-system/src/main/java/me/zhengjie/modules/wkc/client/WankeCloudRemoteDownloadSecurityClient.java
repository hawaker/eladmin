package me.zhengjie.modules.wkc.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.LinkedHashMap;

import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;

/**
 * Description:
 *
 * @date:2021/11/30 18:33
 * @author: caoqingyuan
 */
public interface WankeCloudRemoteDownloadSecurityClient {

  @RequestLine("POST /createBatchTask?v=2&pid={pid}&ct=31&ct_ver=1.4.5.112")
  @Headers({
      "Cookie:sessionid={sessionId}; userid={userId}",
      "Content-Type:application/json"
  })
  TaskActionDto createBatchTask(
      @Param("sessionId") String sessionId,
      @Param("userId") String userId,
      @Param("pid") String pid,
      LinkedHashMap params
      );
}
