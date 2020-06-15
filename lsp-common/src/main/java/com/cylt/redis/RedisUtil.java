package com.cylt.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cylt.common.Redis;
import com.cylt.common.base.pojo.BasePojo;
import com.cylt.common.base.pojo.Page;
import com.cylt.common.base.pojo.Sort;
import com.cylt.common.util.DateUtils;
import com.cylt.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String DIVISION = ":";

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
     * 通过id删除缓存
     *
     * @param pojo 删除数据
     */
    public Boolean del(BasePojo pojo) throws Exception {
        return del(pojo, true);
    }

    /**
     * 通过id删除缓存
     *
     * @param pojo 删除数据
     */
    public Boolean del(BasePojo pojo, boolean isLog) throws Exception {
        String key = getKeyId(pojo);
        return del(key, isLog);
    }


    /**
     * 按照key删除缓存
     *
     * @param key
     * @return
     */
    private Boolean del(String key, boolean isLog) throws Exception {
        BasePojo pojo;
        Set<String> set = redisTemplate.keys(key);
        for (String str : set) {
            key = str;
        }
        if(isLog){
            logger.info("delete:" + key);
        }
        pojo = JSON.parseObject((String) redisTemplate.opsForValue().get(key), BasePojo.class);
        if (pojo == null) {
            return false;
        }
        setRelationship(pojo);
        // 删除关系数据
        del(getRelationship(pojo));
        return redisTemplate.delete(set) != 0;
    }


    /**
     * 批量删除缓存
     *
     * @param pojoList 要删除的数据
     */
    @SuppressWarnings("unchecked")
    public Boolean del(List<BasePojo> pojoList) throws Exception {
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
     * 通过检索条件数据
     *
     * @param rootPojo
     * @return 值
     */
    public BasePojo get(BasePojo rootPojo) {
        return get(getKey(rootPojo), rootPojo.getClass());
    }

    /**
     * 通过id检索数据
     *
     * @param rootPojo
     * @return 值
     */
    public BasePojo getId(BasePojo rootPojo) {
        return get(getKeyId(rootPojo), rootPojo.getClass());
    }

    /**
     * 通过key检索数据
     *
     * @param key
     * @param clazz 泛型
     * @return 值
     */
    public BasePojo get(String key, Class clazz) {
        // 判断是不是级联调用 ，如果是就加个缩进
        StackTraceElement[] ste = new Exception().getStackTrace();
        if (!"setRelationship".equals(ste[1].getMethodName())) {
            logger.info("select:{}", key);
        }
//        else {
//            logger.info("\tselect:{}", key);
//        }
        Set<String> set = redisTemplate.keys(key);
        for (String str : set) {
            key = str;
        }
        BasePojo obj = (BasePojo) JSON.parseObject((String) redisTemplate.opsForValue().get(key), clazz);
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
        String key = getKey(rootPojo, true);
        // 判断是不是级联调用 ，如果是就多打个缩进
        StackTraceElement[] ste = new Exception().getStackTrace();
        if (!"setRelationship".equals(ste[1].getMethodName())) {
            logger.info("select:{}", key);
        } else {
            logger.info("   select:{}", key);
        }
        Set<String> ids = redisTemplate.keys(key);

        List<BasePojo> list = new ArrayList<>();
        BasePojo obj;
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
    public Page list(BasePojo rootPojo, Page page) throws NoSuchFieldException {
        //获取所有属性名 设置key
        String key = getKey(rootPojo, true);
        // 判断是不是级联调用 ，如果是就不循环打log了
        StackTraceElement[] ste = new Exception().getStackTrace();
        if (!"setRelationship".equals(ste[1].getMethodName())) {
            logger.info("select:{}", key);
        } else {
            logger.info("   select:{}", key);
        }
        // 查询该条件
        Set<String> ids = redisTemplate.keys(key);
        // 按条件排序
        ids = sortKeys(ids, rootPojo);
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
        int mexIndex = page.getTotalNumber() < (page.getSinglePage() + index) ? page.getTotalNumber() : (page.getSinglePage() + index);
        // 将set转换成list方便抓取
        List<String> idList = new ArrayList<>(ids);

        // 开始查询分页内数据
        for (; index < mexIndex; index++) {
            obj = JSON.parseObject((String) redisTemplate.opsForValue().get(idList.get(index)), rootPojo.getClass());
            setRelationship(obj);
            // 在这里解析jpa
            list.add(obj);
        }
        page.setPageList(list);
        return page;
    }


    /**
     * 排序sortKeys
     *
     * @param ids
     * @param pojo
     */
    private Set<String> sortKeys(Set<String> ids, BasePojo pojo) throws NoSuchFieldException {
        List<Sort> sortList = pojo.getSort();
        if (sortList == null || sortList.size() == 0) {
            return ids;
        }
        String field;
        List<String> fields = new ArrayList<>();
        List<Boolean> oreders = new ArrayList<>();
        // 遍历转属性名
        for(Sort sort : sortList){
            // 判断该对象里是否有此属性 如果没有就去父类里找
            if (isExistFieldName(sort.getField(), pojo)) {
                field = pojo.getClass().getDeclaredField(sort.getField()).getAnnotation(Column.class).name();
            } else {
                field = pojo.getClass().getSuperclass().getDeclaredField(sort.getField()).getAnnotation(Column.class).name();
            }
            fields.add(field);
        }
        for(Sort sort : sortList){
            oreders.add(sort.getAsc());
        }
        Set<String> sortSet = new TreeSet<>((k1, k2) -> {
            if (compareKey(getKeyField(k1, fields), getKeyField(k2, fields), oreders)) {
                return -1;
            }
            return 1;
        });
        sortSet.addAll(ids);
        return sortSet;

    }

    /**
     * 判断你一个类是否存在某个属性（字段）
     *
     * @param fieldName 字段
     * @param obj       类对象
     * @return true:存在，false:不存在, null:参数不合法
     */
    public static Boolean isExistFieldName(String fieldName, Object obj) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }
        //获取这个类的所有属性
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean flag = false;
        //循环遍历所有的fields
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(fieldName)) {
                flag = true;
                break;
            }
        }

        return flag;
    }


    /**
     * 比较两个key
     *
     * @param v1   value1
     * @param v2   value2
     * @param sortList 排序规则
     */
    private Boolean compareKey(List<String> v1, List<String> v2, List<Boolean> sortList) {
        boolean result = false;
        String v1i;
        String v2i;
        // 遍历所有排序字段一一对比 然后再返回谁大小结果
        for(int i = 0;i < v1.size();i++){
            v1i = v1.get(i);
            v2i = v2.get(i);
            // 如果当前维度排序字段一样 则进入下一个排序维度
            if(v1i.equals(v2i)){
                continue;
            }

            // 判断该字符串是不是数字
            if (StringUtil.isNumeric(v1i) && StringUtil.isNumeric(v2i)) {
                if (Integer.decode(v1i) < Integer.decode(v2i)) {
                    result = true;
                }
            } else if (DateUtils.isValidDate(v1i) && DateUtils.isValidDate(v2i)) {
                if (DateUtils.before(v2i, v1i)) {
                    result = true;
                }
            } else {// 接下来该判断string了
                result = v2i.compareTo(v1i) != 0;
            }

            // 如果不是正序就取反
            if (!sortList.get(i)) {
                result = !result;
            }
            break;
        }
        return result;
    }

    /**
     * 取key属性值
     *
     * @param key    key
     * @param column 属性名
     * @return
     */
    private String getKeyField(String key, String column) {
        // 属性值开始位置
        int start = key.indexOf(DIVISION + column) + column.length() + 2;
        int end = key.indexOf(DIVISION, start);
        return key.substring(start, end);
    }


    /**
     * 批量取key属性值
     *
     * @param key    key
     * @param columns 属性名
     * @return
     */
    private List<String> getKeyField(String key, List<String> columns) {
        List<String> values = new ArrayList<>();
        for(String column : columns){
            values.add(getKeyField(key, column));
        }
        return values;
    }

    /**
     * 保存到redis
     *
     * @param rootPojo 要保存的数据
     */
    public void save(BasePojo rootPojo,long time) throws Exception {
        if (rootPojo == null) {
            return;
        }
        if (null == rootPojo.getId() || "".equals(rootPojo.getId())) {
            rootPojo.setId(UUID.randomUUID().toString());
        }
        // 通过ID查询是否有数据
        String key = getKeyId(rootPojo);
        Set<String> set = redisTemplate.keys(key);
        // 判断有没有这个数据 如果没有则直接新建
        if (set.size() == 0) {
            logger.info("insert:" + key);
            set(rootPojo, time);
            save(getRelationship(rootPojo), time);
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
        if (!data.equals(oldData)) {
            // 如果没有初始化过期时间就按默认的算
            if(time < 0){
                time = getExpire(key);
                if(time == -2){
                    throw new Exception("保存的数据已过期异常:" + key);
                }
            }
            logger.info("update:" + key);
            // 先删除原来的
            del(key, false);
            redisTemplate.delete(key);
            set(rootPojo, time);
        }
        // 保存关系数据
        save(getRelationship(rootPojo), time);
    }

    /**
     * 保存到redis
     *
     * @param rootPojo 要保存的数据
     */
    public void save(BasePojo rootPojo) throws Exception {
        save(rootPojo, -1);
    }

    /**
     * 批量保存到redis（目前没有想到效率比较高的方式 等想到了再改）
     *
     * @param pojoList 要保存的数据
     */
    public void save(List pojoList,long time) throws Exception {
        for (Object pojo : pojoList) {
            save((BasePojo) pojo, time);
        }
    }

    /**
     * 批量保存到redis（目前没有想到效率比较高的方式 等想到了再改）
     *
     * @param pojoList 要保存的数据
     */
    public void save(List pojoList) throws Exception {
        for (Object pojo : pojoList) {
            save((BasePojo) pojo, 0);
        }
    }

    /**
     * 自定义key缓存放入
     *
     * @param rootPojo 储存对象
     * @return true成功 false失败
     */
    public boolean set(BasePojo rootPojo) {
        return set(rootPojo, 0);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param rootPojo 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(BasePojo rootPojo, long time) {
        try {
            //取当前key类型
            String key = getKey(rootPojo);
            redisTemplate.delete(key);
            if (rootPojo.getCreateTime() == null) {
                rootPojo.setCreateTime(new Date());
            }
            rootPojo.setUpdateTime(new Date());
            key = getKey(rootPojo);
            if (time > 0) {
                redisTemplate.opsForValue().set(key, JSON.toJSONString(rootPojo, getJpaFilter(),
                        SerializerFeature.UseSingleQuotes, SerializerFeature.WriteDateUseDateFormat),
                        time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, JSON.toJSONString(rootPojo, getJpaFilter(),
                        SerializerFeature.UseSingleQuotes, SerializerFeature.WriteDateUseDateFormat));
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
     *
     * @param basePojo 实体pojo
     * @return
     */
    private String getKey(BasePojo basePojo) {
        return getKey(basePojo, false);
    }

    /**
     * 取实体类的key
     *
     * @param basePojo     实体pojo
     * @param isVagueQuery 是否取模糊key
     * @return
     */
    private String getKey(BasePojo basePojo, Boolean isVagueQuery) {
        //获取所有属性名 设置key
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(Arrays.asList(basePojo.getClass().getDeclaredFields()));
        fieldList.addAll(Arrays.asList(basePojo.getClass().getSuperclass().getDeclaredFields()));
        StringBuffer buffer = new StringBuffer();
        //表名|-|id=?|-|属性名=?|-|属性名=?|-|属性名=?................
        buffer.append(basePojo.getClass().getAnnotation(Table.class).name()).append(DIVISION);
        buffer.append("id=").append(basePojo.getId() == null || "".equals(basePojo.getId()) ? "*" : basePojo.getId()).append(DIVISION);
        String columnVal = "";
        Object objColumnVal = "";
        //遍历其他属性名
        for (Field field : fieldList) {
            //如果是索引字段就拼接上"列名=value"
            if (field.getAnnotation(Redis.class) != null) {
                try {
                    field.setAccessible(true);//对私有字段的访问取消权限检查。暴力访问。
                    // 先初始化成*
                    columnVal = "*";
                    // 判断如果是date类型的就先格式化下
                    objColumnVal = field.get(basePojo);
                    if ("java.util.Date".equals(field.getGenericType().getTypeName())) {
                        if (objColumnVal != null) {
                            columnVal = DateUtils.formatTime((Date) objColumnVal);
                        }
                    } else {
                        columnVal = (String) objColumnVal;
                    }
                    if (null == columnVal || "".equals(columnVal)) {
                        columnVal = "*";
                    } else if (isVagueQuery) {// 判断是否允许模糊匹配
                        // 判断此字段是不是模糊查询
                        if (field.getAnnotation(Redis.class).vagueQuery()) {
                            columnVal = "*" + columnVal + "*";
                        }
                    }
                    buffer.append(field.getAnnotation(Column.class).name())
                            .append("=").append(columnVal).append(DIVISION);
                } catch (Exception e) {
                    //这里取属性出错了 先略过
                    logger.error(e.getMessage());
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 取实体类的key
     *
     * @param field 实体pojo
     * @return
     */
    private String getJoinTableKey(Field field) {
        // 初始化多对多key模板
        StringBuffer key = new StringBuffer();
        // 表名
        key.append(field.getAnnotation(JoinTable.class).name()).append(DIVISION);
        // 主键
        key.append(field.getAnnotation(JoinTable.class).joinColumns()[0].name()).append("={0}").append(DIVISION);
        // 外键
        key.append(field.getAnnotation(JoinTable.class).inverseJoinColumns()[0].name()).append("={1}").append(DIVISION);
        return key.toString();
    }


    /**
     * 取实体类的key id
     *
     * @param basePojo 实体pojo
     * @return
     */
    private String getKeyId(BasePojo basePojo) {
        StringBuffer buffer = new StringBuffer();
        //表名:id=?:
        buffer.append(basePojo.getClass().getAnnotation(Table.class).name()).append(DIVISION);
        buffer.append("id=").append(basePojo.getId()).append(DIVISION).append("*");
        return buffer.toString();
    }

    /**
     * 取对象关系
     *
     * @param basePojo 业务对象
     */
    private void setRelationship(BasePojo basePojo) {
        if (basePojo == null) {
            return;
        }
        //获取所有属性名 设置key
        Field[] values = basePojo.getClass().getDeclaredFields();
        BasePojo children;
        Field pid = null;
        //遍历其他属性名
        try {
            for (Field field : values) {
                // 判断当前字段是否为一对多
                if (field.getAnnotation(OneToMany.class) != null) {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    children = newInstance(field);
                    pid = children.getClass().getDeclaredField(oneToMany.mappedBy());
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    pid.setAccessible(true);

                    pid.set(children, basePojo.getId());
                    Object obj = list(children);
                    field.set(basePojo, obj);
                    // 判断当前字段是否为多对多
                } else if (field.getAnnotation(ManyToMany.class) != null) {
                    //key
                    String keyTemplate;
                    List<BasePojo> list = new ArrayList<>();
                    if (field.getAnnotation(JoinTable.class) != null) {
                        // 初始化多对多key模板
                        keyTemplate = getJoinTableKey(field);
                        String key = MessageFormat.format(keyTemplate, basePojo.getId(), "*");
                        Set<String> relationshipIds = redisTemplate.keys(key);
                        //因为*是正则表达式里的关键字 所以没法调用 只能把它换成~
                        key = key.replace("*", "~");
                        //创建遍历临时存放id的变量
                        BasePojo pojoId;
                        for (String id : relationshipIds) {
                            pojoId = newInstance(field);
                            pojoId.setId(id.replace(key.split("~")[0], "")
                                    .replace(key.split("~")[1], ""));
                            list.add(get(pojoId));
                        }
                        field.setAccessible(true);
                        field.set(basePojo, list);
                    } else {
                        //TODO 反向关联这里还没写 相互级联查询会死循环 想想办法解决
                    }
                    // 判断当前字段是否为多对一
                } else if (field.getAnnotation(ManyToOne.class) != null) {
                    field.setAccessible(true);
                    BasePojo join = (BasePojo) field.get(basePojo);
                    if (join != null) {
                        field.set(basePojo, getId(join));
                    }

                    // 判断当前字段是否为多对多
                } else if (field.getAnnotation(OneToOne.class) != null) {
                    // TODO 一对一的等遇到了在写

                }
            }
        } catch (NoSuchFieldException noSuchFieldException) {
            logger.error(noSuchFieldException.getMessage());
        } catch (IllegalAccessException accessException) {
            logger.error(accessException.getMessage());
        } catch (IllegalArgumentException argumentException) {
            logger.error(argumentException.getMessage());
        }
    }

    /**
     * new一个未知数组的泛型
     *
     * @param field
     * @return
     */
    private BasePojo newInstance(Field field) {
        BasePojo pojo = null;
        try {
            // 当前集合的泛型类型
            Type genericType = field.getGenericType();
            ParameterizedType pt = (ParameterizedType) genericType;
            // 得到泛型里的class类型对象
            Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
            pojo = (BasePojo) actualTypeArgument.newInstance();
        } catch (Exception e) {
            logger.error("取级联数据发生了问题", e.getMessage());
        }
        return pojo;
    }

    /**
     * 保存对象关系
     *
     * @param basePojo 业务对象
     */
    private List<BasePojo> getRelationship(BasePojo basePojo) throws Exception {
        //获取所有属性名 设置key
        Field[] values = basePojo.getClass().getDeclaredFields();
        List<BasePojo> relationshipList = new ArrayList<>();
        //临时多对多key
        String key;
        BasePojo children;
        Field many;
        //遍历其他属性名
        try {
            for (Field field : values) {
                // 判断当前字段是否为一对多
                if (field.getAnnotation(OneToMany.class) != null) {
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);

                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    // 判断一下是否先删除一下 重新同步
                    if (oneToMany.orphanRemoval()) {
                        // 取到关系对象实体
                        children = newInstance(field);
                        // 取到关系字段实体 并将主表id set到关系表的关联字段里
                        many = children.getClass().getDeclaredField(oneToMany.mappedBy());
                        many.setAccessible(true);
                        many.set(children, basePojo.getId());
                        del(getKey(children), true);
                    }

                    List<BasePojo> list = (List<BasePojo>) field.get(basePojo);
                    for (BasePojo pojo : list) {
                        relationshipList.add(pojo);
                    }
                    // 判断是否为多对多属性 如果是多对多注解判断是不是维护关系的一方
                } else if (field.getAnnotation(ManyToMany.class) != null && field.getAnnotation(JoinTable.class) != null) {
                    //对私有字段的访问取消权限检查。暴力访问。
                    field.setAccessible(true);
                    // 初始化多对多key模板
                    key = getJoinTableKey(field);
                    //先清空源数据 然后替换成新的
                    Set<String> ids = redisTemplate.keys(MessageFormat.format(key, basePojo.getId(), "*"));
                    redisTemplate.delete(ids);
                    if (field.get(basePojo) == null) {
                        continue;
                    }
                    //读取当前多对多关系数据 并保存到
                    List<BasePojo> list = (List<BasePojo>) field.get(basePojo);
                    for (BasePojo pojo : list) {
                        redisTemplate.opsForValue().set(MessageFormat.format(key, basePojo.getId(), pojo.getId()), "");
                    }
                } else if (field.getAnnotation(OneToOne.class) != null) {
                    // TODO 一对一的等遇到了在写

                }
            }
        } catch (Exception e) {
            logger.error("保存关系时发生了问题" + e.getMessage());
            throw new Exception("保存关系时发生了问题");
        }
        return relationshipList;
    }

    /**
     * 过滤掉jpa级联属性
     *
     * @return
     */
    private PropertyFilter getJpaFilter() {
        return (object, name, value) -> {
            try {
                // 反射出当前要序列化的字段
                Field obj = object.getClass().getDeclaredField(name);
                // 判断当前字段如果是jpa的级联属性就不参加序列化
                if (obj.getAnnotation(OneToMany.class) != null || obj.getAnnotation(OneToOne.class) != null
                        || obj.getAnnotation(ManyToMany.class) != null) {
                    return false;
                }
            } catch (NoSuchFieldException e) {
                return true;
            }
            return true;
        };
    }
}
