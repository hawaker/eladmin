package me.zhengjie.modules.activiti.listener;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Description:
 *
 * @date:2021/7/27 6:39 下午
 * @author: caoqingyuan
 */
@Slf4j
public class TaskListenerImpl implements TaskListener {

  @Override
  public void notify(DelegateTask delegateTask) {
    log.info("notify,taskId:[{}]",delegateTask.getId());
  }
}
