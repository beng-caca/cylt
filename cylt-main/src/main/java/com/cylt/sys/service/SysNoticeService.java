package com.cylt.sys.service;

import com.cylt.common.SysUser;
import com.cylt.common.base.service.BaseService;
import com.cylt.pojo.sys.SysNotice;
import com.cylt.pojo.sys.SysPush;
import com.cylt.rabbitMQ.config.RabbitMQDictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知Service
 */
@Transactional
@Service("sysNoticeService")
public class SysNoticeService extends BaseService {

    /**
     * 初始化实例参数
     */
    public void setRoutingKey(){
        ROUTING_KEY = RabbitMQDictionary.SYS;
        SERVICE_NAME = "sysNoticeService";
    }

    /**
     * 推送消息
     *
     * @param notice 通知
     */
    public void push(SysNotice notice) {
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, "push", notice, false);
    }

    /**
     * 取出消息
     *
     * @param user 当前登录用户
     */
    public List<SysPush> pop(SysUser user) {
        List<SysPush> list = redisUtil.mapGet("USER_PUSH", user.getId(), SysPush.class);

        // 将已取出的消息状态改成已推送
        List<SysPush> readList = new ArrayList<>();
        SysPush clone;
        for (SysPush push : list) {
            // 这里浅拷贝对象使修改内容不干扰查询结果
            clone = push.clone();
            clone.setPushState(1);
            readList.add(clone);
        }
        redisUtil.mapSet("USER_PUSH", user.getId(), readList);
        return list;
    }


    /**
     * 消息已读
     *
     * @param user 消息所属的用户
     * @param info 要标记删除的数据
     */
    public void read(SysUser user, SysPush info) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("push", info);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, "read", map, false);
    }

    /**
     * 消息已读(批量)
     *
     * @param user     消息所属的用户
     * @param infoList 要标记删除的数据
     */
    public void readAll(SysUser user, List<SysPush> infoList) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("pushList", infoList);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, "read", map, false);
    }

    /**
     * 消息全部已读
     *
     * @param user 消息所属的用户
     */
    public void readAll(SysUser user) {
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, "readAll", user, false);
    }

    /**
     * 消息删除
     *
     * @param info 要删除的消息
     */
    public void delPush(SysUser user, SysPush info) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("push", info);
        rabbitMQUtil.send(RabbitMQDictionary.SYS, SERVICE_NAME, "del", map, false);
    }
}
