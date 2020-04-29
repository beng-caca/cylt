package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.Page;
import com.cylt.sys.service.SysUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;

@Controller
@RequestMapping(value = "sys/user", produces = "text/html;charset=utf-8")
public class SysUserController extends BaseController {

    /**
     * user
     */
    @Resource
    private SysUserService sysUserService;

    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysUser user, Page page) {
         page = sysUserService.list(user, page);
        return getJson(page);
    }

    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysUserService.getUser(id));
    }

    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysUser sysUser) {
        String msg = sysUserService.save(sysUser);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysUser sysUser) {
        sysUserService.delete(sysUser);
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "updatePassword")
    public String updatePassword(String originalPassword, String newPassword) {
        if(sysUserService.updatePassword(originalPassword, newPassword)){
            return responseSsuccess();
        }
        return responseFail("原密码错误！");
    }
}
