package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysLog;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.sys.service.SysLogService;
import com.cylt.sys.service.SysMenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

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
     * @param log
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysLog log, Page page) throws Exception {
        page = sysLogService.list(log, page);
        return getJson(page);
    }

}
