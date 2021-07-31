package me.zhengjie.modules.activiti.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 *
 * @date:2021/7/22 1:37 下午
 * @author: caoqingyuan
 */
@Getter
@Setter
public class TaskQueryCriteria {

  /**
   * 实例ID
   */
  private String processInstanceId;

  /**
   * 处理人
   */
  private String assignee;
}
