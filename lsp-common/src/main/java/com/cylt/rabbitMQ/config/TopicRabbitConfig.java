package com.cylt.rabbitMQ.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列交换机
 */
@Configuration
public class TopicRabbitConfig {

    /**
     * 模块声明:sys
     * @return
     */
    @Bean
    TopicExchange sys() {
        return new TopicExchange(RabbitMQDictionary.SYS);
    }

    /**
     * 绑定用户持久层
     */
    @Bean
    Binding bindingSysUser() {
        return BindingBuilder.bind(new Queue(RabbitMQDictionary.USER)).to(sys()).with(RabbitMQDictionary.USER);
    }

    /**
     * 绑定菜单持久层
     */
    @Bean
    Binding bindingSysMenu() {
        return BindingBuilder.bind(new Queue(RabbitMQDictionary.MENU)).to(sys()).with(RabbitMQDictionary.MENU);
    }


    /**
     * 绑定角色持久层
     */
    @Bean
    Binding bindingSysRole() {
        return BindingBuilder.bind(new Queue(RabbitMQDictionary.ROLE)).to(sys()).with(RabbitMQDictionary.ROLE);
    }
}
