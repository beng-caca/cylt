package com.cylt.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cylt.common.base.pojo.BasePojo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 *
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param pojo 删除数据
     */
    @SuppressWarnings("unchecked")
    public Boolean del(BasePojo pojo) {
        String key = getKeyId(pojo);
        logger.info("delete:" + key);
        Set<String> set = redisTemplate.keys(key);
        return redisTemplate.delete(set) != 0;
    }

    /**
     * 删除树结构缓存 （默认父子关系为 pid）
     *
     * @param pojo 删除数据
     */
    public void delTree(BasePojo pojo) {
        delTree(pojo, "PID");
    }


    /**
     * 删除树结构缓存 （自定义父关系）
     *
     * 注：暂时只删除一层 以后如果需求多的话可以改成删除多层 ，思路 ：把父级关系配置在pojo里 直接取注解
     * @param pojo 删除数据
     */
    public void delTree(BasePojo pojo, String... pidStrs) {
        // 删除当前节点
        del(pojo);
        String key;
        Set<String> keys = new HashSet<>();
        // 删除子节点
        for (String pidStr : pidStrs) {
            key = "*" + pidStr + "=" + pojo.getId() + ":*";
            keys.addAll(redisTemplate.keys(key));
        }
        for (String id : keys) {
            logger.info("delete children:" + id);
        }
        redisTemplate.delete(keys);
    }


    /**
     * 批量删除缓存
     *
     * @param pojoList 要删除的数据
     */
    @SuppressWarnings("unchecked")
    public Boolean del(List<BasePojo> pojoList) {
        Set<String> ids = new HashSet<>();
        String id;
        if (pojoList != null && pojoList.size() > 0) {
            for (BasePojo pojo : pojoList) {
                id = getKeyId(pojo);
                ids = redisTemplate.keys(id);
                logger.info("delete:" + id);
            }
            return redisTemplate.delete(ids) != 0;

        }
        return false;
    }

    //============================String=============================

    /**
     * 自定义ID获取数据
     *
     * @param rootPojo
     * @return 值
     */
    public Object get(BasePojo rootPojo) {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value序列化

        //取当前key类型
        String key = getKey(rootPojo);
        logger.info("select:" + key);
        Set<String> set = redisTemplate.keys(key);
        for(String str : set){
            key = str;
        }
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 查询数据列表
     *
     * @param rootPojo
     * @return 值
     */
    @Transactional
    public Object list(BasePojo rootPojo) {
        //获取所有属性名 设置key
        String key = getKey(rootPojo);
        //取当前key类型
//        String key = rootPojo.getClass().getAnnotation(Redis.class).key();
//        key = key.replace("?", "*");
        logger.info("select:" + key);
        Set<String> set = redisTemplate.keys(key);
        List<BasePojo> list = new ArrayList<>();
        JSONObject jsonObj;
        for(String str : set){
            jsonObj = JSON.parseObject((String) redisTemplate.opsForValue().get(str));
            list.add(jsonObj.toJavaObject(rootPojo.getClass()));
        }
        return list;
    }


    /**
     * 普通缓存放入
     *
     * @param id       键
     * @param rootPojo 值
     * @return true成功 false失败
     */
    public boolean set(String id, BasePojo rootPojo) {
        try {
            //取当前key类型
            String key = getKey(rootPojo);
            key = key.replace("?", id);
            logger.info("save:" + key);
            if(rootPojo.getCreateTime() == null) {
                rootPojo.setCreateTime(new Date());
            }
            if(rootPojo.getUpdateTime() == null) {
                rootPojo.setUpdateTime(new Date());
            }
            redisTemplate.opsForValue().set(key, JSON.toJSONStringWithDateFormat(rootPojo,"yyyy-MM-dd HH:mm:ss"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param rootPojo 值
     * @return true成功 false失败
     */
    public void update(BasePojo rootPojo) {
        del(rootPojo);
        set(rootPojo);
    }

    /**
     * 自定义key缓存放入
     *
     * @param rootPojo 储存对象
     * @return true成功 false失败
     */
    public boolean set(BasePojo rootPojo) {
        return set(rootPojo.getId(), rootPojo);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(BasePojo value, long time) {
        try {
            if (time > 0) {
                //取当前key类型
                String key = getKey(value);
                key.replace("?", value.getId());
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 取实体类的key
     * @param basePojo 实体pojo
     * @return
     */
    private String getKey(BasePojo basePojo){
        //获取所有属性名 设置key
        Field[] values = basePojo.getClass().getDeclaredFields();
        StringBuffer buffer = new StringBuffer();
        //表名:id=?:属性名=?:属性名=?:属性名=?................
        buffer.append(basePojo.getClass().getAnnotation(Table.class).name()).append(":");
        buffer.append("id=").append(basePojo.getId() == null || "".equals(basePojo.getId()) ? "*" : basePojo.getId()).append(":");
        String columnVal;
        //遍历其他属性名
        for(Field field : values){
            //如果是索引字段就拼接上"列名=value"
            if(field.getAnnotation(Redis.class) != null){
                try{
                    field.setAccessible(true);//对私有字段的访问取消权限检查。暴力访问。
                    columnVal = (String) field.get(basePojo);
                    if(null == columnVal || "".equals(columnVal)){
                        columnVal = "*";
                    }
                    buffer.append(field.getAnnotation(Column.class).name())
                            .append("=").append(columnVal).append(":");
                } catch (Exception e){
                    //这里取属性出错了 先略过
                    logger.error(e.getMessage());
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 取实体类的key id
     * @param basePojo 实体pojo
     * @return
     */
    private String getKeyId(BasePojo basePojo){
        StringBuffer buffer = new StringBuffer();
        //表名:id=?:
        buffer.append(basePojo.getClass().getAnnotation(Table.class).name()).append(":");
        buffer.append("id=").append(basePojo.getId()).append(":*");
        return buffer.toString();
    }

}
