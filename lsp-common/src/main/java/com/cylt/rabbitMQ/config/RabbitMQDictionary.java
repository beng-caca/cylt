package com.cylt.rabbitMQ.config;


public class RabbitMQDictionary {


    //模块名
    public final static String SYS = "sys";

    //分隔符
    public final static String DELIMITER = "_";
    //---------------------------------------------------------------------------------------------
    //功能名:sys_user
    public final static String USER = SYS + DELIMITER + "user";

    //功能名:sys_menu
    public final static String MENU = SYS + DELIMITER + "menu";

    //功能名:sys_role
    public final static String ROLE = SYS + DELIMITER + "role";


    //---------------------------------------------------------------------------------------------
    //基本模块功能

    //操作名:delete
    public final static String DELETE = "delete";
    //操作名:save
    public final static String SAVE = "save";
}
