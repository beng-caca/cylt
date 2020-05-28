package com.cylt.Interceptor;

import com.cylt.common.MQEntity;
import com.cylt.common.util.SpringUtil;
import com.cylt.sys.service.SysLogService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;

/**
 * 系统服务总拦截器
 */
@Component
@RabbitListener(queues = "sysService")
public class SysInterceptor {

    private static Logger logger = LoggerFactory.getLogger(SysInterceptor.class);



    @Resource
    private SysLogService sysLogService;

    @RabbitHandler
    @Transactional(rollbackFor = { Exception.class })
    public void process(MQEntity mq, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try{
            sysLogService.processing(mq.getSysLog());
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
            // 通知rabbitmq处理完成
            channel.basicAck(deliveryTag, true);
        } catch (Exception e){
            logger.error(e.getLocalizedMessage());
            sysLogService.error(mq.getSysLog(), e.toString());
            // 通知rabbitmq处理失败 如果失败时不返回 rabbit会在重启服务后再次重试
            // 失败后忽略
            channel.basicReject(deliveryTag,false);

            //手动开启事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e);
        }
    }


}
