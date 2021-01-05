package com.cylt.dataInit;

import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.redis.RedisUtil;
import com.cylt.sys.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * spring启动完成后初始化redis数据库
 */
@Service
public class DataSourceInitListener  implements ApplicationListener<ContextRefreshedEvent> {

    protected static final Logger logger = LoggerFactory.getLogger(DataSourceInitListener.class);


    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private SysRoleDao sysRoleDao;

    @Resource
    private SysMenuDao sysMenuDao;

    @Resource
    private SysNoticeDao sysNoticeDao;

    @Resource
    private SysScheduleJobDao sysScheduleJobDao;


    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;

    /**
     * entityManager
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        //防止重复执行。
        if(ev.getApplicationContext().getParent() == null){
            try{
                // 保存菜单
                redisUtil.save(sysMenuDao.findAll(),null);
                // 保存角色
                redisUtil.save(sysRoleDao.findAll(),null);
                // 保存用户
                redisUtil.save(sysUserDao.findAll(),null);
                // 保存消息
                redisUtil.save(sysNoticeDao.findAll(),null);
                // 保存消息
                redisUtil.save(sysScheduleJobDao.findAll(),null);
                // 解除实体与session的链接防止修改数据库
                entityManager.clear();
                logger.info("redis init success");
            } catch (Exception e) {
                logger.error("redis初始化失败");
                logger.error(e.getMessage());
            } finally {
                logger.info("redis init end");
            }
        }
    }

}
