package com.cylt.sys.controller;

import com.cylt.common.SysUser;
import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysNotice;
import com.cylt.pojo.sys.SysPush;
import com.cylt.sys.service.SysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 通知Controller
 */
@Api(tags = "通知")
@Controller
@RequestMapping(value = "sys/notice", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class SysNoticeController extends BaseController {

    /**
     * 通知Service
     */
    @Resource
    private SysNoticeService sysNoticeService;

    /**
     * 分页查询列表
     * @param notice 通知对象
     * @param page 分页参数
     * @return 通知列表
     */
    @ApiOperation(value = "分页查询通知模板")
    @RequestMapping(value = "list")
    public String list(SysNotice notice, Page page) {
        page = sysNoticeService.list(notice, page);
        return getJson(page);
    }

    /**
     * 保存通知模板
     * @param notice 通知对象
     * @return 保存结果
     */
    @ApiOperation(value = "保存通知模板")
    @RequestMapping(value = "save")
    public String save(SysNotice notice) {
        sysNoticeService.save(notice);
        return responseSuccess();
    }

    /**
     * 删除通知模板
     * @param notice 通知对象
     * @return 删除结果
     */
    @ApiOperation(value = "删除通知模板")
    @RequestMapping(value = "delete")
    public String delete(SysNotice notice) {
        sysNoticeService.delete(notice);
        return responseSuccess();
    }



    /**
     * 推送通知
     * @param notice 通知对象
     * @return 推送结果
     */
    @ApiOperation(value = "下发通知")
    @RequestMapping(value = "push")
    public String push(SysNotice notice) {
        sysNoticeService.push(notice);
        return responseSuccess();
    }

    /**
     * 推送通知
     * @return 推送列表
     */
    @ApiOperation(value = "刷新推送")
    @RequestMapping(value = "news")
    public String news() {
        // 获取当前登录用户
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return getJson(sysNoticeService.pop(user));
    }

    /**
     * 推送已读
     * @param push 推送对象
     */
    @ApiOperation(value = "推送已读")
    @RequestMapping(value = "read")
    public String read(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.read(user, push);
        return responseSuccess();
    }

    /**
     * 推送已读
     */
    @ApiOperation(value = "全部已读")
    @RequestMapping(value = "readAll")
    public String readAll() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.readAll(user);
        return responseSuccess();
    }

    /**
     * 推送删除
     */
    @ApiOperation(value = "删除推送")
    @RequestMapping(value = "delPush")
    public String delPush(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.delPush(user, push);
        return responseSuccess();
    }

}
