package com.cylt.quartz;

import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.sys.dao.SysScheduleJobDao;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QuartzServiceImpl implements IQuartzService {

    /**
     * 调度器
     */
    @Resource
    private Scheduler scheduler;

    @Resource
    private SysScheduleJobDao sysScheduleJobDao;

    @Override
    public void timingTask() {
        //查询数据库是否存在需要定时的任务
        List<SysScheduleJob> scheduleJobs = sysScheduleJobDao.findByStatus(0);
        if (scheduleJobs != null) {
            scheduleJobs.forEach(this::addJob);
        }
    }

    @Override
    public void addJob(SysScheduleJob job) {
        try {
            //创建触发器
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName())
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .startNow()
                    .build();

            //创建任务
            JobDetail jobDetail = JobBuilder.newJob(QuartzFactory.class)
                    .withIdentity(job.getId())
                    .build();

            //传入调度的数据，在QuartzFactory中需要使用
            jobDetail.getJobDataMap().put("scheduleJob", job);

            //调度作业
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void operateJob(JobOperateEnum jobOperateEnum, SysScheduleJob job) throws SchedulerException {
        JobKey jobKey = new JobKey(job.getId());
        // 找到任务实例
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        // 如果当前实例是否存在
        if (jobDetail == null) {
            // 如果实例状态是启动状态就新建一个实例
            if (jobOperateEnum == JobOperateEnum.START) {
                addJob(job);
            }
            return;
        }
        switch (jobOperateEnum) {
            case START:
                // 这里实现原理并不是quartz的启动 ，而是换成了删除+添加
                //scheduler.resumeJob(jobKey);
                scheduler.deleteJob(jobKey);
                addJob(job);
                break;
            case PAUSE:
                scheduler.pauseJob(jobKey);
                break;
            case DELETE:
                scheduler.deleteJob(jobKey);
                break;
        }
    }

    @Override
    public void startAllJob() throws SchedulerException {
        scheduler.start();
    }

    @Override
    public void pauseAllJob() throws SchedulerException {
        scheduler.standby();
    }
}
