package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.sys.service.SysMenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单Controller
 */
@Controller
@RequestMapping(value = "sys/menu", produces = "text/html;charset=utf-8")
public class SysMenuController extends BaseController {

    /**
     * 菜单 Service
     */
    @Resource
    private SysMenuService sysMenuService;

    /**
     * 查询菜单
     * @param menu 查询条件
     * @return 菜单列表
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysMenu menu) {
        List<SysMenu> list = sysMenuService.list(menu);
        return getJson(list);
    }

    /**
     * 根据ID取得菜单
     * @param id ID
     * @return 订单
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysMenuService.get(id));
    }

    /**
     * 保存菜单
     * @param sysMenu 菜单
     * @return 保存结果
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return responseSuccess();
    }

    /**
     * 删除菜单
     * @param sysMenu 菜单
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysMenu sysMenu) {
        sysMenuService.delete(sysMenu);
        return responseSuccess();
    }


}
