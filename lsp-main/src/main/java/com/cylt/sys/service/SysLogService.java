package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志Service
 */
@Transactional
@Service("sysLogService")
public class SysLogService extends BaseService {


    /**
     * 查询列表
     * @param log log查询条件
     * @param page 分页条件
     * @return 分页对象
     */
    public Page list(SysLog log, Page page) {
        List<Sort> sortList = new ArrayList<>();
        // 通过时间倒序排序
        sortList.add(new Sort("startDate", false));
        log.setSort(sortList);
        return redisUtil.list(log, page);
    }


    /**
     * 重试
     * @param log 重试记录
     */
    public void retry(SysLog log) {
        // 按log参数发送队列
        rabbitMQUtil.send(log);
    }
}
