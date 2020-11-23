package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysDict;
import com.cylt.pojo.sys.SysRole;
import com.cylt.sys.dao.SysDictDao;
import com.cylt.sys.service.SysDictService;
import com.cylt.sys.service.SysRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典Controller
 */
@Controller
@RequestMapping(value = "sys/dict", produces = "text/html;charset=utf-8")
public class SysDictController extends BaseController {

    /**
     * 角色Service
     */
    @Resource
    private SysDictService sysDictService;

    /**
     * 分页查询列表
     * @param dict 字典
     * @param page 分页
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysDict dict, Page page) throws Exception {
        page = sysDictService.list(dict, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param dict
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "noPageList")
    public String list(SysDict dict) {
        List<SysDict> list = sysDictService.list(dict);
        return getJson(list);
    }

    /**
     * 根据ID取得字典
     * @param id ID
     * @return 字典
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysDictService.get(id));
    }

    /**
     * 保存字典
     * @param dict 字典
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysDict dict) throws Exception {
        String msg = sysDictService.save(dict);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSuccess();
    }

    /**
     * 删除字典
     * @param dict 字典
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysDict dict) throws Exception {
        sysDictService.delete(dict);
        return responseSuccess();
    }


}
