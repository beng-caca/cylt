package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
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
     *
     * @param log
     * @return
     */
    public Page list(SysLog log, Page page) throws Exception {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("startDate", false));
        log.setSort(sortList);
        return redisUtil.list(log, page);
    }

}
