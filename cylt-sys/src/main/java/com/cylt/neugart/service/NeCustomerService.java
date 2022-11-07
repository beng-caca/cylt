package com.cylt.neugart.service;

import com.cylt.neugart.dao.NeCustomerDao;
import com.cylt.neugart.dao.NeProductDao;
import com.cylt.pojo.neugart.NeCustomer;
import com.cylt.pojo.neugart.NeProduct;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 客户service
 */
@Service("neCustomerService")
public class NeCustomerService {

    @Resource
    private NeCustomerDao neCustomerDao;

    /**
     * 保存
     * @param neCustomer
     * @return
     */
    public void save(NeCustomer neCustomer) {
        neCustomerDao.save(neCustomer);
    }



    /**
     * 删除
     * @param neCustomer
     * @return
     */
    public void delete(NeCustomer neCustomer) {
        neCustomerDao.deleteById(neCustomer.getId());
    }
}
