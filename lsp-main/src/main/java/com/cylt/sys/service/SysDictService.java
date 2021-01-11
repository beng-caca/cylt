package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysDict;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysDictDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典Service
 */
@Transactional
@Service("sysDictService")
public class SysDictService extends BaseService {


    //模块名
    private final static String SERVICE_NAME = "sysDictService";

    @Resource
    private SysDictDao sysDictDao;



    /**
     * 查询列表
     * @param sysDict 查询条件
     * @param page 分页条件
     * @return 分页对象
     */
    public Page list(SysDict sysDict, Page page) {
        // 这里不用排序字段检索
        sysDict.setDictOrder(-1);
        List<Sort> sortList = new ArrayList<>();
        sortList.add(new Sort("dictKey"));
        sortList.add(new Sort("dictOrder"));
        sysDict.setSort(sortList);
        page = redisUtil.list(sysDict, page);
        // 如果当前一个菜单都没有 就和同步一下
        if (page.getPageList().size() == 0) {
            List<SysDict> list = sysDictDao.findAll();
            for(SysDict role : list){
                redisUtil.save(role);
            }
            page = redisUtil.list(sysDict, page);
        }
        return page;
    }

    /**
     * 查询列表
     * @param sysDict 查询条件
     * @return 全部字典列表
     */
    public List<SysDict> list(SysDict sysDict) {
        return redisUtil.list(sysDict);
    }

    /**
     * 查询字典
     * @param id 查询id
     * @return 字典对象
     */
    public SysDict get(String id) {
        return sysDictDao.getOne(id);
    }

    /**
     * 保存字典
     * @param sysDict 保存对象
     * @return 保存后的字典
     */
    public SysDict save(SysDict sysDict) {
        //刷新缓存
        redisUtil.save(sysDict);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,sysDict);
        return sysDict;
    }


    /**
     * 删除 字典
     * @param sysDict 删除条件
     */
    public void delete(SysDict sysDict) {
        redisUtil.del(sysDict);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.DELETE,sysDict);
    }

}
