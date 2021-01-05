package com.cylt.rabbitMQ.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cylt.common.LogTitle;
import com.cylt.common.MQEntity;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.redis.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

/**
 * 深度封装rabbitMQ工具类
 */
@Component
public class RabbitMQUtil {

    @Resource
    RabbitTemplate rabbitTemplate;


    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;


    /**
     * 核心发送消息队列方法
     *
     * @param mq 消息对象
     */
    private void send(MQEntity mq) throws Exception {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisUtil.save(mq.getSysLog(),60 * 60 * 24, user.getId());
        rabbitTemplate.convertAndSend(RabbitMQDictionary.ROOT, mq.getSysLog().getModule(), mq);
    }


    /**
     * 发送log到mq
     *
     * @param mq 消息对象
     */
    public void sendLog(MQEntity mq) {
        rabbitTemplate.convertAndSend(RabbitMQDictionary.ROOT, RabbitMQDictionary.LOG, mq);
    }

    /**
     * 发送消息队列方法
     *
     * @param routingKey         路由名
     * @param serviceName        service名称
     * @param declaredMethodName 操作名
     * @param obj                参数
     */
    public void send(String routingKey, String serviceName, String declaredMethodName, BasePojo... obj) throws Exception {
        MQEntity mq = new MQEntity();
        // 服务名
        mq.setServiceName(serviceName);
        // 方法名
        mq.setDeclaredMethodName(declaredMethodName);
        // 参数名
        if (obj.length == 1) {
            mq.setPojo(obj[0]);
        } else {
            mq.setPojo(obj);
        }
        // 开始时间
        mq.setStartTime(new Date());
        SysLog log;
        if (obj.length == 1) {
            log = getLog(routingKey, serviceName, declaredMethodName, obj[0]);
        } else {
            log = getLog(routingKey, serviceName, declaredMethodName, obj);
        }
        mq.setSysLog(log);
        send(mq);
    }

    /**
     * 发送消息队列方法
     *
     * @param routingKey         路由名
     * @param serviceName        service名称
     * @param declaredMethodName 操作名
     * @param obj                参数
     * @param log                布尔类型 只要加上这个参数就不生成log记录 坏处就是异步失败了无法追踪
     */
    public void send(String routingKey, String serviceName, String declaredMethodName, Object obj, Boolean log) {
        MQEntity mq = new MQEntity();
        // 服务名
        mq.setServiceName(serviceName);
        // 方法名
        mq.setDeclaredMethodName(declaredMethodName);
        // 参数名
        mq.setPojo(obj);
        // 开始时间
        mq.setStartTime(new Date());
        rabbitTemplate.convertAndSend(RabbitMQDictionary.ROOT, routingKey, mq);
    }


    /**
     * 发送消息队列方法
     *
     * @param log 系统log
     */
    public void send(SysLog log) throws Exception {
        MQEntity mq = new MQEntity();
        // 服务名
        mq.setServiceName(log.getServiceName());
        // 服务名
        mq.setServiceName(log.getServiceName());
        // 方法名
        mq.setDeclaredMethodName(log.getDeclaredMethodName());
        // 参数名
        mq.setPojo(JSON.parseObject(log.getPojo(), BasePojo.class));
        // 将log状态改成正在处理
        log.setState("1");
        // 开始时间
        mq.setStartTime(new Date());
        mq.setSysLog(log);
        send(mq);
    }


    /**
     * 核心发送消息队列方法
     *
     * @param routingKey         路由名
     * @param serviceName        service名称
     * @param declaredMethodName 操作名
     * @param obj                参数
     */
    private SysLog getLog(String routingKey, String serviceName, String declaredMethodName, BasePojo... obj) {

        SysLog sysLog = new SysLog();
        //获取当前登录用户
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sysLog.setUserId(user.getId());
        // 模块名
        sysLog.setModule(routingKey);
        String title = "用户{0} 对 {1} 的 {2} 数据进行 {3} 操作";
        sysLog.setTitle(MessageFormat.format(title, user.getName(), serviceName, getLogTitle(obj), declaredMethodName));
        sysLog.setServiceName(serviceName);
        sysLog.setDeclaredMethodName(declaredMethodName);
        sysLog.setPojo(JSON.toJSONString(obj, SerializerFeature.WriteClassName));

        // 创建id
        sysLog.setId(UUID.randomUUID().toString());
        //获取当前时间作为开始时间
        sysLog.setStartDate(new Date());
        // 将log状态改成正在处理
        sysLog.setState("1");
        return sysLog;
    }

    /**
     * get业务日志标题
     *
     * @param objs 业务对象
     * @return 通过反射机制取到日志标题值
     */
    private String getLogTitle(BasePojo[] objs) {
        StringBuilder logTitle = new StringBuilder();
        //获取所有属性名 设置key
        try {
            Field[] values = objs[0].getClass().getDeclaredFields();
            //遍历其他属性名
            for (Field field : values) {
                // 判断当前字段是否为log标题
                if (field.getAnnotation(LogTitle.class) != null) {
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    for (BasePojo obj : objs) {
                        logTitle.append(field.get(obj));
                    }
                }
            }
        } catch (Exception e) {
            try {
                logTitle.append(objs[0].getId());
            } catch (Exception s) {
                logTitle.append("null");
            }
        }
        return logTitle.toString();
    }

}
