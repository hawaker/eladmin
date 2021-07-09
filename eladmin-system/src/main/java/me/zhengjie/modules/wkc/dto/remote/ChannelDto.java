package me.zhengjie.modules.wkc.dto.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChannelDto {
    @JsonProperty("dlBytes")
    private Integer dlBytes;
    @JsonProperty("available")
    private Integer available;
    @JsonProperty("dlSize")
    private String dlSize;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("failCode")
    private Integer failCode;
    @JsonProperty("speed")
    private Integer speed;
    @JsonProperty("serverSpeed")
    private Integer serverSpeed;
    @JsonProperty("serverProgress")
    private Integer serverProgress;
    @JsonProperty("opened")
    private Integer opened;
    @JsonProperty("type")
    private Integer type;
}