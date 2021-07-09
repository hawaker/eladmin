package me.zhengjie.modules.wkc.dto;


public abstract class AbstractSuccessAble implements SuccessAble {
    public abstract Integer getState();

    public Boolean success() {
        return null == getState() ?
                false :
                getState().equals(0) ? true : false;
    }
}
