package com.cylt.common;

import com.cylt.common.base.pojo.BasePojo;
import com.cylt.pojo.sys.SysLog;

import java.io.Serializable;
import java.util.Date;

public class MQEntity implements Serializable {

    /**
     * service名称
     */
    private String serviceName;

    /**
     * 实体参数
     */
    private BasePojo pojo;

    /**
     * 方法名
     */
    private String declaredMethodName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * log
     */
    private SysLog sysLog;


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BasePojo getPojo() {
        return pojo;
    }

    public void setPojo(BasePojo pojo) {
        this.pojo = pojo;
    }

    public String getDeclaredMethodName() {
        return declaredMethodName;
    }

    public void setDeclaredMethodName(String declaredMethodName) {
        this.declaredMethodName = declaredMethodName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public SysLog getSysLog() {
        return sysLog;
    }

    public void setSysLog(SysLog sysLog) {
        this.sysLog = sysLog;
    }
}
