package me.zhengjie.modules.wkc.dto.control;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.modules.wkc.client.WanKeCloudUtil;
import me.zhengjie.modules.wkc.dto.AbstractSuccessAble;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ControlResponseDto extends AbstractSuccessAble {

    @JsonProperty("rtn")
    private Integer state;
    private List<Object> result;
    private String msg;

    public AppearanceDto getAppearence() {
        if (null == result) {
            return null;
        }
        if (result.size() <= 1) {
            return null;
        }
        Object obj = result.get(1);
        String objStr = WanKeCloudUtil.toJson(obj);
        return WanKeCloudUtil.readValueType(objStr, AppearanceDto.class);
    }
}
