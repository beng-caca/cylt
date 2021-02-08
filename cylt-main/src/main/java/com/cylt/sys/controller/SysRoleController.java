package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysRole;
import com.cylt.sys.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色Controller
 */
@Api(tags = "角色")
@Controller
@RequestMapping(value = "sys/role", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class SysRoleController extends BaseController {

    /**
     * 角色Service
     */
    @Resource
    private SysRoleService sysRoleService;

    /**
     * 查询列表
     * @param role 查询条件
     * @param page 分页条件
     * @return 查询结果
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "list")
    public String list(SysRole role, Page page) {
        page = sysRoleService.list(role, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param role 查询条件
     * @return 查询列表
     */
    @ApiOperation(value = "不分页列表")
    @RequestMapping(value = "noPageList")
    public String list(SysRole role) {
        List<SysRole> list = sysRoleService.list(role);
        return getJson(list);
    }

    /**
     * 保存角色
     * @param role 角色
     * @return 保存结果
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "save")
    public String save(SysRole role) {
        sysRoleService.save(role);
        return responseSuccess();
    }

    /**
     * 删除角色
     * @param role 角色
     * @return 删除结果
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete")
    public String delete(SysRole role) {
        sysRoleService.delete(role);
        return responseSuccess();
    }


}
