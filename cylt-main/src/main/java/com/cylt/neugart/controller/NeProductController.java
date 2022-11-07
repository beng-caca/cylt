package com.cylt.neugart.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.neugart.service.NeProductService;
import com.cylt.pojo.neugart.NeProduct;
import com.cylt.pojo.sys.SysDict;
import com.cylt.sys.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品Controller
 */
@Api(tags = "纽卡特产品")
@Controller
@RequestMapping(value = "neugart/product", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class NeProductController extends BaseController {

    /**
     * 产品Service
     */
    @Resource
    private NeProductService neProductService;

    /**
     * 分页查询列表
     *
     * @param neProduct 产品
     * @param page 分页
     * @return 分页产品结果
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "list")
    public String list(NeProduct neProduct, Page page) {
        page = neProductService.list(neProduct, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     *
     * @param neProduct 查询条件
     * @return 产品列表
     */
    @ApiOperation(value = "不分页列表")
    @RequestMapping(value = "noPageList")
    public String list(NeProduct neProduct) {
        List<NeProduct> list = neProductService.list(neProduct);
        return getJson(list);
    }

    /**
     * 保存产品
     *
     * @param neProduct 产品
     * @return 保存结果
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "save")
    public String save(NeProduct neProduct) {
        neProductService.save(neProduct);
        return responseSuccess();

    }

    /**
     * 删除产品
     *
     * @param neProduct 产品
     * @return 删除结果
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete")
    public String delete(NeProduct neProduct) {
        neProductService.delete(neProduct);
        return responseSuccess();
    }


}
