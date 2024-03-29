package com.cylt.Interceptor;

import com.cylt.common.MQEntity;
import com.cylt.common.util.LogUtil;
import com.cylt.common.util.SpringUtil;
import com.cylt.pojo.sys.SysLog;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.rabbitMQ.util.RabbitMQUtil;
import com.cylt.redis.RedisUtil;
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
@RabbitListener(queues = RabbitMQDictionary.SYS)
public class SysInterceptor {

    private static Logger logger = LoggerFactory.getLogger(SysInterceptor.class);


    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;

    /**
     * 消息队列
     */
    @Resource
    public RabbitMQUtil rabbitMQUtil;

    @RabbitHandler
    @Transactional(rollbackFor = { Exception.class })
    public void process(MQEntity mq, Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 判断该消息是否有日志
        SysLog log = null;
        if (mq.getSysLog() != null) {
            log = (SysLog) redisUtil.getId(mq.getSysLog());
        }
        // 没有日志或者只对发送状态下的请求做处理
        if(log == null || "1".equals(log.getState())){
            try{
                if (log != null) {
                    // 修改log到正在处理状态
                    LogUtil.processing(mq.getSysLog());
                    // 发送消息队列持久保存到数据库
                    rabbitMQUtil.sendLog(mq);

                }
                // 通过spring容器注入service
                Object service = SpringUtil.getBean(mq.getServiceName());
                // 通过注入的service反射到要调用的方法 并执行
                // 判断是否有参数
                if(mq.getPojo() != null){
                    service.getClass().getDeclaredMethod(mq.getDeclaredMethodName(), mq.getPojo().getClass()).invoke(service, mq.getPojo());
                } else {
                    service.getClass().getDeclaredMethod(mq.getDeclaredMethodName()).invoke(service);
                }
                if (log != null) {
                    // 修改log到处理成功状态
                    LogUtil.success(mq.getSysLog());
                    // 发送消息队列持久保存到数据库
                    rabbitMQUtil.sendLog(mq);
                }
                // 通知rabbitmq处理完成
                channel.basicAck(deliveryTag, true);
            } catch (Exception e){
                logger.error(e.getLocalizedMessage());
                if (log != null) {
                    LogUtil.error(mq.getSysLog(), e.toString());
                    // 发送消息队列持久保存到数据库
                    rabbitMQUtil.sendLog(mq);
                }
                // 通知rabbitmq处理失败 如果失败时不返回 rabbit会在重启服务后再次重试
                // 失败后忽略
                channel.basicReject(deliveryTag,false);
                //手动开启事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new Exception(e);
            }
        } else {
            // 通知rabbitmq处理完成 不然rabbit在重启服务后会重新发送
            channel.basicAck(deliveryTag, true);
        }
    }


}
