package com.cylt.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cylt.common.SysUser;
import com.cylt.pojo.sys.SysRole;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysRoleDao;
import com.cylt.sys.dao.SysUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 角色service
 */
@Component
@RabbitListener(queues = RabbitMQDictionary.ROLE)
public class SysRoleService {

    public static Logger logger = LoggerFactory.getLogger(SysRoleService.class);

    @Resource
    private SysRoleDao sysRoleDao;

    @RabbitHandler
    public void process(Map testMessage) {
        JSONObject jsonObject = JSON.parseObject((String) testMessage.get("data"));
        switch((String) testMessage.get("action"))
        {
            case RabbitMQDictionary.DELETE :
                delete(jsonObject.toJavaObject(SysRole.class));
                break;
            case RabbitMQDictionary.SAVE :
                save(jsonObject.toJavaObject(SysRole.class));
                break;
            default :
                logger.error("方法名未找到：" + testMessage.get("action"));
        }
    }

    /**
     * 保存
     * @param role
     * @return
     */
    public void save(SysRole role) {
        sysRoleDao.save(role);
    }



    /**
     * 删除
     * @param role
     * @return
     */
    public void delete(SysRole role) {
        sysRoleDao.delete(role);
    }
}
