package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.SysUser;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户Controller
 */
@Api(tags = "用户")
@Controller
@RequestMapping(value = "sys/user", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class SysUserController extends BaseController {

    /**
     * user
     */
    @Resource
    private SysUserService sysUserService;

    @ApiOperation(value = "列表")
    @RequestMapping(value = "list")
    public String list(SysUser user, Page page) {
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("name"));
        sortList.add(new Sort("enterpriseId" ));
        user.setSort(sortList);
        page = sysUserService.list(user, page);
        return getJson(page);
    }

    @ApiOperation(value = "取单个对象")
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysUserService.getUser(id));
    }

    /**
     * 保存用户
     * @param sysUser 用户信息
     * @return 保存结果
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "save")
    public String save(SysUser sysUser) {
        String msg = sysUserService.save(sysUser);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSuccess();
    }

    /**
     * 删除用户
     * @param sysUser 删除条件
     * @return 删除结果
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete")
    public String delete(SysUser sysUser) {
        sysUserService.delete(sysUser);
        return responseSuccess();
    }

    /**
     * 修改密码
     * @param originalPassword 旧密码
     * @param newPassword 新密码
     * @return 保存结果
     */
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "originalPassword",value = "旧密码",required = true),
            @ApiImplicitParam(name = "newPassword",value = "新密码",required = true)
    })
    @RequestMapping(value = "updatePassword")
    public String updatePassword(String originalPassword, String newPassword) {
        if(sysUserService.updatePassword(originalPassword, newPassword)){
            return responseSuccess();
        }
        return responseFail("原密码错误！");
    }


    /**
     * 获取当前登录用户
     * @return 用户
     */
    @ResponseBody
    @RequestMapping(value = "getThisUser")
    public String getThisUser() {
        SysUser user = (SysUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return getJson(user);
    }
}
