package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统log组件
 */
@Component
@Entity
@Getter
@Setter
@DynamicUpdate
@ApiModel("系统日志")
@Table(name = "SYS_LOG")
public class SysLog extends BasePojo {


    /**
     * 标题
     */
    @Redis(vagueQuery = true)
    @ApiModelProperty(value = "标题")
    @Column(name = "TITLE")
    private String title;

    /**
     * 状态
     */
    @Redis
    @ApiModelProperty(value = "状态")
    @Column(name = "STATE")
    private String state;

    /**
     * 用户id
     */
    @Redis
    @ApiModelProperty(value = "用户id")
    @Column(name = "USER_ID")
    private String userId;


    /**
     * 模块名
     */
    @Redis
    @ApiModelProperty(value = "模块名")
    @Column(name = "MODULE")
    private String module;

    /**
     * 服务名
     */
    @ApiModelProperty(value = "服务名")
    @Column(name = "SERVICE_NAME")
    private String serviceName;


    /**
     * 方法名
     */
    @ApiModelProperty(value = "方法名")
    @Column(name = "DECLARED_METHOD_NAME")
    private String declaredMethodName;


    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    @Column(name = "POJO")
    private String pojo;

    /**
     * 开始时间
     */
    @Redis(sort = 1, isAsc = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private Date startDate;

    /**
     * 处理时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "HANDLE_DATE")
    private Date handleDate;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "END_DATE")
    private Date endDate;


    /**
     * 用时
     */
    @ApiModelProperty(value = "用时")
    @Column(name = "TIME_USE")
    private float timeUse;

    /**
     * 错误文本
     */
    @Redis(vagueQuery = true)
    @ApiModelProperty(value = "错误文本")
    @Column(name = "ERROR_TEXT")
    private String errorText;

}
