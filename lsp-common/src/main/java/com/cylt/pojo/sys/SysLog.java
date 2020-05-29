package com.cylt.pojo.sys;

import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@DynamicUpdate
@Table(name = "SYS_LOG")
public class SysLog extends BasePojo {


    /**
     * 标题
     */
    @Redis(vagueQuery = true)
    @Column(name = "TITLE")
    private String title;

    /**
     * 状态
     */
    @Redis
    @Column(name = "STATE")
    private String state;

    /**
     * 用户id
     */
    @Redis
    @Column(name = "USER_ID")
    private String userId;


    /**
     * 模块名
     */
    @Redis
    @Column(name = "MODULE")
    private String module;

    /**
     * 服务名
     */
    @Column(name = "SERVICE_NAME")
    private String serviceName;


    /**
     * 方法名
     */
    @Column(name = "DECLARED_METHOD_NAME")
    private String declaredMethodName;


    /**
     * 参数
     */
    @Column(name = "POJO")
    private String pojo;

    /**
     * 开始时间
     */
    @Redis
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private Date startDate;

    /**
     * 处理时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "HANDLE_DATE")
    private Date handleDate;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "END_DATE")
    private Date endDate;


    /**
     * 用时
     */
    @Column(name = "TIME_USE")
    private float timeUse;

    /**
     * 错误文本
     */
    @Redis(vagueQuery = true)
    @Column(name = "ERROR_TEXT")
    private String errorText;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(Date handleDate) {
        this.handleDate = handleDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(float timeUse) {
        this.timeUse = timeUse;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDeclaredMethodName() {
        return declaredMethodName;
    }

    public void setDeclaredMethodName(String declaredMethodName) {
        this.declaredMethodName = declaredMethodName;
    }

    public String getPojo() {
        return pojo;
    }

    public void setPojo(String pojo) {
        this.pojo = pojo;
    }
}
