package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 通知dao
 */
@Repository("sysNoticeDao")
public interface SysNoticeDao extends JpaRepository<SysNotice, String> {
}
