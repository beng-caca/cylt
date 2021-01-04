package com.cylt.sys.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysMenuDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 菜单Service
 */
@Transactional
@Service("sysMenuService")
public class SysMenuService extends BaseService {



    //模块名
    private final static String SERVICE_NAME = "sysMenuService";

    @Resource
    private SysMenuDao sysMenuDao;


    /**
     * 查询列表
     * @param menu 查询条件
     * @return 查询结果
     */
    public List<SysMenu> list(SysMenu menu) {
        List<SysMenu> list = redisUtil.list(menu);
        // 如果当前一个菜单都没有 就和同步一下
        if (list.size() == 0) {
            list = sysMenuDao.findAll();
            for(SysMenu menus : list){
                redisUtil.set(menus);
            }
            list = redisUtil.list(menu);
        }
        return list;
    }
    /**
     * 查询菜单
     * @param id 菜单id
     * @return 菜单对象
     */
    public SysMenu get(String id) {
        return sysMenuDao.getOne(id);
    }

    /**
     * 保存
     * @param sysMenu 菜单信息
     * @return 保存结果
     */
    public SysMenu save(SysMenu sysMenu) throws Exception {
        if(null == sysMenu.getId() || "".equals(sysMenu.getId())){
            sysMenu.setId(UUID.randomUUID().toString());
        }
        //刷新缓存
        redisUtil.save(sysMenu);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,sysMenu);
        return sysMenu;
    }


    /**
     * 删除菜单
     * @param sysMenu 删除菜单
     */
    public void delete(SysMenu sysMenu) throws Exception {
        redisUtil.del(sysMenu);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.DELETE,sysMenu);
    }

}
