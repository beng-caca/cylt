package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysDict;
import com.cylt.sys.service.SysDictService;
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
     * @return 分页字典结果
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysDict dict, Page page) {
        page = sysDictService.list(dict, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param dict 查询条件
     * @return 字典列表
     */
    @ResponseBody
    @RequestMapping(value = "noPageList")
    public String list(SysDict dict) {
        List<SysDict> list = sysDictService.list(dict);
        return getJson(list);
    }

    /**
     * 保存字典
     * @param dict 字典
     * @return 保存结果
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysDict dict) {
        sysDictService.save(dict);
        return responseSuccess();
    }

    /**
     * 删除字典
     * @param dict 字典
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysDict dict) {
        sysDictService.delete(dict);
        return responseSuccess();
    }


}
