package me.zhengjie.modules.wkc.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.modules.wkc.dto.AbstractSuccessAble;

@Getter
@Setter
public class AccountResponseDto<T> extends AbstractSuccessAble {
    @JsonProperty("sMsg")
    private String msg;
    private T data;
    @JsonProperty("iRet")
    private Integer state;
    //只有在检查是否注册的情况下有用
    @JsonProperty("register")
    private Integer register;
}