package me.zhengjie.modules.wkc.dto.control;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PartitionDto {
    @JsonProperty("label")
    private String label;
    @JsonProperty("capacity")
    private Long capacity;
    @JsonProperty("unique")
    private Integer unique;
    @JsonProperty("disk_sn")
    private Integer diskSn;
    @JsonProperty("part_symbol")
    private String partSymbol;
    @JsonProperty("fs_type")
    private String fsType;
    @JsonProperty("type")
    private String type;
    @JsonProperty("path")
    private String path;
    @JsonProperty("part_label")
    private String partLabel;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("disk_id")
    private Integer diskId;
    @JsonProperty("used")
    private Long used;
}
