## xxljob-autoregister-spring-boot-starter

**********************************

基于 [XXL-JOB](https://www.xuxueli.com/xxl-job/) 的 Spring Boot Starter，通过自动装配和配置化方式，简化执行器的接入、配置和注册流程，让你「引入依赖 + 写配置」就能开始跑定时任务。

## 1、打包

```
mvn clean install
```

## 2、项目中引入

```xml
<dependency>
    <groupId>com.gtbabc</groupId>
    <artifactId>xxl-job-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 3、配置

springboot项目配置文件application.yaml：

```yaml
server:
  port: 9027

xxl:
  job:
    # 是否开启，默认开启
    enabled: true
    admin:
      addresses: http://127.0.0.1:8099/xxl-job-admin
      accessToken: default_token
      #管理员账号
      username: admin
      #管理员密码
      password: 123456
    executor:
      appname: auto-register-executor-test
      address: ""
      ip: 127.0.0.1
      port: 9999
      logpath: D:\\data\\logs\\xxl-job\\jobhandler
      logretentiondays: 30
      # 执行器名称
      title: 自动注册执行器测试 
      # 执行器地址类型：0=自动注册、1=手动录入，默认为0
      addressType: 1
      # 在上面为1的情况下，手动录入执行器地址列表，多地址逗号分隔
      addressList: http://127.0.0.1:9999
```

`XxlJobSpringExecutor`参数配置与之前相同

## 4、添加注解
需要自动注册的方法添加注解`@XxlJobAutoRegister`，不加则不会自动注册

```java
@Service
public class TestService {

    @XxlJob(value = "testXxlJob")
    @XxlJobAutoRegister(cron = "0/30 * * * * ?")
    public void testXxlJob() {
        System.out.println("==================================testXxlJob=================================");
    }
}
```
