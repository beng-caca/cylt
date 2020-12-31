package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysRole;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysRoleDao;
import com.cylt.sys.dao.SysScheduleJobDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
     * @return
     */
    public Page list(SysScheduleJob job, Page page) throws Exception {
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
     * 查询列表
     * @param job 筛选条件
     * @return
     */
    public List<SysScheduleJob> list(SysScheduleJob job) {
        return (List<SysScheduleJob>) redisUtil.list(job);
    }

    /**
     * 查询菜单
     * @param id 任务id
     * @return
     */
    public SysScheduleJob get(String id) {
        return sysScheduleJobDao.getOne(id);
    }

    /**
     * 保存
     * @param job 任务
     * @return
     */
    public String save(SysScheduleJob job) throws Exception {
        //刷新缓存
        redisUtil.save(job);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.TASK, SERVICE_NAME,RabbitMQDictionary.SAVE,job);
        return "保存成功";
    }


    /**
     * 删除
     * @param job 任务
     * @return
     */
    public void delete(SysScheduleJob job) throws Exception {
        redisUtil.del(job);
        rabbitMQUtil.send(RabbitMQDictionary.TASK, SERVICE_NAME,RabbitMQDictionary.DELETE,job);
    }

}
