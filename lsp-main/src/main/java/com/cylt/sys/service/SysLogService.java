package com.cylt.sys.service;

import com.alibaba.fastjson.JSON;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.rabbitMQ.util.RabbitMQUtil;
import com.cylt.sys.dao.SysLogDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 日志Service
 */
@Transactional
@Service("sysLogService")
public class SysLogService extends BaseService {

    @Resource
    private SysLogDao sysLogDao;

    /**
     * 查询列表
     * @param log
     * @return
     */
    public Page list(SysLog log, Page page) throws Exception {
        List<Sort> sortList = new ArrayList<>();
        // 通过时间倒序排序
        sortList.add(new Sort("startDate", false));
        log.setSort(sortList);
        return redisUtil.list(log, page);
    }


    /**
     * 重试
     * @param log
     * @return
     */
    public void retry(SysLog log) throws Exception {
        // 按log参数发送队列
        rabbitMQUtil.send(log);
    }
}
