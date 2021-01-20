package com.cylt.sys.service;

import com.cylt.pojo.sys.SysRole;
import com.cylt.sys.dao.SysRoleDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 角色service
 */
@Service("sysRoleService")
public class SysRoleService {

    @Resource
    private SysRoleDao sysRoleDao;

    /**
     * 保存
     * @param role
     * @return
     */
    public void save(SysRole role) {
        sysRoleDao.save(role);
    }



    /**
     * 删除
     * @param role
     * @return
     */
    public void delete(SysRole role) {
        sysRoleDao.delete(role);
    }
}
