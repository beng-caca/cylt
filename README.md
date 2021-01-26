# 平台介绍
cylt是一个WEB快速开发平台基础功能包含 菜单、用户、角色、日志、字典、通知、任务；平台支持分库分表、轻量级缓、 分布式、支持微服务应用。
# 平台优势
cylt整体架构清晰、稳定 先进 经典的技术、易于维护、易于扩展。
cylt是一个低代码的开发平台，居右较高的封装度、扩展性、封装不是限制你去做一些事情，而且是在便捷的同时也具有较好的扩展性。
### 技术选型
* 主框架：`Spring Boot2.2`、`Spring Security`
* 持久层：`Spring Data JPA`、`Spring Data Redis`
* 视图层：`Spring MVC`
* 消息中间件：`RabbitMQ`
* 前端组件：`vue2.5`、`vuex3.0` 、`iview3.2`、`vue-router3.0`、`push1.0`、`axios0.19`
### 准备环境
1. 去Oracle官网下载`Java8`环境：
<https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html>
2. 去Apache下载`maven`环境：
<http://maven.apache.org/download.cgi>
### 部署生产环境
* 进入项目根目录执行`mvn install`导入jar包
* 进入项目根路径执行`mvn package`编译java代码
* 编译vue代码
```
1.导入nodejs包
cd cylt-vue
2. 安装yarn 因为npm在国内用实在太慢了有的node包下载不了 （这个安装过了就不用再安装了）
npm i yarn -g
3. 用yarn导入node包
yarn install
4. 编译vue文件
npm run build
```
* 进入`cylt`目录编译docker-compose
```
回到根目录进入cylt目录
cd ../cylt
docker-compose up -d
```
* 等待个几分钟请求<http://localhost>

### 部署开发环境
修改hosts文件 ：
```
127.0.0.1           cylt-redis
127.0.0.1           cylt-rabbitmq
127.0.0.1           cylt-mysql
127.0.0.1           cylt-main
```
要修改哪个文件就把docker中哪个服务停掉 在本地启动
```
docker stop cylt_XXX
```
按照部署生产环境的`第二步`和`第三步`骤重新编译docker环境

如需本地调试 vue 在项目根路径下运行 `cd cylt-vue && npm run dev`
