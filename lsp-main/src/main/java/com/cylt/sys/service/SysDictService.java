package com.cylt.sys.service;

import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysDict;
import com.cylt.pojo.sys.SysRole;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import com.cylt.sys.dao.SysDictDao;
import com.cylt.sys.dao.SysRoleDao;
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
     * @param sysDict
     * @return
     */
    public Page list(SysDict sysDict, Page page) throws Exception {
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
     * @param sysDict
     * @return
     */
    public List<SysDict> list(SysDict sysDict) {
        // 这里不用排序字段检索
        sysDict.setDictOrder(-1);
        return (List<SysDict>) redisUtil.list(sysDict);
    }

    /**
     * 查询字典
     * @param id
     * @return
     */
    public SysDict get(String id) {
        return sysDictDao.getOne(id);
    }

    /**
     * 保存
     * @param sysDict
     * @return
     */
    public String save(SysDict sysDict) throws Exception {
        //刷新缓存
        redisUtil.save(sysDict);
        //发送消息队列持久保存到数据库
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.SAVE,sysDict);
        return "保存成功";
    }


    /**
     * 删除
     * @param sysDict 字典
     * @return
     */
    public void delete(SysDict sysDict) throws Exception {
        redisUtil.del(sysDict);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME,RabbitMQDictionary.DELETE,sysDict);
    }

}
