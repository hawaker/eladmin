package me.zhengjie.modules.wkc.typs;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 *
 * @date:2021/11/17 23:43
 * @author: caoqingyuan
 */
@AllArgsConstructor
@Getter
public enum WkcTaskStateEnum {
  done(11, "下载完成"),
  error(12,"错误"),
  downloading(0,"下载中"),
  suspend(9,"暂停中"),
  waiting(8,"排队中"),
  deprecated(14,"作废"),
  ;
  private int id;
  private String desc;
}
