package com.cylt.rabbitMQ.config;


public class RabbitMQDictionary {


    /**
     * 总交换机
     */
    public final static String ROOT = "ROOTS";

    /**
     * 路由器：系统
     */
    public final static String SYS = "SYS";
    /**
     * 交换机：任务
     */
    public final static String TASK = "TASK";
    /**
     * 交换机：日志
     */
    public final static String LOG = "LOG";


    /**
     * 操作常量:delete
     */
    public final static String DELETE = "delete";
    /**
     * 操作名:save
     */
    public final static String SAVE = "save";
}
