package com.cylt.sys.service;

import com.cylt.common.DESUtil;
import com.cylt.common.RedisUtil;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.sys.dao.SysUserDao;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.rabbitMQ.util.RabbitMQUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service("sysUserService")
public class SysUserService {


    //模块名
    public final static String FEATURES_NAME = RabbitMQDictionary.USER;
    @Resource
    private SysUserDao sysUserDao;


    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RabbitMQUtil rabbitMQUtil;


    /**
     * 查询用户列表
     * @param user
     * @return
     */
    public Page list(SysUser user, Page page) {
        page = redisUtil.list(user, page);
        // 如果当前一个用户都没有 就和同步一下
        if (page.getPageList().size() == 0) {
            List<SysUser> list = sysUserDao.findAll();
            for(SysUser users : list){
                redisUtil.set(users);
            }
            page = redisUtil.list(user, page);
        }
        return page;
    }
    /**
     * 查询用户
     * @param id
     * @return
     */
    public SysUser get(String id) {
        return sysUserDao.getOne(id);
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    public String save(SysUser sysUser) {
        if(null == sysUser.getId() || "".equals(sysUser.getId())){
            //查询缓存里是否有重复
            SysUser redisData = (SysUser) redisUtil.get(sysUser);
            //判断用户是否存在
            if(redisData != null){
                return "用户名已存在！";
            }
            sysUser.setId(UUID.randomUUID().toString());
        }
        //密码加密
        sysUser.setPassword(DESUtil.encrypt(sysUser.getPassword(),DESUtil.KEY));
        //刷新缓存
        redisUtil.del(sysUser);
        redisUtil.set(sysUser.getUsername(),sysUser);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.SAVE,sysUser);
        return "保存成功";
    }


    /**
     * 获取用户
     * @param userName
     * @return
     */
    public SysUser getUser(String userName) {
        SysUser user = sysUserDao.findByUsername(userName);
        return user;
    }
    /**
     * 删除用户
     * @param sysUser 删除用户
     * @return
     */
    public void delete(SysUser sysUser) {
        redisUtil.del(sysUser);
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.DELETE,sysUser);
    }

}
