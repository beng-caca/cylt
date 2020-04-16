package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.SysUser;
import com.cylt.sys.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "sys/user", produces = "text/html;charset=utf-8")
public class SysUserController extends BaseController {

    /**
     * <p>Field toOrderInfoService: 订单组件 Service</p>
     */
    @Resource
    private SysUserService sysUserService;

    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysUser user) {
        List<SysUser> list = sysUserService.list(user);
        return getJson(list);
    }

    /**
     * <p>Description: 根据ID取得订单</p>
     * @param id ID
     * @return 订单
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysUserService.getUser(id));
    }

    @ResponseBody
    @RequestMapping(value = "save")
    public String save(@RequestBody SysUser sysUser) {
        String msg = sysUserService.save(sysUser);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(@RequestBody SysUser sysUser) {
        sysUserService.delete(sysUser);
        return responseSsuccess();
    }

}
