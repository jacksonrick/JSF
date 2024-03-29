<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <#include "include.ftl"/>
    <style type="text/css">
        .red {
            background-color: red !important;
            color: white !important;;
        }
    </style>
    <link href="/static/library/plugins/dropzone/dropzone.css" rel="stylesheet" type="text/css">
</head>

<body class="gray-bg">
<div class="ibox float-e-margins">
    <div class="ibox-content">
        <div class="row">
            <div class="col-md-8">
                <form class="form-inline" method="post" id="queryForm">
                    <label> 昵称/手机号:<input type="text" name="keywords" class="form-control input-sm"></label>
                    <label> 真实姓名:<input type="text" name="realname" class="form-control input-sm"></label>
                    <label> 注册时间:
                        <input type="text" class="form-control input-sm" id="startDate" name="startDate" placeholder="开始时间">
                        <input type="text" class="form-control input-sm" id="endDate" name="endDate" placeholder="结束时间">
                    </label>
                    <button type="button" id="search" class="btn btn-sm btn-info"><i class="fa fa-search"></i></button>
                    <button type="button" id="export" class="btn btn-sm btn-info"><i class="fa fa-share"></i>导出到Excel</button>
                    <button type="button" id="export1" class="btn btn-sm btn-info"><i class="fa fa-share"></i>导出到CSV</button>
                    <button type="button" id="export2" class="btn btn-sm btn-info"><i class="fa fa-share"></i>导出到PDF</button>
                    <button type="button" id="import" class="btn btn-sm btn-info"><i class="fa fa-arrow-down"></i>导入Excel</button>
                </form>
            </div>
            <div class="col-md-4 text-right">
                <button type="button" class="btn btn-primary btn-sm" data-open="modal" data-width="800px" data-height="500px" href="/admin/user/detail" id="btn-add">
                    <i class="fa fa-plus"></i> 添加
                </button>
                <button type="button" class="btn btn-danger btn-sm" id="btn-del"><i class="fa fa-remove"></i> 批量删除</button>
                <button type="button" class="btn btn-warning btn-sm" data-open="modal" data-width="800px"
                        data-height="500px" href="/admin/user/page?mode=1"><i class="fa fa-user"></i> 选择用户
                </button>
            </div>
        </div>
        <table id="table" class="table table-striped table-bordered table-hover" width="100%"></table>
    </div>
</div>

<script src="/static/library/plugins/dropzone/dropzone.js" type="text/javascript"></script>
<script type="text/javascript">
    $.getScript("/static/common/js/enums.js");
    var tables;
    $(function () {
        var columns = [
            CONSTANT.COLUMN.CHECKALL,
            {title: "ID", data: "id"},
            {title: "昵称", data: "nickname", defaultContent: "--"},
            {title: "手机号", data: "phone", defaultContent: "--"},
            {title: "邮箱", data: "email", defaultContent: "--", orderable: false},
            {title: "真实姓名", data: "realname", defaultContent: "--", orderable: false},
            {title: "身份证", data: "idcard", defaultContent: "--", orderable: false},
            {title: "头像", data: "avatar", orderable: false, render: CONSTANT.RENDER.AVATAR},
            {title: "注册时间", data: "createTime", defaultContent: "--"},
            {title: "是否冻结", data: "deleted", render: CONSTANT.RENDER.BOOLEAN},
            {
                title: "操作", data: null, orderable: false,
                render: function (data, type, full, callback) {
                    // btn-edit btn-del
                    var btns = CONSTANT.BUTTON.EDIT("/admin/user/detail?userId=" + full.id);
                    if (full.deleted) {
                        btns += CONSTANT.BUTTON.ENABLE();
                    } else {
                        btns += CONSTANT.BUTTON.DISABLE();
                    }
                    return btns;
                }
            }
        ]
        var $table = $('#table');
        // 默认查询表单id为queryForm
        tables = $table.DataTable($.extend(true, {}, CONSTANT.DEFAULT_OPTION, {
            // lengthChange: true,
            // lengthMenu: [ 20, 50, 75, 100 ],
            // dom: '<"top"i>rt<"bottom"flp><"clear">'
            columns: columns,
            ajax: function (data, callback, settings) {
                CONSTANT.AJAX("/admin/user/list", [[1, "u.id"], [2, "u.nickname"], [3, "u.phone"], [8, "u.id"], [9, "u.is_delete"]], data, callback, settings);
            },
            columnDefs: [
                CONSTANT.BUTTON.CHECKBOXS,
                {"width": "5%", "targets": 1} // 列宽 | className:""
            ],
            drawCallback: function (settings) {
                $(":checkbox[name='check-all']").prop("checked", false);
                /* 自增序列
                    this.api().column(0).nodes().each(function (cell, i) {
                        cell.innerHTML = i + 1;
                    });
                */
            }
        }));

        $("#search").on("click", function () {
            tables.draw();// 查询后不需要保持分页状态，回首页
        });

        // 销毁tables实例
        // tables.destroy();
        /*$("#btn-test").on("click", function () {
            tables.destroy();
            $('#table').html('');
        });*/

        $("#export").click(function () {// 导出到Excel
            var formData = $("#queryForm").serialize();
            location.href = '/admin/user/export?' + formData;
        });
        $("#export1").click(function () {// 导出到CSV
            var formData = $("#queryForm").serialize();
            location.href = '/admin/user/exportCSV?' + formData;
        });
        $("#export2").click(function () {// 导出到Pdf
            var formData = $("#queryForm").serialize();
            location.href = '/admin/user/exportPDF?' + formData;
        });

        $("#import").click(function () {// 导入Excel
            $.UploaderExcel({
                action: '/admin/user/import?param=user',
                callback: function (data, idx) {
                    if (data.code == 0) {
                        showMsg(data.msg, 1, function () {
                            layer.close(idx);
                            //reload();
                        });
                    }
                }
            });
        });


        $("#btn-del").click(function () {// 批量删除
            var arrItemId = [];
            $("tbody :checkbox:checked", $table).each(function (i) {
                var item = tables.row($(this).closest('tr')).data();
                arrItemId.push(item.id);
                //$(this).closest('tr').remove(); // 删除
            });
            if (arrItemId.length > 0) {
                Ajax.ajax({
                    url: "/admin/user/deleteBatch",
                    params: {"ids": arrItemId.join(",")},
                    success: function (data) {
                        if (data.code == 0) {
                            showMsg(data.msg, 1, function () {
                                reload();
                            });
                        } else {
                            showMsg(data.msg, 2);
                        }
                    }
                });
            } else {
                showMsg("请选择用户", 2);
            }
        });

        // 单元格事件绑定
        $table.on("change", ":checkbox", function () {
            if ($(this).is("[name='check-all']")) {// 全选
                $(":checkbox", $table).prop("checked", $(this).prop("checked"));
            } else {// 一般复选
                var checkbox = $("tbody :checkbox", $table);
                $(":checkbox[name='check-all']", $table).prop('checked', checkbox.length == checkbox.filter(':checked').length);
            }
        }).on("click", ".btn-edit", function () {// 点击编辑按钮
            //var item = tables.row($(this).closest('tr')).data();
        }).on("click", ".btn-enable", function () {// 点击删除按钮
            var item = tables.row($(this).closest('tr')).data();
            //$(this).closest('tr').remove();
            layerConfirm('确定要删除或启用该用户吗', "/admin/user/enable?userId=" + item.id, function () {
                reload();
            });
        });

        // 行点击事件
        // $('#table tbody').on('click', 'tr', function () {
        //     $(this).closest('tr').toggleClass("red");
        // });

        datePicker('#startDate,#endDate', "yyyy-mm-dd");
    });

    // 不改变当前页
    function reload() {
        tables.draw('page');
    }

    function dealUser(data) {
        if (data.length > 0) {
            var s = [];
            for (var d of data) {
                s.push(d.nickname)
            }
            layer.alert('您选择了：' + s.join(","));
        }
        console.log(data);
    }
</script>
</body>
</html>