<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Activiti</title>
    <style type="text/css">
        embed {
            display: block;
            width: 1000px;
            height: 450px;
        }

        .main-body {
            font-size: 14px;
            margin-top: 10px;
        }
    </style>
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script type="text/javascript">
        $(function () {
            // 开始流程
            $(".start").bind("click", function () {
                $.ajax({
                    type: 'GET',
                    url: '/start',
                    data: {
                        'instanceKey': 'leave',
                        "jobNumber": $("#jobNumber").val()
                    },
                    error: function () {
                        alert('请求失败');
                    },
                    success: function (data) {
                        $("#instanceId").val(data);
                    }
                });
            });

            // 审批
            $(".apply").bind("click", function () {
                $.ajax({
                    type: 'GET',
                    url: '/apply',
                    data: {
                        'instanceId': $("#instanceId").val(),
                        'taskId': $("#taskId").val(),
                        'jobNumber': $("#jobNumber").val(),
                        'leaveDays': $("#leaveDays").val(),
                        'reason': $("#reason").val()
                    },
                    error: function () {
                        alert('请求失败');
                    },
                    success: function (data) {
                        $(".main-body").append('<p>' + data + '</p>');
                    }
                });
            });

            // 绑定查看流程图
            $(".show-img").bind("click", function () {
                var instanceId = $("#instanceId").val();
                if (instanceId == "") {
                    alert("暂无流程！");
                    return;
                }
                var imgHtml = '<embed src="/showImg?instanceId=' + instanceId + '"/>';
                $(".result-div").html(imgHtml);
            });

            // 查看任务
            $(".show-task").bind("click", function () {
                $.ajax({
                    type: 'GET',
                    url: "/showTask",
                    data: null,
                    dataType: 'json',
                    error: function () {
                        alert('请求失败');
                    },
                    success: function (data) {
                        $(".main-body").append('<p>' + JSON.stringify(data) + '</p>');
                    }
                });
            });
        });
    </script>
</head>

<body>
<div>
    <button class="start">开始流程</button>
    <button class="apply">审批</button>
    <div>
        <input type="text" id="taskId" value="" style="width: 300px" placeholder="taskId">
        <input type="text" id="jobNumber" value="A001" style="width: 80px" placeholder="工号">
        <input type="text" id="leaveDays" value="5" style="width: 80px" placeholder="请假天数">
        <input type="text" id="reason" value="no reason" style="width: 150px" placeholder="请假原因">
    </div>
</div>

<hr>
<div>
    <button class="show-img">查看流程图(instanceId)</button>
    <button class="show-task">查看任务列表</button>
</div>

<br/>
流程实例ID:<input type="text" id="instanceId" placeholder="instanceId" style="width: 500px" value=""/>

<div class="main-body"></div>
<div class="result-div"></div>
</body>
</html>
