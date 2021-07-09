package me.zhengjie.modules.wkc.dto.control;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DeviceDto {
    @JsonProperty("imported")
    private Integer imported;
    @JsonProperty("status")
    private String status;
    @JsonProperty("features")
    private FeaturesDto features;
    @JsonProperty("ban_flag")
    private Object banFlag;
    @JsonProperty("direct_lvm")
    private String directLvm;
    @JsonProperty("system_version")
    private String systemVersion;
    @JsonProperty("lan_ip")
    private String lanIp;
    @JsonProperty("selltag")
    private String selltag;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("account_name")
    private String accountName;
    @JsonProperty("peerid")
    private String peerId;
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("disconnect_time")
    private int disconnectTime;
    @JsonProperty("device_sn")
    private String deviceSn;
    @JsonProperty("device_type")
    private String deviceType;
    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("lan_ipv6")
    private String lanIpv6;
    @JsonProperty("exception_name")
    private String exceptionName;
    @JsonProperty("area_code")
    private String areaCode;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("upgradeable")
    private Boolean upgradeable;
    @JsonProperty("licence")
    private String licence;
    @JsonProperty("coturn_online")
    private Integer coturnOnline;
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("is_exp")
    private Boolean isExp;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("connect_time")
    private String connectTime;
    @JsonProperty("hardware_model")
    private String hardwareModel;
    @JsonProperty("mac_address")
    private String macAddress;
    @JsonProperty("ip_info")
    private IpDto ipInfo;
    @JsonProperty("broker_id")
    private Integer brokerId;
    @JsonProperty("first_bind")
    private Integer firstBind;
    @JsonProperty("last_update_time")
    private Integer lastUpdateTime;
    @JsonProperty("bind_time")
    private Integer bindTime;
    @JsonProperty("exception_message")
    private String exceptionMessage;
}
