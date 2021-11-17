package me.zhengjie.modules.wkc.typs;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 *
 * @date:2021/11/17 23:44
 * @author: caoqingyuan
 */
@AllArgsConstructor
@Getter
public enum WkcTaskErrorCodeEnum {
  noResource(114011, "资源缺失,请重试其他链接"),
  ;
  private Integer id;
  private String desc;

}
