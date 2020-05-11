package com.cylt.rabbitMQ.util;

import com.alibaba.fastjson.JSON;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 深度封装rabbitMQ工具类
 */
@Component
public class RabbitMQUtil {

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 核心发送消息队列方法
     * @param featuresName 模块名+功能名
     * @param action 操作名
     * @param obj 参数
     */
    public void send(String featuresName,String action, BasePojo obj) {
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("action", action);
        map.put("createTime", createTime);
        map.put("data", JSON.toJSONString(obj));
        String[] url = featuresName.split(RabbitMQDictionary.DELIMITER);
        rabbitTemplate.convertAndSend(url[0], featuresName, map);
    }
}
