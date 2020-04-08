package com.cylt.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cylt.common.SysUser;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户service
 */
@Component
@RabbitListener(queues = RabbitMQDictionary.USER)
public class SysUserService {

    public static Logger logger = LoggerFactory.getLogger(SysUserService.class);

    @Resource
    private SysUserDao sysUserDao;

    @RabbitHandler
    public void process(Map testMessage) {
        //没调进来

        JSONObject jsonObject = JSON.parseObject((String) testMessage.get("data"));
        switch((String) testMessage.get("action"))
        {
            case RabbitMQDictionary.DELETE :
                delete(jsonObject.toJavaObject(SysUser.class));
                break;
            case RabbitMQDictionary.SAVE :
                save(jsonObject.toJavaObject(SysUser.class));
                break;
            default :
                logger.error("方法名未找到：" + testMessage.get("action"));
        }
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    public void save(SysUser sysUser) {
        sysUserDao.save(sysUser);
    }



    /**
     * 删除
     * @param sysUser
     * @return
     */
    public void delete(SysUser sysUser) {
        sysUserDao.delete(sysUser);
    }
}
