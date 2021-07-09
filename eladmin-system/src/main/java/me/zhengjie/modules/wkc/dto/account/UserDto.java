package me.zhengjie.modules.wkc.dto.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @JsonProperty("sessionid")
    private String sessionId;
    @JsonProperty("account_type")
    private Integer accountType;
    @JsonProperty("enable_homeshare")
    private Integer enableHomeShare;
    @JsonProperty("userid")
    private String userId;

    private String phone;
    @JsonProperty("bind_pwd")
    private String bindPwd;
    @JsonProperty("phone_area")
    private String phoneArea;
}