package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("sysLogDao")
public interface SysLogDao extends JpaRepository<SysLog, String> {
}
