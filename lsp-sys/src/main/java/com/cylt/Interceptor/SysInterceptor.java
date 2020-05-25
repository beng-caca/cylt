package com.cylt.Interceptor;

import com.cylt.common.MQEntity;
import com.cylt.common.util.SpringUtil;
import com.cylt.sys.dao.SysLogDao;
import com.cylt.sys.service.SysLogService;
import com.cylt.sys.service.SysUserService;
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
 * 系统服务总拦截器
 */
@Component
@RabbitListener(queues = "sysService")
public class SysInterceptor {

    private static Logger logger = LoggerFactory.getLogger(SysInterceptor.class);


    @Resource
    private SysUserService sysUserService;


    @Resource
    private SysLogService sysLogService;

    @RabbitHandler
    public void process(MQEntity mq, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try{
            // 通过spring容器注入service
            Object service = SpringUtil.getBean(mq.getServiceName());
            // 通过注入的service反射到要调用的方法 并执行
            // 判断是否有参数
            if(mq.getPojo() != null){
                service.getClass().getDeclaredMethod(mq.getDeclaredMethodName(), mq.getPojo().getClass()).invoke(service, mq.getPojo());
            } else {
                service.getClass().getDeclaredMethod(mq.getDeclaredMethodName()).invoke(service);
            }
            sysLogService.success(mq.getSysLog());
            channel.basicAck(deliveryTag, true);
        } catch (Exception e){
            sysLogService.error(mq.getSysLog(), e.getMessage());
            channel.basicReject(deliveryTag,false);
        }
        // 业务处理成功后调用，消息会被确认消费
        //channel.basicAck(1l, false);
        // 业务处理失败后调用
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),false, true);
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }


}
