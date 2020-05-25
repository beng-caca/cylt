package com.cylt.sys.service;

import com.cylt.common.SysUser;
import com.cylt.sys.dao.SysUserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户service
 */
@Service("sysUserService")
public class SysUserService {

    @Resource
    private SysUserDao sysUserDao;

    /**
     * 保存
     * @param sysUser
     * @return
     */
    public void save(SysUser sysUser) {
        sysUserDao.save(sysUser);
    }



    /**
     * 删除
     * @param sysUser
     * @return
     */
    public void delete(SysUser sysUser) {
        sysUserDao.delete(sysUser);
    }
}
