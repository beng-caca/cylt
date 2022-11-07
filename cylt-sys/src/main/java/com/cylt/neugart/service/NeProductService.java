package com.cylt.neugart.service;

import com.cylt.neugart.dao.NeProductDao;
import com.cylt.pojo.neugart.NeProduct;
import com.cylt.pojo.sys.SysDict;
import com.cylt.sys.dao.SysDictDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 产品service
 */
@Service("neProductService")
public class NeProductService {

    @Resource
    private NeProductDao neProductDao;

    /**
     * 保存
     * @param neProduct
     * @return
     */
    public void save(NeProduct neProduct) {
        neProductDao.save(neProduct);
    }



    /**
     * 删除
     * @param neProduct
     * @return
     */
    public void delete(NeProduct neProduct) {
        neProductDao.deleteById(neProduct.getId());
    }
}
