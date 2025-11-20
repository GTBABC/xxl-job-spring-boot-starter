package com.gtbabc.xxljob.service;

import com.gtbabc.xxljob.annotation.XxlJobAutoRegister;
import com.gtbabc.xxljob.enums.ScheduleTypeEnum;
import com.gtbabc.xxljob.model.XxlJobGroup;
import com.gtbabc.xxljob.model.XxlJobInfo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * xxl-job 自动注册服务
 */
@Service
@Slf4j
public class XxlJobAutoRegisterService implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private XxlJobGroupService xxlJobGroupService;

    @Autowired
    private XxlJobInfoService xxlJobInfoService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            //注册执行器
            autoRegisterJobGroup();
            //注册任务
            autoRegisterJobInfo();
        } catch (Exception e) {
            log.error("xxl-job auto register error", e);
        }
    }

    private void autoRegisterJobInfo() {
        XxlJobGroup xxlJobGroup = xxlJobGroupService.getXxlJobGroup();
        if (xxlJobGroup == null) {
            log.error("xxl-job auto register job info error, job group is null");
            return;
        }

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            // 获取标注了 @XxlJob 注解的方法
            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));

            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                // 自动注册
                if (executeMethod.isAnnotationPresent(XxlJobAutoRegister.class)) {
                    XxlJobAutoRegister xxlJobAutoRegister = executeMethod.getAnnotation(XxlJobAutoRegister.class);

                    // 判断是否已存在的 Job 信息
                    if (xxlJobInfoService.isExistXxlJobInfo(xxlJobGroup.getId(), xxlJob.value())) {
                        continue;
                    }

                    // 创建新的 XxlJobInfo
                    XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlJobAutoRegister);

                    // 添加新的 Job 信息
                    xxlJobInfoService.autoRegisterXxlJobInfo(xxlJobInfo);
                }
            }
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlJobAutoRegister xxlJobAutoRegister) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        // 基础配置
        xxlJobInfo.setJobGroup(xxlJobGroup.getId());
        xxlJobInfo.setJobDesc(xxlJobAutoRegister.jobDesc());
        xxlJobInfo.setAuthor(xxlJobAutoRegister.author());
        xxlJobInfo.setAlarmEmail(xxlJobAutoRegister.alarmEmail());

        // 调度配置
        if (StringUtils.hasText(xxlJobAutoRegister.cron())) {
            xxlJobInfo.setScheduleType(ScheduleTypeEnum.CRON.getValue());
            xxlJobInfo.setScheduleConf(xxlJobAutoRegister.cron());
        } else {
            xxlJobInfo.setScheduleType(ScheduleTypeEnum.FIX_RATE.getValue());
            // 时间
            xxlJobInfo.setScheduleConf(String.valueOf(TimeUnit.SECONDS.convert(xxlJobAutoRegister.scheduleConf(), xxlJobAutoRegister.timeUnit())));
        }

        // 任务配置
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlJob.value());
        xxlJobInfo.setExecutorParam(xxlJobAutoRegister.executorParam());

        // 高级配置
        xxlJobInfo.setExecutorRouteStrategy(xxlJobAutoRegister.executorRouteStrategy());
        xxlJobInfo.setChildJobId(xxlJobAutoRegister.childJobId());
        xxlJobInfo.setMisfireStrategy(xxlJobAutoRegister.misfireStrategy());
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(xxlJobAutoRegister.executorTimeout());
        xxlJobInfo.setExecutorFailRetryCount(xxlJobAutoRegister.executorFailRetryCount());

        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setTriggerStatus(xxlJobAutoRegister.triggerStatus());
        return xxlJobInfo;
    }

    private void autoRegisterJobGroup() {
        if (xxlJobGroupService.isExistXxlJobGroup()) {
            return;
        }
        xxlJobGroupService.autoRegisterXxlJobGroup();
    }
}
