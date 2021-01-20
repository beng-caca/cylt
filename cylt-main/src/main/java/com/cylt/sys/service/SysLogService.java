package com.cylt.sys.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志Service
 */
@Transactional
@Service("sysLogService")
public class SysLogService extends BaseService {
    /**
     * set mq参数
     */
    public void setRoutingKey(){
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
