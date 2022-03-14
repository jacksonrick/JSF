package com.jsf.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jsf.database.mapper.JobsMapper;
import com.jsf.database.model.Jobs;
import com.jsf.utils.system.LogManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务计划Service
 *
 * @author rick
 */
@Service
public class JobService {

    @Resource
    private JobsMapper jobsMapper;

    /**
     * 查询任务
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<Jobs> findJobAndTriggerDetails(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Jobs> list = jobsMapper.findJobAndTriggerDetails();
        PageInfo<Jobs> page = new PageInfo<Jobs>(list);
        return page;
    }

    public int findCountByName(String name) {
        return jobsMapper.findCountByName(name);
    }


    public void test(String name) {
        LogManager.info(name + "任务计划测试", this.getClass());
    }

}
