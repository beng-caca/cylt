package com.cylt.sys.service;

import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.service.BaseService;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典Service
 */
@Transactional
@Service("sysDictService")
public class SysDictService extends BaseService {

    /**
     * set mq参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysDictService";
    }

    /**
     * 查询列表
     * @param pojo 查询条件
     * @param page 分页条件
     * @return 分页对象
     */
    public Page list(BasePojo pojo, Page page) {
        return super.list(pojo,page);
    }

}
