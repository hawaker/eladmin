package me.zhengjie.modules.wkc.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;


import java.util.LinkedHashMap;
import me.zhengjie.modules.wkc.dto.remote.DownloadListDto;
import me.zhengjie.modules.wkc.dto.remote.DownloadLoginDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;

public interface WanKeCloudRemoteDownloadClient {
    @RequestLine("GET /login?appversion={appVersion}&ct={ct}&pid={pid}&v={v}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    DownloadLoginDto login(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("v") String v,
            @Param("ct") String ct,
            @Param("appVersion") String appVersion
    );

    @RequestLine("GET /list?ct={ct}&needUrl={needUrl}&number={number}&pid={pid}&pos={position}&type={type}&v={v}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    DownloadListDto queryList(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("v") String v,
            @Param("ct") String ct,
            @Param("position") Integer position,
            @Param("number") Integer number,
            @Param("type") String type,
            @Param("needUrl") String needUrl
    );

    @RequestLine("POST /urlResolve?pid={pid}&ct={ct}&v={v}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    UrlResolveDto urlResolve(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("v") String v,
            @Param("ct") String ct,
            LinkedHashMap params
    );

    @RequestLine("POST /createTask?pid={pid}&v={v}&ct={ct}")
    @Headers({
            "Cookie:sessionid={sessionId}; userid={userId}",
            "Content-Type:application/json"
    })
    TaskActionDto createTask(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("v") String v,
            @Param("ct") String ct,
            LinkedHashMap params
    );

    @RequestLine("GET /start?pid={pid}&ct={ct}&v={v}&tasks={tasks}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    TaskActionDto start(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("tasks") String tasks,
            @Param("v") String v,
            @Param("ct") String ct
    );

    @RequestLine("GET /pause?pid={pid}&ct={ct}&v={v}&tasks={tasks}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    TaskActionDto pause(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("tasks") String tasks,
            @Param("v") String v,
            @Param("ct") String ct
    );

    @RequestLine("GET /del?pid={pid}&ct={ct}&v={v}&tasks={tasks}&deleteFile={deleteFile}&recycleTask={recycleTask}")
    @Headers({"Cookie:sessionid={sessionId}; userid={userId}"})
    TaskActionDto del(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("pid") String pid,
            @Param("tasks") String tasks,
            @Param("deleteFile") boolean deleteFile,
            @Param("recycleTask") boolean recycleTask,
            @Param("v") String v,
            @Param("ct") String ct
    );

}