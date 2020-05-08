package com.cylt.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.pojo.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        Set<String> set = redisTemplate.keys(key);
        for(String str : set){
            key = str;
        }
        logger.info("delete:" + key);
        pojo = JSON.parseObject((String) redisTemplate.opsForValue().get(key), pojo.getClass());
        setRelationship(pojo);
        // 删除关系数据
        del(getRelationship(pojo));
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
                // 删除关系数据
                del(getRelationship(pojo));
                id = getKeyId(pojo);
                ids = redisTemplate.keys(id);
                logger.info("delete:" + id);
            }
            return redisTemplate.delete(ids) != 0;

        }
        return false;
    }


    /**
     * 自定义ID获取数据
     *
     * @param rootPojo
     * @return 值
     */
    public BasePojo get(BasePojo rootPojo) {
        //取当前key类型
        String key = getKey(rootPojo);
        // 判断是不是级联调用 ，如果是就加个缩进
        StackTraceElement[] ste = new Exception().getStackTrace();
        if(!"setRelationship".equals(ste[1].getMethodName())){
            logger.info("select:{}", key);
        }
//        else {
//            logger.info("\tselect:{}", key);
//        }
        Set<String> set = redisTemplate.keys(key);
        for(String str : set){
            key = str;
        }
        BasePojo obj = JSON.parseObject((String) redisTemplate.opsForValue().get(key), rootPojo.getClass());
        setRelationship(obj);
        return obj;
    }

    /**
     * 查询数据列表
     *
     * @param rootPojo
     * @return 值
     */
    public Object list(BasePojo rootPojo) {
        //获取所有属性名 设置key
        String key = getKey(rootPojo,true);
        // 判断是不是级联调用 ，如果是就多打个缩进
        StackTraceElement[] ste = new Exception().getStackTrace();
        if(!"setRelationship".equals(ste[1].getMethodName())){
            logger.info("select:{}", key);
        } else {
            logger.info("   select:{}", key);
        }
        Set<String> ids = redisTemplate.keys(key);

        List<BasePojo> list = new ArrayList<>();
        BasePojo obj;
        // 开始查询分页内数据
        for (String id : ids) {
            //转移的问题 去找set方法
            obj = JSON.parseObject((String) redisTemplate.opsForValue().get(id), rootPojo.getClass());
            setRelationship(obj);
            list.add(obj);
        }
        return list;
    }


    /**
     * 查询数据列表
     *
     * @param rootPojo
     * @return 值
     */
    public Page list(BasePojo rootPojo, Page page) {
        //获取所有属性名 设置key
        String key = getKey(rootPojo, true);
        // 判断是不是级联调用 ，如果是就不循环打log了
        StackTraceElement[] ste = new Exception().getStackTrace();
        if(!"setRelationship".equals(ste[1].getMethodName())){
            logger.info("select:{}", key);
        } else {
            logger.info("   select:{}", key);
        }
        Set<String> ids = redisTemplate.keys(key);
        List<BasePojo> list = new ArrayList<>();
        BasePojo obj;

        // 从哪里开始（页数 * 一页显示多少数据）
        int index = 0;
        if (page != null) {
            index = (page.getPageNumber() - 1) * page.getSinglePage();
        } else {
            page = new Page();
        }
        // 赋值一共有多少条
        page.setTotalNumber(ids.size());
        // 要读多少条 如果是最后一页的话就能读多少读多少
        int mexIndex = page.getTotalNumber() < (page.getSinglePage() + index) ? page.getTotalNumber(): (page.getSinglePage() + index);
        // 将set转换成list方便抓取
        List<String> idList = new ArrayList<>(ids);

        // 开始查询分页内数据
        for (;index < mexIndex;index++) {
            obj = JSON.parseObject((String) redisTemplate.opsForValue().get(idList.get(index)),rootPojo.getClass());
            setRelationship(obj);
            // 在这里解析jpa
            list.add(obj);
        }
        page.setPageList(list);
        return page;
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
            if(rootPojo.getCreateTime() == null) {
                rootPojo.setCreateTime(new Date());
            }
            rootPojo.setUpdateTime(new Date());

            redisTemplate.opsForValue().set(key, JSON.toJSONString(rootPojo, getJpaFilter(),
                    SerializerFeature.UseSingleQuotes,SerializerFeature.WriteDateUseDateFormat));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存到redis
     *
     * @param rootPojo 要保存的数据
     */
    public void save(BasePojo rootPojo) {
        if(rootPojo == null){
            return;
        }
        // 通过ID查询是否有数据
        String key = getKeyId(rootPojo);
        Set<String> set = redisTemplate.keys(key);
        // 判断有没有这个数据 如果没有则直接新建
        if(set.size() == 0){
            logger.info("insert:" + key);
            set(rootPojo);
            return;
        }
        for (String str : set) {
            key = str;
        }
        String oldData = (String) redisTemplate.opsForValue().get(key);
        // 把当前数据转换到jsonstring
        String data = JSON.toJSONString(rootPojo, getJpaFilter(), SerializerFeature.UseSingleQuotes,
                SerializerFeature.WriteDateUseDateFormat);
        // 判断新的和旧的是否一样 如果一样则不作操作  如果不一样则做修改
        if(!data.equals(oldData)){
            logger.info("update:" + key);
            redisTemplate.delete(key);
            set(rootPojo);
        }
        // 保存关系数据
        save(getRelationship(rootPojo));
    }

    /**
     * 批量保存到redis（目前没有想到效率比较高的方式 等想到了再改）
     * @param pojoList 要保存的数据
     */
    public void save(List<BasePojo> pojoList) {
        for(BasePojo pojo : pojoList){
            save(pojo);
        }
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
        return getKey(basePojo, false);
    }
    /**
     * 取实体类的key
     * @param basePojo 实体pojo
     * @param isVagueQuery 是否取模糊key
     * @return
     */
    private String getKey(BasePojo basePojo,Boolean isVagueQuery){
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
                    } else if (isVagueQuery) {// 判断是否允许模糊匹配
                        // 判断此字段是不是模糊查询
                        if (field.getAnnotation(Redis.class).vagueQuery()) {
                            columnVal = "*" + columnVal + "*";
                        }
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

    /**
     * 取对象关系
     * @param basePojo 业务对象
     */
    private void setRelationship(BasePojo basePojo){
        if(basePojo == null){
            return;
        }
        //获取所有属性名 设置key
        Field[] values = basePojo.getClass().getDeclaredFields();
        BasePojo children;
        Field pid = null;
        //遍历其他属性名
        try{
            for(Field field : values){
                // 判断当前字段是否为一对多
                if (field.getAnnotation(OneToMany.class) != null) {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    children = (BasePojo) field.getDeclaringClass().newInstance();
                    pid = basePojo.getClass().getDeclaredField(oneToMany.mappedBy());
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    pid.setAccessible(true);

                    pid.set(children, basePojo.getId());
                    Object obj = list(children);
                    field.set(basePojo,obj);
                } else if (field.getAnnotation(OneToOne.class) != null) {
                    // TODO 一对一的等遇到了在写

                }
            }
        } catch (Exception e){
            logger.error("取级联数据发生了问题", e.getMessage());
        }
    }

    /**
     * 保存对象关系
     * @param basePojo 业务对象
     */
    private List<BasePojo> getRelationship(BasePojo basePojo){
        //获取所有属性名 设置key
        Field[] values = basePojo.getClass().getDeclaredFields();
        List<BasePojo> relationshipList = new ArrayList<>();
        //遍历其他属性名
        try{
            for(Field field : values){
                // 判断当前字段是否为一对多
                if (field.getAnnotation(OneToMany.class) != null) {
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    List<BasePojo> list = (List<BasePojo>) field.get(basePojo);
                    for(BasePojo pojo : list){
                        relationshipList.add(pojo);
                    }
                } else if (field.getAnnotation(OneToOne.class) != null) {
                    // TODO 一对一的等遇到了在写

                }
            }
        } catch (Exception e){
            logger.error("取级联数据发生了问题" + e.getMessage());
        }
        return relationshipList;
    }

    /**
     * 过滤掉jpa级联属性
     * @return
     */
    private PropertyFilter getJpaFilter(){
        return (object, name, value) -> {
            try  {
                // 反射出当前要序列化的字段
                Field obj = object.getClass().getDeclaredField(name);
                // 判断当前字段如果是jpa的级联属性就不参加序列化
                if(obj.getAnnotation(OneToMany.class) != null || obj.getAnnotation(OneToOne.class) != null){
                    return false;
                }
            } catch (NoSuchFieldException e) {
                return true;
            }
            return true;
        };
    }
}
