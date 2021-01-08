package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统log
 */
@Component
@Entity
@Getter
@Setter
@Table(name = "SYS_JOB_LOG")
public class SysJobLog extends BasePojo {

    /**
     * 任务ID
     */
    @Redis
    @Column(name = "JOB_ID")
    private String jobId;

    /**
     * 状态
     */
    @Redis
    @Column(name = "STATE")
    private String state;

    /**
     * 任务log
     */
    @Column(name = "TEXT")
    private String text;


    /**
     * 开始执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private Date startDate;

    /**
     * 用时
     */
    @Column(name = "TIME_USE")
    private float timeUse;

}
