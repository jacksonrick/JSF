<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>OAuth2.0统一登录 - 运营平台</title>
    <link rel="stylesheet" href="/static/css/style.css"/>
</head>
<body>

<div id="app">
    <div class="bg">
        <div class="login">
            <div class="form-box">
                <div class="logo-name position-relative"><h2 style="font-size: 30px;">某运营平台</h2>
                    <p class="f13" style="text-transform: capitalize;">operation platform</p>
                    <div id="date" class="date position-absolute position-absolute-right">
                        <em id="em1">10</em><em id="em2">36</em></div>
                </div>
                <div class="el-form loginForm">
                    <div class="el-form-item">
                        <div class="el-form-item__content">
                            <h3 style="color: rgb(65, 93, 185); text-align: center; font-weight: bold;">请选择系统登陆</h3>
                        </div>
                    </div>

                    <div class="sys-name"><a href="http://a.com" target="_self">A系统</a></div>
                    <div class="sys-name"><a href="http://b.com" target="_self">B系统</a></div>

                    <#if logout??>
                        <div style="margin-top: 50px;text-align: center;">
                            <b style="color: red;font-weight: bold;">您已安全退出系统！</b>
                            <a href="javascript:history.back(-1);" style="color: #3e5dbf;">重新登陆</a>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="/static/js/jquery.min.js" type="application/javascript"></script>
<script type="application/javascript">
    $(function () {
        var myDate = new Date();
        var hour = myDate.getHours();
        var minute = myDate.getMinutes();
        $("#em1").html(hour < 10 ? ('0' + hour) : hour);
        $("#em2").html(minute < 10 ? ('0' + minute) : minute);
    })
</script>
</body>
</html>