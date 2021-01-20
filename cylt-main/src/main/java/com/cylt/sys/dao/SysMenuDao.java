package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 菜单dao
 */
@Repository("sysMenuDao")
public interface SysMenuDao extends JpaRepository<SysMenu, String> {
}
