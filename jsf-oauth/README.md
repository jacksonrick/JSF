## OAuth服务
### oauth-server 授权服务
### oauth-resource 资源服务
### oauth-client 客户端模式
### oauth-sso1 授权码模式
**前后端分离**
1、请求接口未登录自动跳转到单点登陆页面
2、支持自定义重定向页面
3、使用Token获取资源服务器接口

### oauth-sso2 授权码模式
**SSO单点登陆**

### oauth-sso3 授权码+客户端模式
**spring security5x**
1、支持自定义principal
2、rabc和hasrole权限控制
3、oauthRestTemplate支持
4、全局异常

**备注**
新版本的springboot oauth（2.6x）已将OAuth认证模块迁移到spring security5x，
多数注解或类标注已过时，如@EnableAuthorizationServer等，不影响当前使用，若升级改造，改动较大，
详见https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide官方迁移文档
