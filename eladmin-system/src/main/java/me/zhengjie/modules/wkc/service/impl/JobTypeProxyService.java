package me.zhengjie.modules.wkc.service.impl;

import java.util.HashMap;
import java.util.Map;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.HandleJobType;
import me.zhengjie.modules.wkc.service.JobTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class JobTypeProxyService implements JobTypeHandler, ApplicationContextAware {
    public HashMap<String, JobTypeHandler> map = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(WkcJob job) {
        logger.info("开始处理任务[{}][{}]",job.getType(),job.getUrl());
        String jobType = job.getType();
        if (null == jobType || jobType.isEmpty()) {
            jobType = "magnet";
        }
        JobTypeHandler jobTypeHandler = map.get(jobType);
        if (null == jobTypeHandler) {
            logger.error("未找到{}的处理类", jobType);
            return;
        }
        jobTypeHandler.handle(job);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, JobTypeHandler> jobTypeHandlerMap = applicationContext.getBeansOfType(JobTypeHandler.class);
        jobTypeHandlerMap.forEach((k, v) -> {
            HandleJobType handleJobType = v.getClass().getAnnotation(HandleJobType.class);
            if (null == handleJobType) {
                return;
            }
            String type = handleJobType.value();
            if (null != type && !type.isEmpty()) {
                map.put(type, v);
            }
        });
    }
}