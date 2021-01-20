package com.cylt.quartz;

import com.cylt.common.util.DateUtils;
import com.cylt.common.util.SpringUtil;
import com.cylt.pojo.sys.SysJobLog;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.redis.RedisUtil;
import com.cylt.sys.dao.SysJobLogDao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;

public class QuartzFactory implements Job {

    /**
     * 手动注入jpa持久层与redis类
     */
    private SysJobLogDao sysJobLogDao = (SysJobLogDao) SpringUtil.getBean("sysJobLogDao");
    private RedisUtil redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");

    private static Logger logger = LoggerFactory.getLogger(QuartzFactory.class);

    /**
     * 实现任务工厂 所有任务都通过此工厂调用
     * @param jobExecutionContext 任务实例
     * @throws JobExecutionException JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取调度数据
        SysScheduleJob scheduleJob = (SysScheduleJob) jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        //获取对应的Bean
        Object object = SpringUtil.getBean(scheduleJob.getBeanName());
        // 开始时间
        Date start = new Date();

        // 初始化log参数
        SysJobLog log = new SysJobLog();
        log.setJobId(scheduleJob.getId());
        log.setStartDate(start);
        try {
            logger.info(MessageFormat.format("-------------------{0}任务执行开始-------------------",
                    scheduleJob.getJobName()));
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(scheduleJob.getMethodName());
            String text = (String) method.invoke(object);
            logger.info(MessageFormat.format("-------------------{0}任务执行结束-------------------",
                    scheduleJob.getJobName()));
            // 状态
            log.setState("3");
            // 运行用时
            log.setTimeUse(DateUtils.minus(new Date(), start));
            log.setText(text);
            sysJobLogDao.save(log);
            redisUtil.save(log, 0 , null);
        } catch (Exception e) {
            // 状态
            log.setState("0");
            // 运行用时
            log.setTimeUse( DateUtils.minus(new Date(), start));
            log.setText(e.getMessage());
            sysJobLogDao.save(log);
            try {
                redisUtil.save(log, 0, null);
            } catch (Exception es) {
                logger.error("redis保存失败");
            }
            e.printStackTrace();
        }
    }
}
