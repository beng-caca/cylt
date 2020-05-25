package com.cylt.rabbitMQ.util;

import com.alibaba.fastjson.JSON;
import com.cylt.common.MQEntity;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.redis.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 深度封装rabbitMQ工具类
 */
@Component
public class RabbitMQUtil {

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;


    /**
     * 核心发送消息队列方法
     * @param exchangeName 交换机名称
     * @param serviceName service名称
     * @param declaredMethodName 操作名
     * @param obj 参数
     */
    public void send(String exchangeName, String serviceName,String declaredMethodName, BasePojo obj) throws Exception {
        MQEntity mq = new MQEntity();
        // 服务名
        mq.setServiceName(serviceName);
        // 方法名
        mq.setDeclaredMethodName(declaredMethodName);
        // 参数名
        mq.setPojo(obj);
        // 开始时间
        mq.setStartTime(new Date());
        SysLog log = getLog(exchangeName, serviceName, declaredMethodName, obj);
        redisUtil.save(log);
        mq.setSysLog(log);
        rabbitTemplate.convertAndSend(exchangeName,null, mq);
    }



    /**
     * 核心发送消息队列方法
     * @param serviceName service名称
     * @param declaredMethodName 操作名
     * @param obj 参数
     */
    private SysLog getLog(String exchangeName, String serviceName,String declaredMethodName, BasePojo obj) {

        SysLog sysLog = new SysLog();
        //获取当前登录用户
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysLog.setUserId(user.getId());
        // 模块名
        sysLog.setModule(exchangeName);
        String title = "用户{0} 对 {1} 的{2}数据进行了 {3}";
        sysLog.setTitle(MessageFormat.format(title, user.getName(),serviceName, obj.getId(), declaredMethodName));
        // 创建id
        sysLog.setId(UUID.randomUUID().toString());
        //获取当前时间作为开始时间
        sysLog.setStartDate(new Date());
        // 将log状态改成正在处理
        sysLog.setState("1");
        return sysLog;
    }
}
