package me.zhengjie.modules.wkc.dto.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    @JsonProperty("createTime")
    private Integer createTime;
    @JsonProperty("dcdnChannel")
    private ChannelDto dcdnChannel;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("id")
    private String id;
    @JsonProperty("refer")
    private String refer;
    @JsonProperty("progress")
    private Integer progress;
    @JsonProperty("exist")
    private Integer exist;
    @JsonProperty("url")
    private String url;
    @JsonProperty("remainTime")
    private Integer remainTime;
    @JsonProperty("size")
    private Long size;
    @JsonProperty("name")
    private String name;
    @JsonProperty("failCode")
    private Integer failCode;
    @JsonProperty("lixianChannel")
    private ChannelDto lixianChannel;
    @JsonProperty("from")
    private Integer from;
    @JsonProperty("path")
    private String path;
    @JsonProperty("speed")
    private Integer speed;
    @JsonProperty("downTime")
    private Integer downTime;
    @JsonProperty("vipChannel")
    private ChannelDto vipChannel;
    @JsonProperty("completeTime")
    private Integer completeTime;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("subList")
    private List<FileDto> subList;
    @JsonProperty("filesize")
    private Long fileSize;
    private String msg;
    @JsonProperty("taskid")
    private String taskId;
    private String result;
}