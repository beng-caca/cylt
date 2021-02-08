package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysJobLog;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.sys.service.SysScheduleJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务Controller
 */
@Api(tags = "任务")
@Controller
@RequestMapping(value = "sys/job", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
public class SysScheduleJobController extends BaseController {

    /**
     * 任务Service
     */
    @Resource
    private SysScheduleJobService sysScheduleJobService;

    /**
     * 分页查询列表
     * @param job 任务
     * @param page 分页
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "list")
    public String list(SysScheduleJob job, Page page) {
        page = sysScheduleJobService.list(job, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param job 任务检索条件
     * @return 任务列表
     */
    @ApiOperation(value = "不分页列表")
    @RequestMapping(value = "noPageList")
    public String list(SysScheduleJob job) {
        List<SysScheduleJob> list = sysScheduleJobService.list(job);
        return getJson(list);
    }

    /**
     * 保存任务
     * @param job 任务
     * @return 保存结果
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "save")
    public String save(SysScheduleJob job) {
        sysScheduleJobService.save(job);
        return responseSuccess();
    }

    /**
     * 删除任务
     * @param job 任务
     * @return 删除结果
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete")
    public String delete(SysScheduleJob job) {
        sysScheduleJobService.delete(job);
        return responseSuccess();
    }


    /**
     * 查询log列表
     * @param jobLog 任务
     * @param page 分页条件
     * @return 查询结果
     */
    @ApiOperation(value = "查询任务log")
    @RequestMapping(value = "logList")
    public String logList(SysJobLog jobLog, Page page) {
        return getJson(sysScheduleJobService.jobLogList(jobLog, page));
    }

}
