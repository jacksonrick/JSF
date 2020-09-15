<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <#include "include.ftl"/>
    <style type="text/css">
        .conf-btns {
            text-align: center;
            position: fixed;
            bottom: 0px;
            width: 100%;
            background-color: #fff;
            border-top: solid 2px #ccc;
            padding: 8px;
        }

        .panel-group {
            margin-bottom: 50px;
        }
    </style>
</head>

<body class="gray-bg">
<div class="panel-group" id="accordion">
    <#list configs?keys as typ>
        <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="head${typ_index}">
                <h4 class="panel-title">
                    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapse${typ_index}" aria-expanded="false" aria-controls="collapse${typ_index}">
                        ${typ_index + 1}. ${typ}
                    </a>
                </h4>
            </div>
            <div id="collapse${typ_index}" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="head${typ_index}">
                <div class="panel-body">
                    <div class="row">
                        <#list configs[typ] as cfg>
                            <div class="col-md-6">
                                <label for="${cfg.grp}.${cfg.key}" style="width: 100%;">${cfg.descr}
                                    <input type="text" id="${cfg.grp}.${cfg.key}" class="form-control" name="${cfg.key}" value="${cfg.val}" data-id="${cfg.id}" style="width: 100%;"/></label>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </#list>
</div>

<div class="conf-btns">
    <button type="button" id="btn1" class="btn btn-success btn-rounded" style="width: 150px;">保存</button>
    <button type="button" id="btn2" class="btn btn-primary btn-rounded" style="width: 150px;" data-toggle="tooltip" data-placement="top"
            title="如果从数据库修改了配置，需要点击按钮手动刷新到内存">刷新配置缓存
    </button>
</div>

<script type="text/javascript">
    $(function () {
        $("#btn1").click(function () {
            var json = []
            $.each($("input"), function (key, ele) {
                var t = $(ele);
                json.push({
                    'id': t.attr('data-id'),
                    'text': t.val()
                });
            });
            Ajax.ajax({
                url: '/admin/system/sysConfigEdit',
                params: {"json": JSON.stringify(json)},
                success: function (data) {
                    if (data.code == 0) {
                        showMsg(data.msg, 1);
                    } else {
                        showMsg(data.msg, 2);
                    }
                }
            });
        });

        $("#btn2").click(function () {
            Ajax.ajax({
                url: '/admin/system/sysConfigRefresh',
                params: null,
                success: function (data) {
                    showMsg(data.msg, 1);
                }
            });
        });
    });
</script>

</body>
</html>