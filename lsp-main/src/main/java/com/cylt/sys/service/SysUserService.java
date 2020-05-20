package com.cylt.sys.service;

import com.cylt.common.DESUtil;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.service.BaseService;
import com.cylt.sys.dao.SysUserDao;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 用户Service
 */
@Transactional
@Service("sysUserService")
public class SysUserService extends BaseService {


    //模块名
    public final static String FEATURES_NAME = RabbitMQDictionary.USER;
    @Resource
    private SysUserDao sysUserDao;


    /**
     * 查询用户列表
     * @param user
     * @return
     */
    public Page list(SysUser user, Page page) throws NoSuchFieldException {
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
        SysUser user = new SysUser();
        user.setId(id);
        return (SysUser) redisUtil.getId(user);
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    public String save(SysUser sysUser) throws Exception {
        if(null == sysUser.getId() || "".equals(sysUser.getId())){
            SysUser userName = new SysUser();
            userName.setUsername(sysUser.getUsername());
            //查询缓存里是否有重复
            SysUser redisData = (SysUser) redisUtil.get(userName);
            //判断用户是否存在
            if(redisData != null){
                return "用户名已存在！";
            }
            sysUser.setId(UUID.randomUUID().toString());
            //密码加密
            sysUser.setPassword(DESUtil.encrypt(sysUser.getPassword(),DESUtil.KEY));
        }
        //刷新缓存
        redisUtil.save(sysUser);
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
    public void delete(SysUser sysUser) throws Exception {
        redisUtil.del(sysUser);
        rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.DELETE,sysUser);
    }

    /**
     * 修改用户密码
     * @param originalPassword
     * @param newPassword
     * @return
     */
    public boolean updatePassword(String originalPassword, String newPassword) throws Exception {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        // 判断当前用户的密码是否等于原密码 如果等于就改密码 否则不作操作
        if(user.getPassword().equals(DESUtil.encrypt(originalPassword,DESUtil.KEY))){
            // 为新密码加密
            user.setPassword(DESUtil.encrypt(newPassword,DESUtil.KEY));
            //刷新缓存
            redisUtil.del(user);
            redisUtil.set(user.getUsername(),user);
            //发送消息队列持久保存到数据库
            rabbitMQUtil.send(FEATURES_NAME,RabbitMQDictionary.SAVE,user);
            return true;
        } else {
            return false;
        }
    }
}
