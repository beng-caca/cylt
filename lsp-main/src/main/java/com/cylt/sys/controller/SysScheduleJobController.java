package com.cylt.sys.controller;

import com.cylt.common.base.controller.BaseController;
import com.cylt.common.base.pojo.Page;
import com.cylt.pojo.sys.SysScheduleJob;
import com.cylt.sys.service.SysScheduleJobService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务Controller
 */
@Controller
@RequestMapping(value = "sys/job", produces = "text/html;charset=utf-8")
public class SysScheduleJobController extends BaseController {

    /**
     * 角色Service
     */
    @Resource
    private SysScheduleJobService sysScheduleJobService;

    /**
     * 分页查询列表
     * @param job 任务
     * @param page 分页
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public String list(SysScheduleJob job, Page page) throws Exception {
        page = sysScheduleJobService.list(job, page);
        return getJson(page);
    }


    /**
     * 不分页查询列表
     * @param job 任务检索条件
     * @return 任务列表
     */
    @ResponseBody
    @RequestMapping(value = "noPageList")
    public String list(SysScheduleJob job) {
        List<SysScheduleJob> list = sysScheduleJobService.list(job);
        return getJson(list);
    }

    /**
     * 根据ID取得任务
     * @param id ID
     * @return 字典
     */
    @ResponseBody
    @RequestMapping(value = "get")
    public String get(String id) {
        return getJson(sysScheduleJobService.get(id));
    }

    /**
     * 保存任务
     * @param job 任务
     * @return 保存结果
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public String save(SysScheduleJob job) throws Exception {
        String msg = sysScheduleJobService.save(job);
        if(!"保存成功".equals(msg)){
            return responseFail(msg);
        }
        return responseSuccess();
    }

    /**
     * 删除任务
     * @param job 任务
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "delete")
    public String delete(SysScheduleJob job) throws Exception {
        sysScheduleJobService.delete(job);
        return responseSuccess();
    }


}
