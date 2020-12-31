package com.cylt.job;

import com.cylt.pojo.sys.SysScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("sysScheduleJobDao")
public interface SysScheduleJobDao extends JpaRepository<SysScheduleJob, String> {
}
