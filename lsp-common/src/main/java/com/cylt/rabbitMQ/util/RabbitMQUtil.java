package com.cylt.rabbitMQ.util;

import com.alibaba.fastjson.JSON;
import com.cylt.common.LogTitle;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        // 系统log保存一天
        redisUtil.save(log, 360 * 12);
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
        String title = "用户{0} 对 {1} 的 {2} 数据进行 {3} 操作";
        sysLog.setTitle(MessageFormat.format(title, user.getName(),serviceName, getLogTitle(obj), declaredMethodName));
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
     * @param obj
     * @return
     */
    private String getLogTitle(BasePojo obj) {
        String logTitle = "";
        //获取所有属性名 设置key
        Field[] values = obj.getClass().getDeclaredFields();
        //遍历其他属性名
        try {
            for (Field field : values) {
                // 判断当前字段是否为一对多
                if (field.getAnnotation(LogTitle.class) != null) {
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    logTitle = (String) field.get(obj);
                }
            }
        } catch (Exception e) {
            logTitle = obj.getId();
        }
        return logTitle;
    }

}
