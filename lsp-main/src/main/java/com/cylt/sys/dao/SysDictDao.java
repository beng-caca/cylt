package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysDict;
import com.cylt.pojo.sys.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 字典dao
 */
@Repository("sysDictDao")
public interface SysDictDao extends JpaRepository<SysDict, String> {
}
