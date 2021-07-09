package me.zhengjie.modules.wkc.client;


import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.LinkedHashMap;
import me.zhengjie.modules.wkc.dto.account.AccountDto;
import me.zhengjie.modules.wkc.dto.account.AccountResponseDto;
import me.zhengjie.modules.wkc.dto.account.IncomeHistoryDto;
import me.zhengjie.modules.wkc.dto.account.UserDto;


public interface WanKeCloudAccountClient {
    @RequestLine("POST /user/login?appversion={appVersion}")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    AccountResponseDto<UserDto> login(
            @Param("appVersion") String appVersion,
            LinkedHashMap params
    );

    @RequestLine("POST /user/check?appversion={appVersion}")
    @Headers({
        "Content-Type: application/x-www-form-urlencoded"
    })
    AccountResponseDto<UserDto> checkRegister(
        @Param("appVersion") String appVersion,
        LinkedHashMap params
    );


    @RequestLine("POST /wkb/account-info")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Cookie:sessionid={sessionId}; userid={userId}"
    })
    AccountResponseDto<AccountDto> getAccountInfo(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            LinkedHashMap params
    );

    @RequestLine("POST /wkb/income-history")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Cookie:sessionid={sessionId}; userid={userId}"
    })
    AccountResponseDto<IncomeHistoryDto> getIncomeHistory(
            @Param("sessionId") String sessionId,
            @Param("userId") String userId,
            LinkedHashMap params
    );

}