package com.cylt.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 实现项目启动后自运行
 */
@Component
public class JobSchedule implements CommandLineRunner {

    @Autowired
    private IQuartzService taskService;

    /**
     * 任务调度开始
     *
     * @param strings
     * @throws Exception
     */
    @Override
    public void run(String... strings) throws Exception {
        taskService.timingTask();
    }
}
