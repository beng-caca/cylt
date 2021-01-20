package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色dao
 */
@Repository("sysRoleDao")
public interface SysRoleDao extends JpaRepository<SysRole, String> {
}
