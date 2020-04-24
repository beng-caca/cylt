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


    @Bean
    Binding bindingSysUser() {
        return BindingBuilder.bind(new Queue(RabbitMQDictionary.USER)).to(sys()).with(RabbitMQDictionary.USER);
    }

    @Bean
    Binding bindingSysMenu() {
        return BindingBuilder.bind(new Queue(RabbitMQDictionary.MENU)).to(sys()).with(RabbitMQDictionary.MENU);
    }
}
