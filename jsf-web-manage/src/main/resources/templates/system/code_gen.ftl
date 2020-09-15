<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <#include "include.ftl"/>
    <style type="text/css">
        #btns {
            text-align: center;
            position: fixed;
            bottom: 0px;
            width: 100%;
            background-color: #fff;
            border-top: solid 2px #ccc;
            padding: 8px;
        }

        #tables {
            margin-top: 10px;
            margin-bottom: 50px;
        }

        .btn-tbl {
            color: #333;
            background-color: #fff;
            border: solid 1px #ccc;
            display: inline-block;
            text-align: center;
            vertical-align: middle;
            cursor: pointer;
            white-space: nowrap;
            padding: 6px 12px;
            font-size: 14px;
            line-height: 1.42857143;
            border-radius: 50px;
            margin: 0px 8px 8px 0px;
        }

        .btn-tbl.active {
            background-color: #5cb85c;
            color: #ffffff;
            border-color: #4cae4c;
        }
    </style>
</head>

<body>
<div class="ibox-content">
    <div class="alert alert-warning mb5">
        "数据库连接"如果不填写，将使用当前系统连接的数据库，数据库连接串格式：url|username|password；<br>
        "额外参数"目前仅在选择PostgreSQL时填写模式名称，不填默认public；<br>
        先点击查询，再点击选中需要生成的表，不选择默认生成全部。
    </div>
    <div class="form-inline">
        <label>数据库类型:
            <select class="form-control input-sm" id="dbType">
                <option value="mysql">MySQL</option>
                <option value="postgresql">PostgreSQL</option>
            </select>
        </label>
        <label>额外参数:<input type="text" id="extra" class="form-control input-sm" placeholder="额外参数"></label>
        <button id="btn-query" class="btn btn-success btn-sm">查询</button>
        <br>
        <label style="width: 90%">数据库连接:<input type="text" id="jdbcs" class="form-control input-sm" style="width: 100%" placeholder="url|username|password"></label>
    </div>

    <div id="tables"></div>
</div>
<div id="btns">
    <button type="button" class="btn btn-success btn-rounded btn-gen" data-action="root">生成到项目根目录</button>
    <button type="button" class="btn btn-success btn-rounded btn-gen" data-action="zip">打包生成</button>
    <button type="button" class="btn btn-warning btn-rounded btn-gen" data-action="dict">生成数据字典</button>
</div>
</body>

<script type="text/javascript">
    $(function () {
        $("#btn-query").click(function () {
            Ajax.ajax({
                url: '/admin/system/getTables',
                params: {
                    "dbType": $("#dbType").val(),
                    "jdbcs": $("#jdbcs").val(),
                    "extra": $("#extra").val()
                },
                success: function (data) {
                    if (data.code != 0) {
                        layer.alert(data.msg);
                        return;
                    }
                    var html = '';
                    if (data.data.length == 0) {
                        $("#tables").html('未查询到表');
                        return;
                    }
                    data.data.forEach(function (tb) {
                        html += '<span class="btn-tbl">' + tb + '</span>'
                    });
                    $("#tables").html(html);
                }
            });
        });

        $("#tables").on("click", ".btn-tbl", function () {
            if ($(this).hasClass('active')) {
                $(this).removeClass('active');
            } else {
                $(this).addClass("active");
            }
        });

        $(".btn-gen").click(function () {
            var tbls = $(".btn-tbl.active");
            var arr = [];
            $.each(tbls, function (i, tbl) {
                arr.push($(tbl).text());
            });
            var action = $(this).data("action");
            Ajax.ajax({
                url: '/admin/system/genFromTable',
                params: {
                    "action": action,
                    "tbls": arr.join(","),
                    "dbType": $("#dbType").val(),
                    "jdbcs": $("#jdbcs").val(),
                    "extra": $("#extra").val()
                },
                success: function (data) {
                    if (data.code == 0) {
                        showMsg(data.msg, 1, function () {
                            if (action == 'zip') {
                                location.href = data.data;
                            } else if (action == 'dict') {
                                window.open(data.data);
                            }
                        });
                    } else {
                        showMsg(data.msg, 2);
                    }
                }
            });
        });
    });

</script>
</html>