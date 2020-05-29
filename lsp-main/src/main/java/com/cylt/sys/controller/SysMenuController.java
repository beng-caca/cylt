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

    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysMenu menu) {
        List<SysMenu> list = sysMenuService.list(menu);
        return getJson(list);
    }

    /**
     * <p>Description: 根据ID取得菜单</p>
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysMenu sysMenu) throws Exception {
        String msg = sysMenuService.save(sysMenu);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSuccess();
    }

    /**
     * 删除菜单
     * @param sysMenu 菜单
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysMenu sysMenu) throws Exception {
        sysMenuService.delete(sysMenu);
        return responseSuccess();
    }


}
