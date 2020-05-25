package com.cylt.sys.service;

import com.cylt.pojo.sys.SysMenu;
import com.cylt.sys.dao.SysMenuDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 菜单service
 */
@Component
public class SysMenuService {


    @Resource
    private SysMenuDao sysMenuDao;

    /**
     * 保存
     * @param sysMenu
     * @return
     */
    public void save(SysMenu sysMenu) {
        sysMenuDao.save(sysMenu);
    }



    /**
     * 删除
     * @param sysMenu
     * @return
     */
    public void delete(SysMenu sysMenu) {
        sysMenuDao.deleteById(sysMenu.getId());
    }
}
