**功能**：监控feign调用，输出适用于prometheus的metrics

**适用范围**：依赖`spring-cloud-openfeign-core`

**使用方式**：
1. pom引入

```xml
<dependency>
    <groupId>link.zzone</groupId>
    <artifactId>commons-feign</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2. 配置文件中增加如下内容
```yaml
feign:
  client:
    monitor:
      enabled: true
```
3. 启动类添加注解```@EnableFeignClientMonitor```

**其他说明**：

spring cloud`Hoxton.SR5`版本及以上或 openfeign`10.9`版本及以上可以直接使用[feign-micrometer](https://mvnrepository.com/artifact/io.github.openfeign/feign-micrometer)

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>Hoxton.SR5</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```
