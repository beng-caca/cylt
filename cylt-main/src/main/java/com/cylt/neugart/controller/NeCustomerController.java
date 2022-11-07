package com.cylt.neugart.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.neugart.service.NeCustomerService;
import com.cylt.neugart.service.NeProductService;
import com.cylt.pojo.neugart.NeCustomer;
import com.cylt.pojo.neugart.NeProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客户Controller
 */
@Api(tags = "客户信息")
@Controller
@RequestMapping(value = "neugart/customer", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class NeCustomerController extends BaseController {

    /**
     * 客户Service
     */
    @Resource
    private NeCustomerService neCustomerService;

    /**
     * 分页查询列表
     *
     * @param neCustomer 产品
     * @param page 分页
     * @return 分页产品结果
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "list")
    public String list(NeCustomer neCustomer, Page page) {
        page = neCustomerService.list(neCustomer, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     *
     * @param neCustomer 查询条件
     * @return 产品列表
     */
    @ApiOperation(value = "不分页列表")
    @RequestMapping(value = "noPageList")
    public String list(NeCustomer neCustomer) {
        List<NeCustomer> list = neCustomerService.list(neCustomer);
        return getJson(list);
    }

    /**
     * 保存
     *
     * @param neCustomer 客户
     * @return 保存结果
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "save")
    public String save(NeCustomer neCustomer) {
        neCustomerService.save(neCustomer);
        return responseSuccess();

    }

    /**
     * 删除
     *
     * @param neCustomer 客户
     * @return 删除结果
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete")
    public String delete(NeCustomer neCustomer) {
        neCustomerService.delete(neCustomer);
        return responseSuccess();
    }


}
