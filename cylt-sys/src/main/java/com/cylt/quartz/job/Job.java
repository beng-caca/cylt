package com.cylt.quartz.job;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("Job4Log")
public class Job {

    /**
     * 测试定时任务
     * @return log
     */
    public String test() {
        System.out.println(new Date().toLocaleString());
        return "1";
    }
}
