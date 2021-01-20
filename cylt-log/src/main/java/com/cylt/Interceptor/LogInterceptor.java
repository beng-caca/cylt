package com.cylt.Interceptor;

import com.cylt.common.MQEntity;
import com.cylt.common.SysUser;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.redis.RedisUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * log总拦截器
 */
@Component
@RabbitListener(queues = RabbitMQDictionary.LOG)
public class LogInterceptor {

    @Resource
    private SysLogDao sysLogDao;

    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;

    @RabbitHandler
    public void process(MQEntity mq) throws Exception {
        SysLog log = mq.getSysLog();
        // 判断是否消费成功 如果消费成功则只保存一天的log到redis
        if("3".equals(log.getState())){
            redisUtil.save(log,60 * 60 * 24, log.getUpdateBy());
        } else {
            redisUtil.save(log);
        }
        sysLogDao.save(log);
    }


}
