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

    /**
     * set mq参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysScheduleJobService";
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
