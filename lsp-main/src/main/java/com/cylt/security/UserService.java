package com.cylt.security;

import com.cylt.pojo.sys.SysJurisdiction;
import com.cylt.pojo.sys.SysRole;
import com.cylt.redis.RedisUtil;
import com.cylt.common.SysUser;
import com.cylt.sys.dao.SysUserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录专用类
 * 自定义类，实现了UserDetailsService接口，用户登录时调用的第一类
 */
@Component
@Service
public class UserService implements UserDetailsService {

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //判断缓存里有没有这个用户
        SysUser user = new SysUser();
        user.setUsername(username);
        user = (SysUser) redisUtil.get(user);
        if (user == null) {
            user = sysUserDao.findByUsername(username);
            //如果确定有这个用户就放到缓存里
            if(user != null){
                redisUtil.set(user);
            } else {
                user = new SysUser();
            }
        }
        // 初始化权限
        if(user.getRoleList() != null){
            Set<SysJurisdiction> authorities = new HashSet<>();
            for(SysRole role : user.getRoleList()){
                authorities.addAll(role.getJurisdictionList());
            }
            user.setAuthorities(authorities);
        }
        return user;
    }

}
