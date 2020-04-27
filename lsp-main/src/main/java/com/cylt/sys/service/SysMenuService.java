package com.cylt.sys.service;

import com.cylt.common.RedisUtil;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.rabbitMQ.util.RabbitMQUtil;
import com.cylt.sys.dao.SysMenuDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service("sysMenuService")
public class SysMenuService {


    //模块名
    public final static String FEATURES_NAME = RabbitMQDictionary.MENU;
    @Resource
    private SysMenuDao sysMenuDao;


    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RabbitMQUtil rabbitMQUtil;


    /**
     *
     * @param user
     * @return
     */
    public List<SysMenu> list(SysMenu user) {
        List<SysMenu> list = (List<SysMenu>) redisUtil.list(user);
        // 如果当前一个菜单都没有 就和同步一下
        if (list.size() == 0) {
            list = sysMenuDao.findAll();
            for(SysMenu menu : list){
                redisUtil.set(menu);
            }
        }
        return list;
    }
    /**
     * 查询菜单
     * @param id
     * @return
     */
    public SysMenu get(String id) {
        return sysMenuDao.getOne(id);
    }

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    public String save(SysMenu sysMenu) {
        if(null == sysMenu.getId() || "".equals(sysMenu.getId())){
            sysMenu.setId(UUID.randomUUID().toString());
        }
        //刷新缓存
        redisUtil.del(sysMenu);
        redisUtil.set(sysMenu);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.SAVE,sysMenu);
        return "保存成功";
    }


    /**
     * 删除菜单
     * @param sysMenu 删除菜单
     * @return
     */
    public void delete(SysMenu sysMenu) {
        redisUtil.delTree(sysMenu);
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.DELETE,sysMenu);
    }

}
