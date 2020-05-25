package com.cylt.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列交换机
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue queueLog() {
        return new Queue("logService");
    }

    @Bean
    public Queue queueSysService() {
        return new Queue("sysService");
    }


    @Bean
    FanoutExchange sysFanoutExchange() {
        return new FanoutExchange(RabbitMQDictionary.SYS);
    }

    /**
     * sys log
     */
    @Bean
    Binding bindingSysLog() {
        return BindingBuilder.bind(queueLog()).to(sysFanoutExchange());
    }

    /**
     * sys service
     */
    @Bean
    Binding bindingSys() {
        return BindingBuilder.bind(queueSysService()).to(sysFanoutExchange());
    }
}
