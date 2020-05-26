package com.cylt.Interceptor;

import com.cylt.common.MQEntity;
import com.cylt.sys.service.SysLogService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * log总拦截器
 */
@Component
@RabbitListener(queues = "logService")
public class LogInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Resource
    private SysLogService sysLogService;

    @RabbitHandler
    public void process(MQEntity mq, Message message, Channel channel) throws Exception {
        sysLogService.processing(mq.getSysLog());
        // 通知rabbitmq处理完成
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, true);
    }


}
