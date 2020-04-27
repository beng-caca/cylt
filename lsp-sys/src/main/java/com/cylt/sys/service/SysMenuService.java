package com.cylt.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysMenuDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 菜单service
 */
@Component
@RabbitListener(queues = RabbitMQDictionary.MENU)
public class SysMenuService {

    public static Logger logger = LoggerFactory.getLogger(SysMenuService.class);

    @Resource
    private SysMenuDao sysMenuDao;

    @RabbitHandler
    public void process(Map testMessage) {
        //没调进来

        JSONObject jsonObject = JSON.parseObject((String) testMessage.get("data"));
        switch((String) testMessage.get("action"))
        {
            case RabbitMQDictionary.DELETE :
                delete(jsonObject.toJavaObject(SysMenu.class));
                break;
            case RabbitMQDictionary.SAVE :
                save(jsonObject.toJavaObject(SysMenu.class));
                break;
            default :
                logger.error("方法名未找到：" + testMessage.get("action"));
        }
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    public void save(SysMenu sysMenu) {
        sysMenuDao.save(sysMenu);
    }



    /**
     * 删除
     * @param sysMenu
     * @return
     */
    public void delete(SysMenu sysMenu) {
        sysMenuDao.deleteById(sysMenu.getId());
    }
}
