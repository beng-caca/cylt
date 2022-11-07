package com.cylt.neugart.service;

import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.neugart.NeCustomer;
import com.cylt.pojo.sys.SysPush;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 客户Service
 */
@Transactional
@Service("neCustomerService")
public class NeCustomerService extends BaseService {

    /**
     * set mq参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "neCustomerService";
    }


    /**
     * 门户登录
     *
     * @param loginId 登录名
     * @param verificationCode 验证码
     * @return 登录结果 null=登录失败;Object=登录成功
     */
    public NeCustomer getUser(String loginId) {

        // 如果验证通过 就取到用户返回
        StringBuffer buffer = new StringBuffer();
        buffer.append("NE_CUSTOMER:*LOGIN_ID=").append(loginId).append(":*");
        NeCustomer customer = (NeCustomer) redisUtil.get(buffer.toString(), NeCustomer.class);
        return customer;
    }

    /**
     * 发送验证码
     *
     * @param loginId 登录名
     * @return 发送结果 200=发送成功;300=用户不存在;301=验证码重复发送
     */
    public int sendVerificationCode(String loginId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("NE_CUSTOMER:*LOGIN_ID=").append(loginId).append(":*");
        NeCustomer customer = (NeCustomer) redisUtil.get(buffer.toString(), NeCustomer.class);
        // 用户不存在
        if (customer == null) {
            return 300;
        }
        Integer code = getVerificationCode(loginId);
        // 验证码重复发送
        if (code != null) {
            return 301;
        }
        // 得到 ThreadLocalRandom 对象
        ThreadLocalRandom random = ThreadLocalRandom.current();
        code = random.nextInt(99999,999999);
        redisUtil.mapSet("VERIFICATION_CODE", customer.getLoginId(), code, 60);
        return 200;
    }


    /**
     *  取该用户的验证码
     * @param loginId 用户
     * @return 验证码
     */
    public Integer getVerificationCode(String loginId) {
        String code = (String) redisUtil.hget("VERIFICATION_CODE", loginId);
        if(code == null) {
            return null;
        }
        return Integer.decode(code);
    }
}
