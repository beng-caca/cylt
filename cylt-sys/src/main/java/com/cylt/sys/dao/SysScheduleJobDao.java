package com.cylt.sys.dao;

import com.cylt.pojo.sys.SysScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("sysScheduleJobDao")
public interface SysScheduleJobDao extends JpaRepository<SysScheduleJob, String> {


    List<SysScheduleJob> findByStatus(int status);
}
