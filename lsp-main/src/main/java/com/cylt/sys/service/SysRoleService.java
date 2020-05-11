package com.cylt.sys.service;

import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysRole;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysRoleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 角色Service
 */
@Transactional
@Service("sysRoleService")
public class SysRoleService extends BaseService {


    //模块名
    public final static String FEATURES_NAME = RabbitMQDictionary.ROLE;

    @Resource
    private SysRoleDao sysRoleDao;



    /**
     * 查询列表
     * @param sysRole
     * @return
     */
    public List<SysRole> list(SysRole sysRole) {
        List<SysRole> list = (List<SysRole>) redisUtil.list(sysRole);
        // 如果当前一个菜单都没有 就和同步一下
        if (list.size() == 0) {
            list = sysRoleDao.findAll();
            for(SysRole role : list){
                redisUtil.set(role);
            }
            list = (List<SysRole>) redisUtil.list(sysRole);
        }
        return list;
    }
    /**
     * 查询菜单
     * @param id
     * @return
     */
    public SysRole get(String id) {
        return sysRoleDao.getOne(id);
    }

    /**
     * 保存
     * @param sysRole
     * @return
     */
    public String save(SysRole sysRole) {
        if(null == sysRole.getId() || "".equals(sysRole.getId())){
            sysRole.setId(UUID.randomUUID().toString());
        }
        //刷新缓存
        redisUtil.save(sysRole);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.SAVE,sysRole);
        return "保存成功";
    }


    /**
     * 删除
     * @param sysRole 角色
     * @return
     */
    public void delete(SysRole sysRole) {
        redisUtil.del(sysRole);
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.DELETE,sysRole);
    }

}
