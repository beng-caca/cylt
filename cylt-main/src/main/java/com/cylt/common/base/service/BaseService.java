package com.cylt.common.base.service;


import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.pojo.Page;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.rabbitMQ.util.RabbitMQUtil;
import com.cylt.redis.RedisUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务层基类
 */
@Transactional
public abstract class BaseService {

    // 路由名
    protected String ROUTING_KEY;
    // 功能名
    protected String SERVICE_NAME;


    /**
     * 构造时初始化参数
     */
    public BaseService() {
        setRoutingKey();
    }

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


    /**
     * set mq参数
     */
    public abstract void setRoutingKey();

    /**
     * 查询列表
     * @param pojo 查询条件
     * @param page 分页条件
     * @return 分页对象
     */
    public Page list(BasePojo pojo, Page page) {
        page = redisUtil.list(pojo, page);
        return page;
    }

    /**
     * 查询列表
     * @param pojo 查询条件
     * @return 全部字典列表
     */
    public <T>List<T> list(T pojo) {
        return redisUtil.list((BasePojo) pojo);
    }

    /**
     * 保存
     * @param pojo 保存对象
     * @return 保存后的对象
     */
    public BasePojo save(BasePojo pojo) {
        //刷新缓存
        redisUtil.save(pojo);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(ROUTING_KEY, SERVICE_NAME,RabbitMQDictionary.SAVE,pojo);
        return pojo;
    }


    /**
     * 删除
     * @param pojo 删除条件
     */
    public void delete(BasePojo pojo) {
        redisUtil.del(pojo);
        rabbitMQUtil.send(ROUTING_KEY, SERVICE_NAME,RabbitMQDictionary.DELETE,pojo);
    }
}
