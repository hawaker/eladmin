package me.zhengjie.modules.wkc.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class GasDto {
    @JsonProperty("fastGas")
    private String fastGas;
    @JsonProperty("normalGas")
    private String normalGas;
    @JsonProperty("isFree")
    private Integer isFree;
}
