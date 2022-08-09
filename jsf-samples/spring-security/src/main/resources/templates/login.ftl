<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>登陆</title>
</head>
<body>
<form action="/dologin" method="post">
    <input type="text" name="username" value="admin">
    <input type="password" name="password" value="123456">

    <br>
    <label><input name="remember-me" type="checkbox" value="true">记住我</label>
    <br>
    <button type="submit">登陆</button>
    <br>
    <b style="color: red">${msg!''}</b>
</form>
</body>
</html>