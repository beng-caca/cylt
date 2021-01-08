package com.cylt.sys.service;

import com.cylt.pojo.sys.SysJobLog;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.quartz.IQuartzService;
import com.cylt.quartz.JobOperateEnum;
import com.cylt.redis.RedisUtil;
import com.cylt.sys.dao.SysJobLogDao;
import com.cylt.sys.dao.SysScheduleJobDao;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 字典service
 */
@Service("sysScheduleJobService")
public class SysScheduleJobService {

    @Resource
    private SysScheduleJobDao sysScheduleJobDao;

    @Resource
    private SysJobLogDao sysJobLogDao;


    @Resource
    private IQuartzService quartzService;

    @Resource
    public RedisUtil redisUtil;
    /**
     * 保存
     * @param scheduleJob 保存参数
     */
    public void save(SysScheduleJob scheduleJob) throws SchedulerException {
        sysScheduleJobDao.save(scheduleJob);
        // 遍历并找到当前状态的枚举类型
        for (JobOperateEnum enumm : JobOperateEnum.values()) {
            if (scheduleJob.getStatus()  == enumm.getValue()) {
                quartzService.operateJob(enumm, scheduleJob);
            }
        }
    }



    /**
     * 删除
     * @param scheduleJob 删除参数
     */
    public void delete(SysScheduleJob scheduleJob) throws Exception {
        // 删除该任务
        quartzService.operateJob(JobOperateEnum.DELETE, scheduleJob);
        // 删除日志log
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobId(scheduleJob.getId());
        redisUtil.del(jobLog);
        sysJobLogDao.deleteByJobId(scheduleJob.getId());
        // 删除持久数据库
        sysScheduleJobDao.delete(scheduleJob);

    }
}
