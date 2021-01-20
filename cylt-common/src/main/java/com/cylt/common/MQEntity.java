package com.cylt.common;

import com.cylt.pojo.sys.SysLog;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MQEntity implements Serializable {

    /**
     * service名称
     */
    private String serviceName;

    /**
     * 实体参数
     */
    private Object pojo;

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
}
