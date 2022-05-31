<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width,user-scalable=no,shrink-to-fit=no,initial-scale=1,maximum-scale=1,minimum-scale=1"/>
    <link rel="icon" href="/static/favicon.ico" type="image/x-icon">
    <title>Index</title>
</head>
<body>

<form action="/im/chat" method="get">
    登陆ID：<input type="text" name="loginId" value="" placeholder="登陆ID(必填)"> <br/>
    对方ID：<input type="text" name="otherId" value="" placeholder="对方ID(选填)"> <br/><br/>
    <button type="submit">登陆到聊天页</button>

    <p>在线用户</p>
    <ul>
        <#list onlines as u>
            <li>${u.name}</li>
        </#list>
        <#if onlines?size == 0>空</#if>
    </ul>

    <h3>支持功能</h3>
    <ol>
        <li>独立对话和列表选择对话</li>
        <li>对话列表、消息列表</li>
        <li>在线标识、未读数量</li>
        <li>断线重连</li>
        <li>语音消息，最大60s</li>
        <li>消息已读回执</li>
        <li>文本、表情、图片消息、链接</li>
        <li>下拉历史消息</li>
        <li>消息提示音</li>
    </ol>

    <h3>说明</h3>
    <ol>
        <li>登陆ID为t_user表的id字段</li>
        <li>暂未支持安全功能</li>
        <li>代码仅供参考</li>
    </ol>
</form>

</body>
</html>