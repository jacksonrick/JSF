# JSF - An Integrated Java Springboot/Cloud Framework
##### Version `v6.0.4`
Developed by @jackson-rick  Blog: [https://www.liu-nian.cn](https://www.liu-nian.cn) 


## 模块
* doc `文档/配置/数据库/部署脚本`
* jsf-commons `系统配置/数据库配置/处理器/拦截器等`
* jsf-utils `工具类`
* jsf-web-api `API端`
    * API安全
    * 常用接口
    * DEMO：微信/支付宝支付、公众号、扫码登陆
* jsf-web-manage `后台管理端`
    * 模块：权限管理、模块管理、地址、文件管理、常用工具、日志管理、系统日志(可视化)、系统配置
    * 代码生成：可视化；生成实体类、Mapper接口、MapperSQL、Service、Controller、模板页面、菜单、数据字典
* jsf-oauth `OAuth模块`
    * oauth-server `授权服务器`
    * oauth-resource `资源服务器`
    * oauth-sso `SSO客户端`
    * oauth-client `Client模式`
* jsf-gateway `网关模块`
* jsf-registry `注册中心`
    * eureka-server `Eureka注册中心`
    * zookeeper-server `ZK注册中心`
* jsf-demos `集成案例`
    * mservice `微服务|注册服务`
    * activiti `工作流`
    * actuator `Springboot监控`
    * dubbo `Dubbo`
    * elasticsearch `全文搜索`
    * im `即时聊天`
    * influxdb `Influxdb`
    * kafka `Kafka`
    * nacos-config `Nacos分布式配置`
    * quartz `任务调度/UI`
    * rabbitmq `消息队列`
    * redis `缓存`
    * sharding-db `分表分库`
    * spring-security `SpringSecurity`
    * storm `Storm`
    * websocket `WebSocket`
    * websocket-mq `分布式WebSocket`
    * zookeeper `分布式配置和服务发现`
    * retry `重试框架`

### 集成
* Spring/MyBatis/SpringBoot/SpringCloud
* Spring Redis/Redis Session/Redisson/RedisTemplate
* APIToken/API验签/防重/SpringSecurity/OAuth授权/SSO单点登录
* Actuator健康监控、数据源监控
* 日志服务、分布式日志ELK、全局异常处理、多数据源、分布式锁、分布式Session、FastDFS
* PageHelper分页、封装分页组件、Freemarker模板、Freemarker函数和指令
* Quartz(单点/集群)、UI管理、异步任务、线程池
* 第三方：微信SDK/支付宝SDK/JPush推送/高德地图/Geetest/阿里云OSS/短信/邮件
* 工具包：字符、时间、金额、JSON、Http、PDF、License、分页、文件、POI和CSV读写、对象、加密、二维码、图形验证码、ID生成器等
* JS插件：Ajax/DataTables/CityPicker/Layer/DatePicker/Ztree/Select2/Dropzone等

### 环境与配置
* Jdk 1.8
* MySQL 5.7 +
* Postgresql 10 +
* Maven 3.3.9
* Redis 4.0+
* Linux CentOS 7
* IDEA 2020.1
* Docker CE 18+
* 其他：RabbitMQ3.6、Jenkins、Dubbo2.7.1、ElasticSearch6.4.3、Zookeeper3.4.13、Nacos1.3+…
---

### 运行
#### 打包到服务器运行
* 修改生产环境配置
* 安装依赖到本地仓库 
    * 根目录-> clean install
    * cd jsf-utils -> clean install -e
    * cd jsf-commons -> clean install -e
* 在各个端下运行 
    * cd   jsf-web-manage
    * clean package -Dmaven.test.skip=true -e
* 将target目录下的打包文件上传到服务器目录
* 运行`doc/scripts/start.sh`命令
* 注意：运行脚本前，请先查看注释
* 目录说明
    * upload: 文件上传
    * logs: 日志目录

#### Docker
* 直接运行打包命令，将上传到服务器目录，如:/home/docker/web
* 将项目doc/build/docker/docker-compose.yml文件拷贝到项目根目录
* 运行`docker-compose up -d` | `docker-compose start web`

#### Jenkins
可到我博客里查看

### 其他
* 使用maven下载Jar包时，请先在Maven中添加阿里的镜像源
* 数据库脚本：`doc/mysql/database.sql`
* 操作手册说明：`doc/框架使用手册.docx`(很久没更新了`-_-`)
* 默认为MySQL数据库，如使用Postgresql，将数据库脚本替换为`doc/postgresql/public.sql`，并修改相应配置
* jsf-demos模块代码仅供参考

### 声明
本框架不能直接用于生产环境，需要根据业务做一定量调整，如有疑问，请在博客中回复

---

### 更新日志
* v6.0.4 `20210203`
    * 优化springcloud gateway，集成限流、熔断等
    * 新增工具类HttpManager
* v6.0.2 `20201027`
    * SSO登陆页面UI更新，新增禁用和锁定
    * API安全新增接口锁定
    * Excel优化导入导出及注释说明
* v6.0.1 `20201009`
    * 改动优化太多，懒得写
* v5.9.4 `20200904`
    * 5.x最后版本优化
* v5.9.3 `20200812`
    * 聊天功能优化
    * 其他优化
* v5.9.2 `20200806`
    * 新增即时聊天模块`jsf-demos/im`
* v5.9.1 `20200624`
    * 优化utils工具包
    * 新增KafKa示例
* v5.9 `20200617`
    * 集成Nacos配置管理和服务发现
    * 升级springcloud及相关依赖的版本
    * 常规优化~_~
* v5.8.4 `20200611`
    * 新增SpringSecurity示例
    * 优化SSO客户端在前后端分离开发的场景
* v5.8.3 `20200609`
    * 优化请求日志打印，支持ReqestBody注解
    * 新增支持获取指定挂载目录容量的方法
    * 修复POI转换CSV格式问题
* v5.8.2 `20200605`
    * 优化支持session redis存储序列化和@Cacheable注解
    * 优化SSO客户端
* v5.8.1 `20200602`
    * 完善influxdb示例
    * 优化OAuth服务端
    * 新增SSO客户端多个场景示例
    * 修复Excel转CSV问题
* v5.8 `20200512`
    * 新增activiti、influxdb、storm示例代码
    * 整合mservice微服务模块
    * 其他优化
* v5.7.6 `20200323`
    * 新增ZIP解压缩和Shell执行工具
    * 其他优化
* v5.7.5 `20191213`
    * 支持发布到maven私服
    * 优化maven dependencyManagement
    * MyBatis Generate更新
* v5.7.4 `20191118`
    * 新增ShardingJDBC组件
    * 修复升级Springcloud版本导致的问题
    * 优化及修复SSO服务端
    * 整理各模块Maven依赖关系
* v5.7.3 `20191030`
    * SpringBoot升级到2.2
    * 优化配置文件
    * 新增注解开关
    * 优化Redis缓存配置
* v5.7.2 `20190929`
    * 优化SSO及客户端
* v5.7.1 `20190918`
    * 重新设计表字段[后台管理模块]
    * 优化SSO单点登陆
    * 优化代码规范
    * 注：此版本需要重新导入数据库
* v5.7 `20190828`
    * 新增Springboot应用监控测试模块
    * 优化打包方式
    * 优化websocket
    * 优化POI导入导出
* v5.6.4 `20190726`
    * 优化SSO单点登陆
    * 优化POI配置式导入
    * 新增阿里云OSS工具类
* v5.6.3 `20190718`
    * 优化数据库逆向工程工具
    * 新增ElasticSearch模块，支持分词、拼音、高亮查询
* v5.6.1 `20190710`
    * 升级springcloud版本[2.1.2.RELEASE]
    * maven依赖优化
    * 新增zookeeper注册和配置中心
    * 新增多个utils
* v5.6 `20190703`
    * 新增zookeeper注册和配置中心DEMO
    * 新增多个辅助类
    * 新增声明式事务
    * 优化后台页面
    * 其他优化
* v5.5.6 `20190627`
    * 修复POI导入BUG，支持大批量导入
    * 移除core模块entity包
* v5.5.5 `20190624`
    * 常规更新
    * utils模块版本号升级为3.0
* v5.5.4 `20190613`
    * 系统优化，BUG修复
* v5.5.3 `20190515`
    * 新增一些APP端示例接口
    * 更新Pom文件,优化依赖
    * 其他优化👻
* v5.5.2 `20190403`
    * 更新Token前缀，新增数据库token支持
    * 配置优化，配置分离[推荐使用插件打包]
    * 工具类和WebSocket优化
* v5.5.1 `20190321`
    * 简化yml配置，合并成一个文件
    * 新增dubbo实例
    * 新增微信扫码登陆实例
    * 更新docker和nginx配置
* v5.5 `20190308`
    * 更新接口响应数据封装类ResMsg
    * 新增Nacos分布式配置服务
    * 优化测试模块，分离多个子模块
    * 新增RabbitMQ和WebSocket集成
    * 多个工具类优化
* v5.4.3 `20190222`
    * 优化异常处理和日志打印
    * 修复BUG
* v5.4.2 `20190124`
    * 后台管理系统UI更新[去除动画，按钮样式]
    * PDF等工具类更新
    * 其他优化
* v5.4.1 `20181218`
    * 新增springmvc与itextpdf快速导出fm模板
    * 导入Excel支持注解
    * 其它报错、依赖问题解决
* v5.4 `20181212`
    * Excel导出支持JSON配置、实体类注解
    * Excel导入支持实体类注解
    * 新增Nacos分布式配置
* v5.3 `20181203&20181203`
    * 升级Springboot2.1.0
    * 生成数据字典现支持Postgresql
* v5.2.3 `20181113`
    * 更新utils包
    * 优化OAuth单点登录模块
    * 优化Redis&Session序列化
    * 优化APP Token
* v5.2.2 `20181101`
    * 升级WebSocket与OAuth模块配置，并优化注释
    * 优化其他配置
* v5.2.1 `20181019`
    * 新增服务器备份脚本，支持定时、Docker
    * RabbitMQ支持延迟队列等特性
* v5.2 `20180920`
    * 升级Springboot2.0.5
    * 优化Oauth2服务
    * 优化错误日志跟踪显示
    * 工具类优化
* v5.1.1 `20180903`
    * 后台UI更新
    * 新增Excel通过配置读取数据工具类
* v5.1 `20180827`
    * 新增限流模块[RedisLimit]
    * 新增网关链路追踪、优化网关路由
* v5.0.8 beta `20180817`
    * 添加LICENSE
    * 优化一些配置和JS
    * 修复Excel读取2003版本报错的问题
* v5.0.7 beta `20180809`
    * 优化配置
    * 新增RabbitMQ的ACK机制
* v5.0.6 beta `20180727`
    * 移除多数据源功能
    * 新增对额外yml配置的说明
    * 新增网关模块[待完善]
    * 新增对postgres的支持
    * 优化mybaits生成工具和其他工具类
    * 新增Quartz数据库支持和图形化界面、日志可视化
    * 其他日志、任务等**配置**优化
* v5.0.5 beta `20180706`
    * 新增Quartz任务管理器及界面
    * 修改utils包名
* v5.0.4 beta `20180705`
    * 将mybatis mapperxml移至resources目录
    * 修复BUG
* v5.0.3 beta `20180703`
    * 监控接口更改为OAuth请求模式
    * 优化MyBatis对JSON类型的处理
    * 新增Excel导出工具、更换JSON处理包为fastxml
    * 修复多个异常错误的BUG
* v5.0.2 beta `20180619`
    * 优化部分目录结构
    * 新增支持数据库JSON类型的支持
* v5.0.1 beta `20180612`
    * 优化实体类和数据库及数据字典生成工具
    * 新增gateway模块，下个版本将集中开发网关功能
* v5.0.0 beta `20180611`
    * 架构优化，轻量化服务
    * 优化测试模块，集成更多的服务测试
    * 新增OAuth授权服务及三种模式
    * 优化打包，集成Docker配置
    * 移除全部的拦截器，更换为AOP，包括请求日志、权限、统一的错误处理等
    * 解决部分依赖不兼容的问题，修复个别BUG
* v4.8 `20180507`
    * 优化Jar依赖关系
    * 新增AMPQ、ELK等组件
    * 移除model模块
* v4.7.1 `20180423`
    * 完善系统架构文档说明
    * 优化日志、Sys、Redisson等服务的配置
* v4.7 `20180403`
    * 修复BUG
    * 优化参数和部署脚本
* v4.6 beta2 `20180327`
    * 增强系统稳定性
    * 修复BUG
* v4.6 beta1 `20180315`
    * 升级核心框架：SpringBoot 2.0/SpringCloud Finchley.M7
    * 优化各种组件的兼容性问题
    * 优化并简化yml配置
    * 新增测试模块，测试DEMO
    * 其他优化：日志、配置、监控等
* v4.5.1 `20180309`
    * 优化日志输出、拦截器、支付、run脚本
    * 新增二维码生成工具
    * 新增JenkinsAPI管理，可自动部署
* v4.5 `20180303`
    * 服务组件条件化加载，可单独启用或关闭
    * 优化SpringBoot配置、监控服务端UI
    * 新增Quartz，可动态管理任务[原SpringSchedule]
    * 移除Jedis，使用Spring Template
* v4.4.4 `20180227`
    * 添加了服务消费示例[service-order]
    * 分离监控配置文件
    * 整合测试DEMO[web-front]
    * 下个版本将集中优化模块依赖、服务加载和配置
* v4.4.3 `20180225`
    * 优化服务端启动脚本
    * 删除支付宝SDK本地Jar，替换为Maven
    * 修复日志文件名称为undefined的问题
* v4.4.2 `20180208`
    * 服务监控中心和配置中心(cloud-server)
    * 优化日志、上传、定时任务配置
    * 修复Maven打包后程序出现文件找不到的问题
* v4.4.1 `20180202`
    * 新增模块删除和地址导出功能
    * 更新fa图标库，优化CSS效果
    * 服务监控中心[Test]
* v4.4 `20180130`
    * 更新springboot-druid-starter
    * 新增druid数据源监控
    * 多数据源配置和自动切换及事务
    * 修复了部分名称和变量引起的BUG
* v4.3.1 `20180129`
    * 修复若干问题
    * 新增对上传、富文本、Select2等插件的BS验证
    * 优化了js注释和参数命名
    * 优化Tomcat性能
* v4.3 `20180126`
    * 集成Openfire+Smack即时通信服务
    * 集成微信支付SDK，新增多个接口
* v4.2.2 `20180119`
    * 优化日志服务
    * 自动生成实体类工具现可以生成HTML
    * 优化支付宝微信接口
* v4.2.1 `20180116`
    * 集成Spring WebSocket并与Redis Session集成
    * 优化目录结构，暂时关闭不需要的组件服务
    * 通过ConfigurationProperties进行配置
* v4.2 `20180109`
    * 集成spring activemq
    * 集成boot-admin服务监控[front]
    * 优化代码和目录结构
* v4.1 `20180104`
    * 集成Spring Redisson分布式事务
    * Spring Session Redis管理
    * Spring Redis缓存管理
    * FastDFS基于SpringBoot配置
    * 集成SpringCloud服务发现
    * SpringBoot 监控[测试]
* v4.0 `20171218`
    * 基于SpringBoot构建
    * 集中文件配置
    * 更新为Logback日志管理
    * 优化多个核心组件的配置
    * 基于Maven的一键式打包
* v3.7.1 `20171103`
    * 修复BUG
* v3.7 `20171022`
    * 更新页面模板：Freemarker
    * 集成Flume日志
* v3.6.1 `20171016`
    * 新的分页插件：pageHelper
    * 优化dataTables
* v3.6 `20171012`
    * 新增Spring Data Session
    * 升级Spring至4.3.11
    * 优化日志服务
* v3.5.5 `20170930`
    * 新增Quartz管理器
    * 优化日志服务
    * 新增地址插件
    * 优化部分JS插件
* v3.5.4 `20170921`
    * 新增后台模块管理
    * 新增阿里云服务
    * 优化日志服务
* v3.5.3 `20170825`
    * 优化后台页面和部分JS插件
    * 优化支付接口和推送接口
* v3.5.2 `20170801`
    * 优化Redis服务
    * 优化AppToken服务
* v3.5.1 `20170620`
    * 优化SQL查询和实体类查询
    * 新增第三方验证码接口
    * 新增多个Util工具类
    * 新增和优化PC/APP上传插件和服务端
    * 新增Redis服务和AppToken
* v3.4.3 `20170525`
    * 更新部分SQL稳定性
    * 新增第三方支付和推送接口
* v3.4.2 `20170424`
    * 新增FastDFS文件存储
    * 模块分层，优化结构
    * 统一的异常处理
* v3.4.1 `20170324`
    * 新增地址和图片上传组件
    * 优化封装的JS插件
* v1.x `20160601`
    * JFrame项目Start
    * 3.x之前的版本更新记录找不到了～