package com.jsf.controller;

import com.jsf.service.UserService;
import com.jsf.utils.ActivitiUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description: 请假流程测试
 * User: xujunfei
 * Date: 2021-04-15
 * Time: 14:02
 */
@Controller
public class ActivitiController {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    //流程定义和部署相关的存储服务
    @Autowired
    private RepositoryService repositoryService;

    //流程运行时相关的服务
    @Autowired
    private RuntimeService runtimeService;

    //节点任务相关操作接口
    @Autowired
    private TaskService taskService;

    //历史记录相关服务接口
    @Autowired
    private HistoryService historyService;

    //自己定义的用户组服务类
    @Autowired
    private UserService userService;


    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 开始流程
     *
     * @param instanceKey xml中定义的ID
     * @return
     */
    @GetMapping("/start")
    @ResponseBody
    public Object start(String instanceKey, String jobNumber) {
        if (instanceKey == null) {
            return "instanceKey为空";
        }
        logger.info("开启{}流程...", instanceKey);

        // 设置流程参数，开启流程
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jobNumber", jobNumber);
        // map.put("bizData", "your data");
        // 使用流程定义的key启动流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceKey, map);

        logger.info("启动流程实例成功:{}", instance);
        logger.info("流程实例ID:{}", instance.getId());
        logger.info("流程定义ID:{}", instance.getProcessDefinitionId());

        // 通过查询正在运行的流程实例来判断
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        // 根据流程实例ID来查询
        List<ProcessInstance> runningList = processInstanceQuery.processInstanceId(instance.getProcessInstanceId()).list();
        logger.info("根据流程ID查询条数:{}", runningList.size());

        // 查询任务
        Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult();
        // 指派用户，第一个参数任务ID，第二个参数为具体的候选用户名
        taskService.claim(task.getId(), userService.getUser(0));

        // 返回流程ID
        return instance.getId();
    }

    /**
     * 审核
     * <p>此接口可以重复使用(审核)</p>
     *
     * @param instanceId
     * @param taskId
     * @param jobNumber  员工号
     * @param leaveDays  请假天数
     * @param reason     请假原因
     * @return
     */
    @GetMapping("/apply")
    @ResponseBody
    public Object apply(String instanceId, String taskId, String jobNumber, Integer leaveDays, String reason) {
        if (instanceId == null || taskId == null) {
            return "ID为空";
        }
        // 查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            logger.info("任务ID:{}查询到任务为空！", taskId);
            return "任务为空";
        }

        Map<String, Object> v1 = taskService.getVariables(taskId);
        logger.info("任务ID:{}，提交参数:{}", taskId, v1);

        // 参数传递并提交申请
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jobNumber", jobNumber);
        map.put("days", leaveDays);
        map.put("date", LocalDate.now().toString());
        map.put("reason", reason);
        taskService.complete(task.getId(), map);
        logger.info("执行审核环节，任务ID:{}", task.getId());

        Task taskNext = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();
        if (taskNext != null) {
            taskService.claim(taskNext.getId(), userService.getUser(1)); // 指派用户
            logger.info("新任务ID:{}", taskNext.getId());
            return "审核成功";
        } else {
            return "审核成功，流程结束";
        }
    }

    /**
     * 查看任务
     *
     * @return
     */
    @GetMapping("/showTask")
    @ResponseBody
    public Object showTask() {
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee(userService.getUser(0)) // 被指派用户
                .list();
        if (taskList == null || taskList.size() == 0) {
            logger.info("查询任务列表为空！");
            return "null";
        }

        // 查询所有任务，并封装
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (Task task : taskList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("taskId", task.getId());
            map.put("name", task.getName());
            map.put("createTime", task.getCreateTime().toString());
            map.put("assignee", task.getAssignee());
            map.put("instanceId", task.getProcessInstanceId());
            map.put("executionId", task.getExecutionId());
            map.put("definitionId", task.getProcessDefinitionId());
            resultList.add(map);
        }

        logger.info("返回集合:{}", resultList.toString());
        return resultList;
    }

    /**
     * 查看当前流程图
     *
     * @param instanceId
     * @param response
     */
    @GetMapping(value = "/showImg")
    @ResponseBody
    public void showImg(String instanceId, HttpServletResponse response) {
        logger.info("查看完整流程图！流程实例ID:{}", instanceId);
        if (StringUtils.isBlank(instanceId)) return;

        //获取流程实例
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (processInstance == null) {
            logger.error("流程实例ID:{}没查询到流程实例！", instanceId);
            return;
        }

        // 根据流程对象获取流程对象模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        // 查看已执行的节点集合，获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
        // 构造历史流程查询
        HistoricActivityInstanceQuery historyInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId);
        // 查询历史节点
        List<HistoricActivityInstance> historicActivityInstanceList = historyInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();
        if (historicActivityInstanceList == null || historicActivityInstanceList.size() == 0) {
            logger.info("流程实例ID:{}没有历史节点信息！", instanceId);
            outputImg(response, bpmnModel, null, null);
            return;
        }
        // 已执行的节点ID集合(将historicActivityInstanceList中元素的activityId字段取出封装到executedActivityIdList)
        List<String> executedActivityIdList = historicActivityInstanceList.stream().map(item -> item.getActivityId()).collect(Collectors.toList());

        // 获取流程走过的线
        // 获取流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        List<String> flowIds = ActivitiUtils.getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);

        // 输出图像，并设置高亮
        outputImg(response, bpmnModel, flowIds, executedActivityIdList);
    }

    /**
     * 输出图像
     *
     * @param response
     * @param bpmnModel
     * @param flowIds
     * @param executedActivityIdList
     */
    private void outputImg(HttpServletResponse response, BpmnModel bpmnModel, List<String> flowIds, List<String> executedActivityIdList) {
        InputStream imageStream = null;
        try {
            ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            imageStream = generator.generateDiagram(bpmnModel, executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", true, "png");
            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            logger.error("流程图输出异常！", e);
        } finally { // 流关闭
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
