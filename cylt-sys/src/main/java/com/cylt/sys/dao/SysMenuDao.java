package com.cylt.sys.dao;

import com.cylt.common.SysUser;
import com.cylt.pojo.sys.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("sysMenuDao")
public interface SysMenuDao extends JpaRepository<SysMenu, String> {
}
