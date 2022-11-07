package com.cylt.neugart.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 产品Service
 */
@Transactional
@Service("neProductService")
public class NeProductService extends BaseService {

    /**
     * set mq参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "neProductService";
    }

}
