package me.zhengjie.modules.wkc.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccountDto {
    @JsonProperty("fronzen_time")
    private Integer fronzenTime;
    @JsonProperty("bind_time")
    private Integer bindTime;
    @JsonProperty("balance")
    private String balance;
    @JsonProperty("isBindAddr")
    private Integer isBindAddr;
    @JsonProperty("addr")
    private String addr;
    @JsonProperty("gasInfo")
    private GasDto gasInfo;
    @JsonProperty("max_draw")
    private double maxDraw;
}
