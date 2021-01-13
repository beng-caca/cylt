package com.cylt.sys.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单Service
 */
@Transactional
@Service("sysMenuService")
public class SysMenuService extends BaseService {
    /**
     * 初始化实例参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysMenuService";
    }
}
