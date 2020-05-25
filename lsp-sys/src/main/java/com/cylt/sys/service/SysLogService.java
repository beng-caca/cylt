package com.cylt.sys.service;

import com.cylt.common.SysUser;
import com.cylt.common.util.DateUtils;
import com.cylt.pojo.sys.SysLog;
import com.cylt.sys.dao.SysLogDao;
import com.cylt.sys.dao.SysUserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * log
 */
@Service("sysLogService")
public class SysLogService {

    @Resource
    private SysLogDao sysLogDao;

    /**
     * 正在处理
     * @param sysLog log实体
     */
    public void processing(SysLog sysLog) {
        sysLog.setHandleDate(new Date());
        // 将log状态改成正在处理
        sysLog.setState("2");
        sysLogDao.save(sysLog);
    }



    /**
     * 处理成功
     * @param sysLog log实体
     * @return
     */
    public void success(SysLog sysLog) {
        sysLog.setEndDate(new Date());
        float asd = DateUtils.minus(sysLog.getEndDate(), sysLog.getStartDate());
        sysLog.setTimeUse(DateUtils.minus(sysLog.getEndDate(), sysLog.getStartDate()));
        // 将log状态改成处理完成
        sysLog.setState("3");
        sysLogDao.save(sysLog);
    }


    /**
     * 处理失败
     * @param sysLog log实体
     * @param error 异常文本
     * @return
     */
    public void error(SysLog sysLog, String error) {
        sysLog.setEndDate(new Date());
        float asd = DateUtils.minus(sysLog.getStartDate(), sysLog.getEndDate());
        sysLog.setTimeUse(DateUtils.minus(sysLog.getEndDate(), sysLog.getStartDate()));
        // 将log状态改成正在处理
        sysLog.setState("0");
        sysLog.setErrorText(error);
        sysLogDao.save(sysLog);
    }
}
