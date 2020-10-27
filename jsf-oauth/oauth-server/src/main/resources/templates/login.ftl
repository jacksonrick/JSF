<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
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
                <form class="el-form loginForm" action="/authentication/form" onsubmit="return checkForm()" method="post">
                    <div class="el-form-item">
                        <div class="el-form-item__content">
                            <h3 style="color: rgb(65, 93, 185); text-align: center; font-weight: bold;">用户登录</h3>
                        </div>
                    </div>
                    <div class="el-form-item is-error">
                        <div class="el-form-item__content">
                            <div class="el-input el-input--prefix">
                                <input type="text" name="username" autocomplete="off" placeholder="请输入登录账号" class="el-input__inner">
                                <span class="el-input__prefix"><i class="el-input__icon el-icon-user-solid"></i></span>
                            </div>
                            <div class="el-form-item__error username_error" style="display: none">
                                请输入登录账号
                            </div>
                        </div>
                    </div>
                    <div class="el-form-item is-error">
                        <div class="el-form-item__content">
                            <div class="el-input el-input--prefix el-input--suffix">
                                <input type="password" name="password" autocomplete="off" placeholder="请输入登录密码" class="el-input__inner">
                                <span class="el-input__prefix"><i class="el-input__icon el-icon-key fb"></i></span>
                                <span class="el-input__suffix"><span class="el-input__suffix-inner">
                                    </span><i class="el-input__icon el-icon-view" onclick="changePwdView()"></i></span>
                            </div>
                            <div class="el-form-item__error password_error" style="display: none">
                                请输入登录密码
                            </div>
                        </div>
                    </div>
                    <div class="el-form-item is-error">
                        <div class="el-form-item__content">
                            <div class="el-input el-input--prefix">
                                <input type="number" name="verify" autocomplete="off" placeholder="验证码" class="el-input__inner" maxlength="4">
                                <span class="el-input__prefix"><i class="el-input__icon el-icon-picture"></i></span>
                                <img src="/valid" onclick="changeNum(this)" class="valid-img">
                            </div>
                            <div class="el-form-item__error verify_error" style="display: none">
                                请输入验证码
                            </div>
                        </div>
                    </div>

                    <div class="el-form-item text-center">
                        <div class="el-form-item__error">
                            <#if msg??>
                                <#if msg == '00'>用户名不存在</#if>
                                <#if msg == '01'>账户已禁用</#if>
                                <#if msg == '02'>账户已锁定，请联系管理员</#if>
                                <#if msg == '03'>密码输入错误</#if>
                                <#if msg == '04'>验证码错误</#if>
                            </#if>
                        </div>
                    </div>
                    <div class="el-form-item text-center">
                        <div class="el-form-item__content">
                            <button type="submit" class="el-button full-width el-button--primary" style="padding: 12px; background-color: rgb(65, 93, 185);">
                                <span>登录</span>
                            </button>
                        </div>
                    </div>
                    <div class="el-form-item">
                        <div class="el-form-item__content">
                            <p style="color: rgb(102, 102, 102); text-align: center;">明天你会感谢今天努力的自己</p></div>
                    </div>
                </form>
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

    checkBrowser();

    function checkBrowser() {
        if ((navigator.userAgent.indexOf('MSIE') >= 0) || navigator.userAgent.indexOf('NET') >= 0) {
            alert("您正在使用IE或IE内核的浏览器，为保证页面正常显示，请更换谷歌、火狐浏览器或支持兼容模式的浏览器。");
        }
    }

    function checkForm() {
        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();
        var verify = $("input[name='verify']").val();
        if (username == "") {
            $(".username_error").show();
            return false;
        } else {
            $(".username_error").hide();
        }
        if (password == "") {
            $(".password_error").show();
            return false;
        } else {
            $(".password_error").hide();
        }
        if (verify == "") {
            $(".verify_error").show();
            return false;
        } else {
            $(".verify_error").hide();
        }
        return true;
    }

    function changePwdView() {
        var password = $("input[name='password']");
        var attr = password.attr("type");
        if (attr == "password") {
            password.attr("type", "text");
        } else {
            password.attr("type", "password");
        }
    }

    function changeNum(obj) {
        var num = Math.floor(Math.random() * 10000);
        obj.src = "/valid?" + num;
    }
</script>
</body>
</html>
