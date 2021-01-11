package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysJobLog;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysScheduleJobDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务Service
 */
@Transactional
@Service("sysScheduleJobService")
public class SysScheduleJobService extends BaseService {


    //模块名
    private final static String SERVICE_NAME = "sysScheduleJobService";

    @Resource
    private SysScheduleJobDao sysScheduleJobDao;


    /**
     * 查询列表
     * @param job 筛选条件
     * @return 分页任务列表
     */
    public Page list(SysScheduleJob job, Page page) {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("createTime"));
        job.setSort(sortList);
        page = redisUtil.list(job, page);
        // 如果当前一个菜单都没有 就和同步一下
        if (page.getPageList().size() == 0) {
            List<SysScheduleJob> list = sysScheduleJobDao.findAll();
            for(SysScheduleJob scheduleJob : list){
                redisUtil.save(scheduleJob);
            }
            page = redisUtil.list(job, page);
        }
        return page;
    }

    /**
     * 查询任务列表
     * @param job 筛选条件
     * @return 任务
     */
    public List<SysScheduleJob> list(SysScheduleJob job) {
        return redisUtil.list(job);
    }

    /**
     * 查询任务
     * @param id 任务id
     * @return 任务信息
     */
    public SysScheduleJob get(String id) {
        return sysScheduleJobDao.getOne(id);
    }

    /**
     * 保存
     * @param job 任务
     * @return 保存结果
     */
    public SysScheduleJob save(SysScheduleJob job) {
        //刷新缓存
        redisUtil.save(job);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,job);
        return job;
    }


    /**
     * 删除
     * @param job 任务
     */
    public void delete(SysScheduleJob job) {
        redisUtil.del(job);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.DELETE,job);
    }

    /**
     * 查询任务日志列表
     * @param jobLog 任务日志对象
     * @param page 分页属性
     */
    public Page jobLogList(SysJobLog jobLog, Page page) {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("createTime", false));
        jobLog.setSort(sortList);
        return redisUtil.list(jobLog, page);
    }

}
