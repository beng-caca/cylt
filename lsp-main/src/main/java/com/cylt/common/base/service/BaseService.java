package com.cylt.common.base.service;


import com.cylt.rabbitMQ.util.RabbitMQUtil;
import com.cylt.redis.RedisUtil;

import javax.annotation.Resource;

/**
 * 业务层基类
 */
public abstract class BaseService {

    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;

    /**
     * 消息队列
     */
    @Resource
    public RabbitMQUtil rabbitMQUtil;
}
