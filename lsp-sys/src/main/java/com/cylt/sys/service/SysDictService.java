package com.cylt.sys.service;

import com.cylt.pojo.sys.SysDict;
import com.cylt.sys.dao.SysDictDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 字典service
 */
@Service("sysDictService")
public class SysDictService {

    @Resource
    private SysDictDao sysDictDao;

    /**
     * 保存
     * @param sysDict
     * @return
     */
    public void save(SysDict sysDict) {
        sysDictDao.save(sysDict);
    }



    /**
     * 删除
     * @param sysDict
     * @return
     */
    public void delete(SysDict sysDict) {
        sysDictDao.delete(sysDict);
    }
}
