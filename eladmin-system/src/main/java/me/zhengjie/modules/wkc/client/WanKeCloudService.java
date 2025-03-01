package me.zhengjie.modules.wkc.client;

import cn.hutool.json.JSON;
import com.qiniu.util.Json;
import com.qiniu.util.StringMap;
import feign.Client;
import feign.Feign;
import feign.Retryer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import me.zhengjie.modules.wkc.dto.account.AccountDto;
import me.zhengjie.modules.wkc.dto.account.AccountResponseDto;
import me.zhengjie.modules.wkc.dto.account.IncomeHistoryDto;
import me.zhengjie.modules.wkc.dto.account.UserDto;
import me.zhengjie.modules.wkc.dto.control.AppearanceDto;
import me.zhengjie.modules.wkc.dto.control.ControlResponseDto;
import me.zhengjie.modules.wkc.dto.control.DeviceDto;
import me.zhengjie.modules.wkc.dto.remote.DownloadListDto;
import me.zhengjie.modules.wkc.dto.remote.DownloadLoginDto;
import me.zhengjie.modules.wkc.dto.remote.TaskActionDto;
import me.zhengjie.modules.wkc.dto.remote.TaskDto;
import me.zhengjie.modules.wkc.dto.remote.UrlResolveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;

@Service
public class WanKeCloudService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String APP_VERSION = "2.6.0";
    public WanKeCloudAccountClient wanKeCloudAccountClient;
    public WanKeCloudControlClient wanKeCloudControlClient;
    public WanKeCloudRemoteDownloadClient wanKeCloudRemoteDownloadClient;
    public WankeCloudRemoteDownloadSecurityClient wankeCloudRemoteDownloadSecurityClient;

    @PostConstruct
    public void initService() {
        FeginClientConfig feginClientConfig=new FeginClientConfig();
        try {
            Client client= feginClientConfig.getClient();
        this.wanKeCloudAccountClient = Feign.builder()
                .encoder(new WanKeCloudEncoder())
                .retryer(Retryer.NEVER_RETRY)
                .requestInterceptor(new WanKeCloudRequestInterceptor())
                .decoder(new WanKeCloudDecoder())
                .client(client)
                .target(WanKeCloudAccountClient.class, "https://account.onethingpcs.com");
        this.wanKeCloudControlClient = Feign.builder()
                .encoder(new WanKeCloudEncoder())
                .retryer(Retryer.NEVER_RETRY)
                .requestInterceptor(new WanKeCloudRequestInterceptor())
            .client(client)
                .decoder(new WanKeCloudDecoder())
                .target(WanKeCloudControlClient.class, "https://control.onethingpcs.com");
        this.wanKeCloudRemoteDownloadClient = Feign.builder()
                .encoder(new WanKeCloudEncoder())
                .retryer(Retryer.NEVER_RETRY)
            .client(client)
                .requestInterceptor(new WanKeCloudRequestInterceptor())
                .decoder(new WanKeCloudDecoder())
                .target(WanKeCloudRemoteDownloadClient.class, "http://control-remotedl.onethingpcs.com/");
        this.wankeCloudRemoteDownloadSecurityClient = Feign.builder()
            .encoder(new WanKeCloudEncoder())
            .retryer(Retryer.NEVER_RETRY)
            .requestInterceptor(new WanKeCloudRequestInterceptor())
            .client(client)
            .decoder(new WanKeCloudDecoder())
            .target(WankeCloudRemoteDownloadSecurityClient.class, "https://control-remotedl.onethingpcs.com/");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WanKeCloudService wanKeCloudService = new WanKeCloudService();
        wanKeCloudService.initService();

        AccountResponseDto<UserDto> user = wanKeCloudService.login("17600776604", "b13frdely");

        String sessionId = user.getData().getSessionId();
        String userId = user.getData().getUserId();
        //System.out.println(sessionId);
        //System.out.println(userId);
        //
        //AccountResponseDto<AccountDto> userInfo = wanKeCloudService.getAccountInfo(sessionId, userId);
        //AccountResponseDto<IncomeHistoryDto> history = wanKeCloudService.getIncomeHistory(sessionId, userId, 0);
        //ControlResponseDto controlResponseDto = wanKeCloudService.getPeerInfo(sessionId, userId);
        //AppearanceDto appearanceDto = controlResponseDto.getAppearence();
        //String deviceId = "";
        //String peerId = "";
        //for (DeviceDto device : appearanceDto.getDevices()) {
        //    deviceId = device.getDeviceId();
        //    peerId = device.getPeerId();
        //    break;
        //}
        //System.out.println(deviceId);
        //System.out.println(peerId);
        //ControlResponseDto usb = wanKeCloudService.getUSBInfo(sessionId, userId, deviceId);
        //AppearanceDto appearanceDto1 = usb.getAppearence();
        //
        //DownloadLoginDto remoteDownloadLogin = wanKeCloudService.remoteDownloadLogin(sessionId, userId, peerId);
        //
        //UrlResolveDto urlResolveDto = wanKeCloudService.urlResolve(sessionId, userId, peerId, "https://bitsdts.net/torrent/AAF43B6092953A93DC9DE0377CCF3603BA0D73CB");
        //System.out.println(Json.encode(urlResolveDto));
        //String sessionId="cs001.373976D0F6095C3A9C6B8D7D3F883421";
        //String userId="1626853709";
        String deviceId = "duzIPMzw3395";
        String peerId = "B0D59DE2E05A889X0030";
        //String buf="{\"infohash\":\"\",\"taskInfo\":{\"createTime\":0,\"dcdnChannel\":{\"dlBytes\":1234,\"available\":0,\"dlSize\":\"0\",\"state\":0,\"failCode\":0,\"speed\":0},\"state\":0,\"id\":\"0\",\"refer\":\"\",\"progress\":0,\"exist\":0,\"url\":\"https:\\/\\/bitsdts.net\\/torrent\\/AAF43B6092953A93DC9DE0377CCF3603BA0D73CB\",\"remainTime\":0,\"size\":\"0\",\"name\":\"AAF43B6092953A93DC9DE0377CCF3603BA0D73CB\",\"failCode\":0,\"lixianChannel\":{\"dlBytes\":1234,\"state\":0,\"serverSpeed\":0,\"dlSize\":\"0\",\"serverProgress\":0,\"failCode\":0,\"speed\":0},\"from\":0,\"subList\":[],\"path\":\"\\/app\\/system\\/miner.plugin-etm.ipk\\/tmp\\/\",\"speed\":0,\"downTime\":0,\"vipChannel\":{\"opened\":0,\"dlBytes\":1234,\"available\":0,\"dlSize\":\"0\",\"speed\":0,\"failCode\":0,\"type\":0},\"completeTime\":0,\"type\":1},\"rtn\":0}";
        //StringMap stringMap = Json.decode(buf);
        //System.out.println(Json.encode(stringMap.get("taskInfo")));
        //List<TaskDto> tasks = new ArrayList<>();
        //TaskDto taskDto = new TaskDto();
        //taskDto.setName(stringMap.get("taskInfo").getClass().getSimpleName());
        //taskDto.setUrl("ed2k://|file|%E9%BB%84%E7%9F%B3.Yellowstone.2018.S01E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.WEB.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|472873520|c273bf00703b45225f2056393d6de87f|h=yq4vc2vndh2fnqdiwnhnqapwh7xcvlrw|/");
        ////tasks.add(taskDto);
        //TaskActionDto TaskActionDto = wanKeCloudService.createTask(sessionId, userId, peerId, "/media/sda1/onecloud/tddownload", taskDto);
        DownloadListDto remoteDownloadLogin1 = wanKeCloudService.remoteDownloadList(sessionId, userId, peerId, 0, 10);
        System.out.println(Json.encode(Json.encode(remoteDownloadLogin1)));
        //String taskId = "";
        //List<TaskDto> taskDtos = remoteDownloadLogin1.getTasks();
        //for (TaskDto taskDto1 : taskDtos) {
        //    if (taskDto1.getName().contains("黄石")) {
        //        taskId = taskDto1.getId();
        //    }
        //}
        //TaskActionDto taskActionDto1 = wanKeCloudService.pause(sessionId, userId, peerId, taskId + "_0", "1", "31");
        //TaskActionDto taskActionDto2 = wanKeCloudService.start(sessionId, userId, peerId, taskId + "_9", "1", "31");
        //TaskActionDto taskActionDto3 = wanKeCloudService.del(sessionId, userId, peerId, taskId + "_9", true, false, "1", "31");
        //System.out.println("ss");
    }

    /**
     * 登录
     *
     * @param phone
     * @param pwd
     * @return
     */
    public AccountResponseDto<UserDto> login(String phone, String pwd) {
        return login(phone, pwd, "4", WanKeCloudUtil.getDeviceId(phone), WanKeCloudUtil.getImeiId(phone), APP_VERSION);
    }

    /**
     * 登录
     *
     * @param phone 手机号
     * @param pwd 密码
     * @param accountType 账户类型
     * @param deviceId 设备ID
     * @param imeiId IMEIID
     * @param appVersion
     * @return
     */
    public AccountResponseDto<UserDto> login(String phone, String pwd, String accountType, String deviceId, String imeiId, String appVersion) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("account_type", accountType);
        params.put("deviceid", deviceId);
        params.put("imeiid", imeiId);
        params.put("phone", phone);
        params.put("pwd", WanKeCloudUtil.getSignPassword(pwd));
        params.put("key", "");
        return wanKeCloudAccountClient.login(appVersion, params);
    }

    /**
     * 检查是否注册
     *
     * @param phone 手机号
     * @param appVersion 版本
     * @return
     */
    public AccountResponseDto<UserDto> checkRegister(String phone,String appVersion){
        LinkedHashMap<String,Object> params=new LinkedHashMap<>();
        params.put("phone",phone);
        return wanKeCloudAccountClient.checkRegister(appVersion,params);
    }


    public void restartDevice(String sessionId,String userId){
        wanKeCloudControlClient.restart(sessionId,userId);
    }

    /**
     * 获取账户信息
     *
     * @param sessionId
     * @param userId
     * @param appVersion
     * @return
     */
    public AccountResponseDto<AccountDto> getAccountInfo(String sessionId, String userId, String appVersion) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("appversion", appVersion);
        params.put("key", sessionId);
        return wanKeCloudAccountClient.getAccountInfo(sessionId, userId, params);
    }

    /**
     * 获取账户信息
     *
     * @param sessionId
     * @param userId
     * @return
     */
    public AccountResponseDto<AccountDto> getAccountInfo(String sessionId, String userId) {
        return getAccountInfo(sessionId, userId, APP_VERSION);
    }

    /**
     * 获取挖矿记录
     *
     * @param sessionId
     * @param userId
     * @param page
     * @param appVersion
     * @return
     */
    public AccountResponseDto<IncomeHistoryDto> getIncomeHistory(String sessionId, String userId, Integer page, String appVersion) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("appversion", appVersion);
        params.put("page", page);
        params.put("key", sessionId);
        return wanKeCloudAccountClient.getIncomeHistory(sessionId, userId, params);
    }

    /**
     * 获取挖矿记录
     *
     * @param sessionId
     * @param userId
     * @param page
     * @return
     */
    public AccountResponseDto<IncomeHistoryDto> getIncomeHistory(String sessionId, String userId, Integer page) {
        return this.getIncomeHistory(sessionId, userId, page, APP_VERSION);
    }

    /**
     * 获取节点信息
     *
     * @param sessionId
     * @param userId
     * @param v
     * @param ct
     * @param xLicencePub
     * @param appVersion
     * @return
     */
    public ControlResponseDto getPeerInfo(String sessionId, String userId, String v, String ct, String xLicencePub, String appVersion) {
        return wanKeCloudControlClient.queryPeers(sessionId, userId, v, ct, xLicencePub, appVersion);
    }

    /**
     * 获取节点信息
     *
     * @param sessionId
     * @param userId
     * @return
     */
    public ControlResponseDto getPeerInfo(String sessionId, String userId) {
        return this.getPeerInfo(sessionId, userId, "2", "9", "1", APP_VERSION);
    }

    /**
     * 获取设备的USB信息
     *
     * @param sessionId
     * @param userId
     * @param deviceId
     * @param v
     * @param ct
     * @param xLicencePub
     * @param appVersion
     * @return
     */
    public ControlResponseDto getUSBInfo(String sessionId, String userId, String deviceId, String v, String ct, String xLicencePub, String appVersion) {
        return wanKeCloudControlClient.getUSBInfo(sessionId, userId, deviceId, v, ct, xLicencePub, appVersion);
    }

    /**
     * 获取设备的USB信息
     *
     * @param sessionId
     * @param userId
     * @param deviceId
     * @return
     */
    public ControlResponseDto getUSBInfo(String sessionId, String userId, String deviceId) {
        return this.getUSBInfo(sessionId, userId, deviceId, "2", "9", "1", APP_VERSION);
    }

    /**
     * 没有测试,慎用,是否可用未知
     * ControlResponseDto draw= wanKeCloudService.draw(sessionId,userId,"2",appVersion);
     */
    @Deprecated
    public ControlResponseDto draw(String sessionId, String userId, String gasType, String appVersion) {
        String[] s = new String[]{"appversion=" + appVersion, "gasType=" + gasType};
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("appversion", appVersion);
        params.put("gasType", gasType);
        return wanKeCloudControlClient.draw(sessionId, userId, params);
    }


    /**
     * 远程管理下载登录
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param v
     * @param ct
     * @param appVersion
     * @return
     */
    public DownloadLoginDto remoteDownloadLogin(String sessionId, String userId, String peerId, String v, String ct, String appVersion) {
        return this.wanKeCloudRemoteDownloadClient.login(sessionId, userId, peerId, v, ct, appVersion);
    }

    /**
     * 远程管理下载登录
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @return
     */
    public DownloadLoginDto remoteDownloadLogin(String sessionId, String userId, String peerId) {
        return this.remoteDownloadLogin(sessionId, userId, peerId, "1", "32", APP_VERSION);
    }

    /**
     * 检查手机号是否注册
     *
     * @param phone 手机号
     * @return
     */
    public AccountResponseDto<UserDto> checkRegister(String phone){
        return checkRegister(phone,APP_VERSION);
    }


    /**
     * 下载任务列表
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param position
     * @param number
     * @param type
     * @param needUrl
     * @param v
     * @param ct
     * @return
     */
    public DownloadListDto remoteDownloadList(String sessionId, String userId, String peerId, Integer position, Integer number, String type, String needUrl, String v, String ct) {
        return this.wanKeCloudRemoteDownloadClient.queryList(sessionId, userId, peerId, v, ct, position, number, type, needUrl);
    }

    /**
     * 下载任务列表
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param position
     * @param number
     * @return
     */
    public DownloadListDto remoteDownloadList(String sessionId, String userId, String peerId, Integer position, Integer number) {
        return this.remoteDownloadList(sessionId, userId, peerId, position, number, "4", "1", "2", "31");
    }

    /**
     * 解析下载链接
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param url
     * @param v
     * @param ct
     * @return
     */
    public UrlResolveDto urlResolve(String sessionId, String userId, String peerId, String url, String v, String ct) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("url", url);
        return this.wanKeCloudRemoteDownloadClient.urlResolve(sessionId, userId, peerId, v, ct, params);
    }

    /**
     * 解析下载链接
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param url
     * @return
     */
    public UrlResolveDto urlResolve(String sessionId, String userId, String peerId, String url) {
        return this.urlResolve(sessionId, userId, peerId, url, "1", "31");
    }

    ///**
    // * 批量创建下载任务
    // *
    // * @param sessionId
    // * @param userId
    // * @param peerId
    // * @param path
    // * @param tasks
    // * @param v
    // * @param ct
    // * @return
    // */
    //public TaskActionDto createTasks(String sessionId, String userId, String peerId, String path, List<TaskDto> tasks, String v, String ct) {
    //    LinkedHashMap<String, Object> params = new LinkedHashMap<>();
    //    params.put("path", path);
    //    params.put("tasks", tasks);
    //    return this.wanKeCloudRemoteDownloadClient.createTask(sessionId, userId, peerId, v, ct, params);
    //}

    public TaskActionDto createBatchTasks(String sessionId, String userId, String peerId, String path, List<TaskDto> tasks){
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("path", path);
        params.put("tasks", tasks);
        return this.wankeCloudRemoteDownloadSecurityClient.createBatchTask(sessionId,userId,peerId,params);
    }

    /**
     * 创建一个下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param path
     * @param task
     * @return
     */
    public TaskActionDto createTask(String sessionId, String userId, String peerId, String path, TaskDto task) {
        List<TaskDto> taskDtos = new ArrayList<>();
        taskDtos.add(task);
        return this.createTask(sessionId, userId, peerId, path, taskDtos);
    }


    /**
     * 批量创建下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param path
     * @param tasks
     * @return
     */
    public TaskActionDto createTask(String sessionId, String userId, String peerId, String path, List<TaskDto> tasks) {
        return this.createBatchTasks(sessionId,userId,peerId,path,tasks);
    }


    /**
     * 恢复一个被暂停的下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param tasks
     * @param v
     * @param ct
     * @return
     */
    public TaskActionDto start(String sessionId, String userId, String peerId, String tasks, String v, String ct) {
        return this.wanKeCloudRemoteDownloadClient.start(sessionId, userId, peerId, tasks, v, ct);
    }

    /**
     * 恢复一个被暂停的下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param taskId
     * @return
     */
    public TaskActionDto start(String sessionId, String userId, String peerId, String taskId) {
        return this.start(sessionId, userId, peerId, taskId + "_9", "1", "31");
    }

    /**
     * 暂停一个下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param tasks
     * @param v
     * @param ct
     * @return
     */
    public TaskActionDto pause(String sessionId, String userId, String peerId, String tasks, String v, String ct) {
        return this.wanKeCloudRemoteDownloadClient.pause(sessionId, userId, peerId, tasks, v, ct);
    }

    /**
     * 暂停一个下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param taskId
     * @return
     */
    public TaskActionDto pause(String sessionId, String userId, String peerId, String taskId) {
        return this.pause(sessionId, userId, peerId, taskId + "_0", "1", "31");
    }

    /**
     * 删除一个下载任务
     *
     * @param sessionId   session
     * @param userId      用户
     * @param peerId      节点
     * @param tasks       {任务ID}_9 例如 任务ID是123,那么这里就是123_9
     * @param deleteFile  是否删除硬盘上的文件
     * @param recycleTask 回收任务
     * @param v           不知道干嘛的貌似固定值
     * @param ct          不知道干嘛的貌似固定值
     * @return
     */
    public TaskActionDto del(String sessionId, String userId, String peerId, String tasks, boolean deleteFile, boolean recycleTask, String v, String ct) {
        return this.wanKeCloudRemoteDownloadClient.del(sessionId, userId, peerId, tasks, deleteFile, recycleTask, v, ct);
    }

    /**
     * 删除一个下载任务
     *
     * @param sessionId
     * @param userId
     * @param peerId
     * @param taskId
     * @param deleteFile
     * @param recycleTask
     * @return
     */
    public TaskActionDto del(String sessionId, String userId, String peerId, String taskId, boolean deleteFile, boolean recycleTask) {
        return this.del(sessionId, userId, peerId, taskId + "_9", deleteFile, recycleTask, "1", "31");
    }

}