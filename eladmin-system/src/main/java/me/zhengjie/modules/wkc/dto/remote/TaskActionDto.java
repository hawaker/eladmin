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
public class TaskActionDto extends AbstractSuccessAble {
    @JsonProperty("rtn")
    private Integer state;
    @JsonProperty("tasks")
    private List<TaskDto> tasks;
}