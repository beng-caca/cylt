package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysLog;
import com.cylt.sys.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * log Controller
 */
@Controller
@RequestMapping(value = "sys/log", produces = "text/html;charset=utf-8")
public class SysLogController extends BaseController {

    /**
     * log Service
     */
    @Resource
    private SysLogService sysLogService;

    /**
     * 查询log
     * @param log 检索条件
     * @param page 分页条件
     * @return 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysLog log, Page page) {
        page = sysLogService.list(log, page);
        return getJson(page);
    }

    /**
     * 重试请求
     * @param log 日志信息
     * @return 重试请求结果
     */
    @ResponseBody
    @RequestMapping(value = "retry")
    public String retry(SysLog log) {
        sysLogService.retry(log);
        return responseSuccess();
    }
}
