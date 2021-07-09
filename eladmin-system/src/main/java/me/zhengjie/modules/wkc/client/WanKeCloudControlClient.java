package me.zhengjie.modules.wkc.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.LinkedHashMap;
import me.zhengjie.modules.wkc.dto.control.ControlResponseDto;

public interface WanKeCloudControlClient {
    @RequestLine("GET /listPeer?X-LICENCE-PUB={xLicencePub}&appversion={appVersion}&ct={ct}&v={v}")
    @Headers({
            "Cookie:sessionid={sessionId}; userid={userId}"
    })
    ControlResponseDto queryPeers(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("v") String v,
            @Param("ct") String ct,
            @Param("xLicencePub") String xLicencePub,
            @Param("appVersion") String appVersion
    );

    @RequestLine("GET /getUSBInfo?X-LICENCE-PUB={xLicencePub}&appversion={appVersion}&ct={ct}&deviceid={deviceId}&v={v}")
    @Headers({
            "Cookie:sessionid={sessionId}; userid={userId}"
    })
    ControlResponseDto getUSBInfo(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            @Param("deviceId") String deviceId,
            @Param("v") String v,
            @Param("ct") String ct,
            @Param("xLicencePub") String xLicencePub,
            @Param("appVersion") String appVersion
    );

    @RequestLine("POST /wkb/draw")
    @Headers({
            "Cookie:sessionid={sessionId}; userid={userId}"
    })
    ControlResponseDto draw(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            LinkedHashMap map
    );

    @RequestLine("GET sysmgr??opt=mnt&func=reboot")
    @Headers({
        "Cookie:sessionid={sessionId}; userid={userId}"
    })
    void restart(
        @Param("sessionId") String sessionId,
        @Param("userId") String userId
    );
}