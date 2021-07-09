package me.zhengjie.modules.wkc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResult implements SuccessAble{
    private Integer statusCode;
    private String message;
    private Object data;

    public Boolean getSuccess() {
        return this.statusCode.equals(200);
    }

    public static RestResult successResult(Object obj) {

        return new RestResult(200, null, obj);
    }

    public static RestResult errorResult(Integer statusCode, String message) {
        return new RestResult(statusCode, message, null);

    }

    @Override
    public Boolean success() {
        return this.getSuccess();
    }
}
