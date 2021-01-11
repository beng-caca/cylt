package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysRole;
import com.cylt.sys.service.SysRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色Controller
 */
@Controller
@RequestMapping(value = "sys/role", produces = "text/html;charset=utf-8")
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
    @ResponseBody
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
    @ResponseBody
    @RequestMapping(value = "noPageList")
    public String list(SysRole role) {
        List<SysRole> list = sysRoleService.list(role);
        return getJson(list);
    }

    /**
     * 根据ID取得角色
     * @param id ID
     * @return 角色
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysRoleService.get(id));
    }

    /**
     * 保存角色
     * @param role 角色
     * @return 保存结果
     */
    @ResponseBody
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
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysRole role) {
        sysRoleService.delete(role);
        return responseSuccess();
    }


}
