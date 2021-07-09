package me.zhengjie.modules.wkc.dto.control;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AppearanceDto {

    @JsonProperty("global_appearance")
    private Integer globalAppearance;
    private List<DeviceDto> devices;
    private List<PartitionDto> partitions;
}
