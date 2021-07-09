package me.zhengjie.modules.wkc.client;

import static org.junit.jupiter.api.Assertions.*;

import me.zhengjie.modules.wkc.dto.account.AccountResponseDto;
import me.zhengjie.modules.wkc.dto.account.UserDto;
import org.junit.jupiter.api.Test;

class WanKeCloudServiceTest {

  public WanKeCloudService getClient(){
    WanKeCloudService wanKeCloudService = new WanKeCloudService();
    wanKeCloudService.initService();
    return wanKeCloudService;
  }

  @Test
  void checkRegister() {
    getClient().checkRegister("17600776604");
  }


  @Test
  void login(){
    getClient().login("17600776604","b13frdely");
    //{"sMsg":"Success","data":{"nickname":"17600776604","sessionid":"cs001.49AE16F428F8C8B0B7EB8B96C215376F","account_type":"4","enable_homeshare":1,"userid":"1626853709","phone":"17600776604","bind_pwd":"1","phone_area":"86"},"iRet":0}
  }

  @Test
  void restart(){
    getClient().restartDevice("cs001.49AE16F428F8C8B0B7EB8B96C215376F","1626853709");
  }


}