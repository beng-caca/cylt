package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("sysJobLogDao")
public interface SysJobLogDao extends JpaRepository<SysJobLog, String> {
    /**
     * 根据任务id删除任务日志
     * @param jobId 任务id
     */
    void deleteByJobId(String jobId);
}
