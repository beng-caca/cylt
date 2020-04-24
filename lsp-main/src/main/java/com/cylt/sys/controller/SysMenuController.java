package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.pojo.sys.SysMenu;
import com.cylt.sys.service.SysMenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "sys/menu", produces = "text/html;charset=utf-8")
public class SysMenuController extends BaseController {

    /**
     * <p>Field toOrderInfoService: 订单组件 Service</p>
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
     * <p>Description: 根据ID取得订单</p>
     * @param id ID
     * @return 订单
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysMenuService.get(id));
    }

    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysMenu sysMenu) {
        String msg = sysMenuService.save(sysMenu);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSsuccess();
    }

    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysMenu sysMenu) {
        sysMenuService.delete(sysMenu);
        return responseSsuccess();
    }


}
