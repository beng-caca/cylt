package com.cylt.sys.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色Service
 */
@Transactional
@Service("sysRoleService")
public class SysRoleService extends BaseService {

    /**
     * 初始化实例参数
     */
    public void setRoutingKey() {
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysRoleService";
    }



}
