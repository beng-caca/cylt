package com.cylt.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列交换机
 */
@Configuration
public class RabbitConfig {

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Bean
    public Queue queueLog() {
        return new Queue(RabbitMQDictionary.LOG);
    }

    @Bean
    public Queue queueSysService() {
        return new Queue(RabbitMQDictionary.SYS);
    }

    // --------------------------  SYS  --------------------------

    /**
     * SYS
     */
    @Bean
    TopicExchange rootTopicExchange() {
        return new TopicExchange(RabbitMQDictionary.ROOT);
    }

    /**
     * sys log
     */
    @Bean
    Binding bindingSysLog() {
        return BindingBuilder.bind(queueLog()).to(rootTopicExchange()).with(RabbitMQDictionary.LOG);
    }

    /**
     * sys service
     */
    @Bean
    Binding bindingSys() {
        return BindingBuilder.bind(queueSysService()).to(rootTopicExchange()).with(RabbitMQDictionary.SYS);
    }


    // ------------------------------------------------------------


    /**
     * 创建初始化RabbitAdmin对象
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
    /**
     * 创建交换机和对列
     */
    @Bean
    public void createExchangeQueue (){
        rabbitAdmin.declareExchange(rootTopicExchange());
        rabbitAdmin.declareQueue(queueLog());
        rabbitAdmin.declareQueue(queueSysService());
    }

}
