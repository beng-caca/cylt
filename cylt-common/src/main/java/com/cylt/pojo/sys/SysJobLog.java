package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统任务日志
 */
@Component
@Entity
@Getter
@Setter
@ApiModel("系统任务日志")
@Table(name = "SYS_JOB_LOG")
public class SysJobLog extends BasePojo {

    /**
     * 任务ID
     */
    @Redis
    @ApiModelProperty(value = "任务ID")
    @Column(name = "JOB_ID")
    private String jobId;

    /**
     * 状态
     */
    @Redis
    @ApiModelProperty(value = "状态")
    @Column(name = "STATE")
    private String state;

    /**
     * 任务log
     */
    @ApiModelProperty(value = "任务log")
    @Column(name = "TEXT")
    private String text;


    /**
     * 开始执行时间
     */
    @ApiModelProperty(value = "开始执行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private Date startDate;

    /**
     * 用时
     */
    @ApiModelProperty(value = "用时")
    @Column(name = "TIME_USE")
    private float timeUse;

}
