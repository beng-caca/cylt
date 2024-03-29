package com.cylt.pojo.sys;

import com.cylt.common.LogTitle;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 任务
 */
@Component
@Entity
@Getter
@Setter
@ApiModel("任务")
@Table(name = "SYS_SCHEDULE_JOB")
@Where(clause = "DEL_STATE = '0'")
@SQLDelete(sql = "UPDATE SYS_SCHEDULE_JOB SET DEL_STATE = '1' WHERE id=?", check = ResultCheckStyle.COUNT)
public class SysScheduleJob  extends BasePojo {

    /**
     * 任务名称
     */
    @Redis(vagueQuery = true)
    @LogTitle
    @ApiModelProperty(value = "任务名称")
    @Column(name = "JOB_NAME")
    private String jobName;

    /**
     * cron表达式
     */
    @Redis
    @ApiModelProperty(value = "cron表达式")
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;

    /**
     * 服务名称
     */
    @Redis
    @ApiModelProperty(value = "服务名称")
    @Column(name = "BEAN_NAME")
    private String beanName;

    /**
     * 方法名称
     */
    @ApiModelProperty(value = "方法名称")
    @Column(name = "METHOD_NAME")
    private String methodName;

    /**
     * 状态 0 启动 1 停止
     */
    @Redis(sort = 1)
    @ApiModelProperty(value = "状态0 启动 1 停止")
    @Column(name = "STATUS")
    private int status;
}
