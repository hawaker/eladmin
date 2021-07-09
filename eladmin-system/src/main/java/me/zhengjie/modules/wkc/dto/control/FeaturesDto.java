package me.zhengjie.modules.wkc.dto.control;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FeaturesDto {
    @JsonProperty("miner")
    private Integer miner;
    @JsonProperty("onecloud_coin")
    private Integer onecloudCoin;
}
