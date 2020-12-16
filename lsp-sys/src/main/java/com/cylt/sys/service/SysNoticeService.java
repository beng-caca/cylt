package com.cylt.sys.service;

import com.cylt.common.SysUser;
import com.cylt.pojo.sys.SysNotice;
import com.cylt.pojo.sys.SysPush;
import com.cylt.pojo.sys.SysRole;
import com.cylt.redis.RedisUtil;
import com.cylt.sys.dao.SysNoticeDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 通知service
 */
@Service("sysNoticeService")
public class SysNoticeService {

    @Resource
    private SysNoticeDao sysNoticeDao;

    /**
     * 缓存数据库
     */
    @Resource
    public RedisUtil redisUtil;

    /**
     * 保存
     *
     * @param notice
     * @return
     */
    public void save(SysNotice notice) {
        sysNoticeDao.save(notice);
    }


    /**
     * 删除
     *
     * @param notice
     */
    public void delete(SysNotice notice) {
        sysNoticeDao.delete(notice);
    }


    /**
     * 推送
     *
     * @param notice
     */
    public void push(SysNotice notice) {
        SysNotice sysNotice = sysNoticeDao.getOne(notice.getId());
        // 创建推送对象
        SysPush push = new SysPush();
        push.setId(UUID.randomUUID().toString());
        push.setCallbackUrl(notice.getCallbackUrl());
        push.setCode(notice.getCode());
        push.setContent(notice.getContent());
        push.setTitle(notice.getTitle());
        push.setPushDate(new Date());
        push.setRead(false);
        push.setPushState(0);
        List<SysPush> pushList;
        // 遍历所有要推送的角色
        for (SysRole role : sysNotice.getRoleList()) {
            // 遍历所有发送的用户
            for (SysUser user : role.getUserList()) {
                // 将该用户全部推送取出
                pushList = redisUtil.mapGet("USER_PUSH", user.getId(), SysPush.class);
                if (pushList == null) {
                    pushList = new ArrayList<>();
                }
                // 追加推送消息
                pushList.add(push);
                redisUtil.mapSet("USER_PUSH", user.getId(), pushList);
            }
        }
    }



    /**
     * 消息已读
     * @param map 修改消息参数
     */
    public void read (HashMap<String, Object> map) {
        SysUser user = (SysUser) map.get("user");
        List<SysPush> pushList;
        if(map.get("push") == null){
            pushList = (List<SysPush>) map.get("pushList");
        } else {
            pushList = new ArrayList<>();
            pushList.add((SysPush) map.get("push"));
        }
        List<SysPush> list = redisUtil.mapGet("USER_PUSH", user.getId(), SysPush.class);
        // 在缓存中找到消息
        for (SysPush read : pushList) {
            for (SysPush push : list) {
                if (push.getId().equals(read.getId())) {
                    push.setRead(true);
                }
            }
        }
        redisUtil.mapSet("USER_PUSH", user.getId(), list);
    }



    /**
     * 消息全部已读
     * @param user 操作用户
     */
    public void readAll (SysUser user) {
        List<SysPush> list = redisUtil.mapGet("USER_PUSH", user.getId(), SysPush.class);
        // 在缓存中找到消息
        for (SysPush push : list) {
            push.setRead(true);
        }
        redisUtil.mapSet("USER_PUSH", user.getId(), list);
    }

    /**
     * 消息删除
     * @param map 要删除的消息 user，push
     */
    public void del (HashMap<String, Object> map) {
        SysUser user = (SysUser) map.get("user");
        SysPush info = (SysPush) map.get("info");
        List<SysPush> list = redisUtil.mapGet("USER_PUSH", user.getId(), SysPush.class);
        SysPush delPush = null;
        // 在缓存中找到消息
        for(SysPush push : list) {
            if(push.getId().equals(info.getId())){
                delPush = push;
                break;
            }
        }
        // 删除消息
        list.remove(delPush);
        redisUtil.mapSet("USER_PUSH", user.getId(), list);
    }
}
