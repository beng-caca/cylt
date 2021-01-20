package com.cylt.common.util;

import com.cylt.pojo.sys.SysLog;

import java.util.Date;

public class LogUtil {


    /**
     * 正在处理
     * @param sysLog log实体
     */
    public static void processing(SysLog sysLog) {
        sysLog.setHandleDate(new Date());
        // 将log状态改成正在处理
        sysLog.setState("2");
    }

    /**
     * 处理成功
     * @param sysLog log实体
     * @return
     */
    public static void success(SysLog sysLog) {
        sysLog.setEndDate(new Date());
        sysLog.setTimeUse(DateUtils.minus(sysLog.getEndDate(), sysLog.getStartDate()));
        // 将log状态改成处理完成
        sysLog.setState("3");
    }

    /**
     * 处理失败
     * @param sysLog log实体
     * @param error 异常文本
     * @return
     */
    public static void error(SysLog sysLog, String error) {
        sysLog.setEndDate(new Date());
        sysLog.setTimeUse(DateUtils.minus(sysLog.getEndDate(), sysLog.getStartDate()));
        // 将log状态改成处理失败
        sysLog.setState("0");
        sysLog.setErrorText(error);
    }
}
