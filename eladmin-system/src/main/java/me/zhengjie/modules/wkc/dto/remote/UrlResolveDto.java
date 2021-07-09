package me.zhengjie.modules.wkc.dto.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.modules.wkc.dto.AbstractSuccessAble;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UrlResolveDto extends AbstractSuccessAble {
    @JsonProperty("infohash")
    private String infoHash;
    @JsonProperty("taskInfo")
    private TaskDto taskDto;
    @JsonProperty("rtn")
    private Integer state;
}
