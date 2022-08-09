<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>主页</title>
</head>
<body>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />

<h1>主页</h1>
<p>受保护的页面</p>

<p>受保护的请求</p>
<ol>
    <@security.authorize access="hasPermission('','sys:list')">
        <li><a href="/sys/list">list</a></li>
    </@security.authorize>
    <@security.authorize access="hasPermission('','sys:view')">
        <li><a href="/sys/view">view</a></li>
    </@security.authorize>
    <@security.authorize access="hasPermission('','sys:delete')">
        <li><a href="/sys/delete">delete</a></li>
    </@security.authorize>
    <@security.authorize access="hasPermission('','sys:edit')">
        <li><a href="/sys/edit">edit</a></li>
    </@security.authorize>
</ol>
<p>
    <a href="/admin1">admin1</a>
    <a href="/admin2">admin2</a>
</p>

<a href="/logout">退出</a>
</body>
</html>