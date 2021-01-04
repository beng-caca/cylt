package com.cylt.sys.controller;

import com.cylt.common.SysUser;
import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysNotice;
import com.cylt.pojo.sys.SysPush;
import com.cylt.sys.service.SysNoticeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 通知Controller
 */
@Controller
@RequestMapping(value = "sys/notice", produces = "text/html;charset=utf-8")
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
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysNotice notice, Page page) throws Exception {
        page = sysNoticeService.list(notice, page);
        return getJson(page);
    }

    /**
     * 根据ID取得通知
     * @param id ID
     * @return 角色
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysNoticeService.get(id));
    }

    /**
     * 保存通知
     * @param notice 通知对象
     * @return 保存结果
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysNotice notice) throws Exception {
        sysNoticeService.save(notice);
        return responseSuccess();
    }

    /**
     * 删除通知
     * @param notice 通知对象
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysNotice notice) throws Exception {
        sysNoticeService.delete(notice);
        return responseSuccess();
    }



    /**
     * 推送通知
     * @param notice 通知对象
     * @return 推送结果
     */
    @ResponseBody
    @RequestMapping(value = "push")
    public String push(SysNotice notice) {
        sysNoticeService.push(notice);
        return responseSuccess();
    }

    /**
     * 推送通知
     * @return 推送列表
     */
    @ResponseBody
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
    @ResponseBody
    @RequestMapping(value = "read")
    public String read(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.read(user, push);
        return responseSuccess();
    }

    /**
     * 推送已读
     */
    @ResponseBody
    @RequestMapping(value = "readAll")
    public String readAll() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.readAll(user);
        return responseSuccess();
    }

    /**
     * 推送删除
     */
    @ResponseBody
    @RequestMapping(value = "delPush")
    public String delPush(SysPush push) {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        sysNoticeService.delPush(user, push);
        return responseSuccess();
    }

}
