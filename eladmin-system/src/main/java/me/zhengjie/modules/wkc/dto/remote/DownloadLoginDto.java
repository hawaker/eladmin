package me.zhengjie.modules.wkc.dto.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.modules.wkc.dto.AbstractSuccessAble;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DownloadLoginDto extends AbstractSuccessAble {

    @JsonProperty("clientVersion")
    private Integer clientVersion;
    @JsonProperty("rtn")
    private Integer state;
    @JsonProperty("pathList")
    private List<String> pathList;

}