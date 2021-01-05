package com.cylt.quartz.job;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("Job4Log")
public class Job {

    public void test() {
        System.out.println("-------------------Job4Log任务执行开始-------------------");
        System.out.println(new Date().toLocaleString());
        System.out.println("-------------------Job4Log任务执行结束-------------------");
    }
}
