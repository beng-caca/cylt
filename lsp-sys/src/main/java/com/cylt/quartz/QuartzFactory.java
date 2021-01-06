package com.cylt.quartz;

import com.cylt.common.util.SpringUtil;
import com.cylt.pojo.sys.SysScheduleJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Method;
import java.util.Date;

public class QuartzFactory implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        //获取调度数据
        SysScheduleJob scheduleJob = (SysScheduleJob) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");

        //获取对应的Bean
        Object object = SpringUtil.getBean(scheduleJob.getBeanName());
        try {
            // 开始时间
            long start = new Date().getTime();
            System.out.println("-------------------Job4Log任务执行开始-------------------");
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(scheduleJob.getMethodName());
            String dd = (String) method.invoke(object);
            long ee = start - new Date().getTime();
            System.out.println(ee);
            System.out.println("-------------------Job4Log任务执行结束-------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
