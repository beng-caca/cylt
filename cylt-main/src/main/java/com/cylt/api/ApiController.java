package com.cylt.api;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.neugart.service.NeCustomerService;
import com.cylt.neugart.service.NeProductService;
import com.cylt.pojo.neugart.NeCustomer;
import com.cylt.pojo.neugart.NeProduct;
import com.cylt.pojo.neugart.NeProductDiscount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 门户Controller
 */
@Api(tags = "门户接口")
@Controller
@RequestMapping(value = "api/", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class ApiController extends BaseController {

    /**
     * 产品Service
     */
    @Resource
    private NeProductService neProductService;

    /**
     * 客户Service
     */
    @Resource
    private NeCustomerService neCustomerService;


    /**
     * 不分页查询列表
     *
     * @param custType 用户类型
     * @return 产品列表
     */
    @ApiOperation(value = "不分页列表")
    @RequestMapping(value = "product")
    public String list(String custType) {
        List<NeProduct> list = neProductService.list(new NeProduct());
        for (NeProduct productNext : list) {
            productNext.getDiscountList().removeIf(neProductDiscount -> !custType.equals(neProductDiscount.getCustType()));
        }
        return getJson(list);
    }

    /**
     * 门户登录
     *
     * @param loginId 登录名
     * @param verificationCode 验证码
     * @return 登录结果
     */
    @ApiOperation(value = "门户登录")
    @RequestMapping(value = "login")
    public String login(String loginId, String verificationCode) {
        // 取该用户动态验证码
        Integer code = neCustomerService.getVerificationCode(loginId);
        if (code == null) {
            return responseFail("验证码已过期！");
        }
        if (!verificationCode.equals(code.toString())) {
            return responseFail("验证码错误！");
        }
        // 登录成功 获取用户
        NeCustomer user = neCustomerService.getUser(loginId);
        if(user != null) {
            return getJson(user);
        } else {
            return responseFail("获取用户失败！");
        }
    }


    /**
     * 获取验证码
     *
     * @param loginName 登录名
     * @return 登录结果 200=发送成功;300=用户不存在;301=验证码重复发送
     */
    @ApiOperation(value = "获取验证码")
    @RequestMapping(value = "sendVerificationCode")
    public String sendVerificationCode(String loginName) {
        int status = neCustomerService.sendVerificationCode(loginName);
        Map<String,Object> map = new HashMap<>();
        map.put("code",status);
        switch (status){
            case 200 :
                map.put("message","发送成功，请查收！");
                break;
            case 300:
                map.put("message","用户不存在！");
                break;
            case 301:
                map.put("message","请勿重复发送验证码！");
                break;
        }
        return getJson(map);
    }


}
