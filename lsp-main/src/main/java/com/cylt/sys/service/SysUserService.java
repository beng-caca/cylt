package com.cylt.sys.service;

import com.cylt.common.DESUtil;
import com.cylt.common.SysUser;
import com.cylt.common.base.service.BaseService;
import com.cylt.sys.dao.SysUserDao;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 用户Service
 */
@Transactional
@Service("sysUserService")
public class SysUserService extends BaseService {
    /**
     * 初始化实例参数
     */
    public void setRoutingKey() {
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysUserService";
    }

    @Resource
    private SysUserDao sysUserDao;

    /**
     * 保存
     * @param sysUser 保存对象
     * @return 保存结果
     */
    public String save(SysUser sysUser) {
        setRoutingKey();
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
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, RabbitMQDictionary.SAVE, sysUser);
        return "保存成功";
    }


    /**
     * 获取用户
     * @param userName 用户名称
     * @return 用户对象
     */
    public SysUser getUser(String userName) {
        return sysUserDao.findByUsername(userName);
    }

    /**
     * 修改用户密码
     * @param originalPassword 旧密码
     * @param newPassword 新密码
     * @return 是否保存成功
     */
    public boolean updatePassword(String originalPassword, String newPassword) {
        setRoutingKey();
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        // 判断当前用户的密码是否等于原密码 如果等于就改密码 否则不作操作
        if(user.getPassword().equals(DESUtil.encrypt(originalPassword,DESUtil.KEY))){
            // 为新密码加密
            user.setPassword(DESUtil.encrypt(newPassword,DESUtil.KEY));
            //刷新缓存
            redisUtil.save(user);
            //发送消息队列持久保存到数据库
            rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,user);
            return true;
        } else {
            return false;
        }
    }
}
