package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysRole;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysRoleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色Service
 */
@Transactional
@Service("sysRoleService")
public class SysRoleService extends BaseService {


    //模块名
    private final static String SERVICE_NAME = "sysRoleService";

    @Resource
    private SysRoleDao sysRoleDao;



    /**
     * 查询列表
     * @param sysRole 查询条件
     * @param page 分页条件
     * @return 分页列表
     */
    public Page list(SysRole sysRole, Page page) {
        page = redisUtil.list(sysRole, page);
        // 如果当前一个菜单都没有 就和同步一下
        if (page.getPageList().size() == 0) {
            List<SysRole> list = sysRoleDao.findAll();
            for(SysRole role : list){
                redisUtil.save(role);
            }
            page = redisUtil.list(sysRole, page);
        }
        return page;
    }

    /**
     * 查询列表
     * @param sysRole 查询条件
     * @return 角色列表
     */
    public List<SysRole> list(SysRole sysRole) {
        return redisUtil.list(sysRole);
    }

    /**
     * 查询角色
     * @param id 角色ID
     * @return 角色信息
     */
    public SysRole get(String id) {
        return sysRoleDao.getOne(id);
    }

    /**
     * 保存角色
     * @param sysRole 角色
     * @return 保存后的角色
     */
    public SysRole save(SysRole sysRole) {
        //刷新缓存
        redisUtil.save(sysRole);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,sysRole);
        return sysRole;
    }


    /**
     * 删除
     * @param sysRole 角色
     */
    public void delete(SysRole sysRole) {
        redisUtil.del(sysRole);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.DELETE,sysRole);
    }

}
