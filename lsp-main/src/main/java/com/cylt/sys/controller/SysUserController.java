package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.sys.service.SysUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户Controller
 */
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
    public String list(SysUser user, Page page) throws NoSuchFieldException {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("name"));
        sortList.add(new Sort("enterpriseId" ));
        user.setSort(sortList);
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
    public String save(SysUser sysUser) throws Exception {
        String msg = sysUserService.save(sysUser);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysUser sysUser) throws Exception {
        sysUserService.delete(sysUser);
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "updatePassword")
    public String updatePassword(String originalPassword, String newPassword) throws Exception {
        if(sysUserService.updatePassword(originalPassword, newPassword)){
            return responseSsuccess();
        }
        return responseFail("原密码错误！");
    }


    /**
     * 获取当前登录用户
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getThisUser")
    public String getThisUser() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return getJson(user);
    }
}
