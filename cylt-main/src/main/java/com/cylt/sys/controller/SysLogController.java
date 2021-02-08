package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysLog;
import com.cylt.sys.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * log Controller
 */
@Api(tags = "日志")
@Controller
@RequestMapping(value = "sys/log", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class SysLogController extends BaseController {

    /**
     * log Service
     */
    @Resource
    private SysLogService sysLogService;

    /**
     * 查询log
     *
     * @param log  检索条件
     * @param page 分页条件
     * @return 分页列表
     */
    @ApiOperation(value = "分页查询日志")
    @RequestMapping(value = "list")
    public String list(SysLog log, Page page) {
        page = sysLogService.list(log, page);
        return getJson(page);
    }

    /**
     * 重试请求
     *
     * @param log 日志信息
     * @return 重试请求结果
     */
    @ApiOperation(value = "重试失败操作")
    @RequestMapping(value = "retry")
    public String retry(SysLog log) {
        sysLogService.retry(log);
        return responseSuccess();
    }
}
