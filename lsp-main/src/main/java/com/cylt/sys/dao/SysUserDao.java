package com.cylt.sys.dao;

import com.cylt.common.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户dao
 */
@Repository("sysUserDao")
public interface SysUserDao extends JpaRepository<SysUser, String> {

    SysUser findByUsername( @Param("userName") String userName);
}
