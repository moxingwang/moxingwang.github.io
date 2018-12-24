## 谨记
* 做题和应用是对知识的最好理解和记忆。


`notice`:文件存储发生变化

```
https://github.com/moxingwang/collection/blob/master/resources

https://github.com/moxingwang/resource/blob/master



https://raw.githubusercontent.com/moxingwang/collection/master/resources

https://raw.githubusercontent.com/moxingwang/resource/master
```

# 开发点注意事项
* 多个update保证顺序一致性
* 对每个sql负责，去琢磨sql，官方手册搞明白执行原理和过程，如何优化。




# notice
* 简单说明jit













0329聊天点
* 最多是基础集合多线程 
* 然后是mysql优化 
* redis事务 优化
* 好多小公司喜欢问jvm调优 然而他们自己都不用
* 还有就是算法问问 比如洗牌策略 棋子走位存储方式 
* 总体很让我不爽 比如写个单例 我就用内部类的 还不满意
* 说mysql 有什么隐式转换
* 我没听过名字 后来知道原来说的是如果字段数字型 如果字符串查 用不到索引
* 他还问一些名词解释 什么分布式 事务
* 嗯，有一点收获 就是以后看下中间价相关的 比较实用
* 尤其是有些面试官会问你用的亮点技术 这得编一个高难度的，是啊 但如果成熟 把这一套讲下来应该也算
*
*
*
*
*
*


1. Java集合框架源码。
2. 同步，锁。
3. 内存模型。
4. dubbo。
5. kafka。



# 2017年08月26日15:40:11
* UTF8
* [UTF-8编码规则](http://blog.csdn.net/sandyen/article/details/1108168)
> UTF-8是一种变长字节编码方式。对于某一个字符的UTF-8编码，如果只有一个字节则其最高二进制位为0；如果是多字节，其第一个字节从最高位开始，连续的二进制位值为1的个数决定了其编码的位数，其余各字节均以10开头。UTF-8最多可用到6个字节。 
  如表： 
  1字节 0xxxxxxx 
  2字节 110xxxxx 10xxxxxx 
  3字节 1110xxxx 10xxxxxx 10xxxxxx 
  4字节 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx 
  5字节 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 
  6字节 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 
  因此UTF-8中可以用来表示字符编码的实际位数最多有31位，即上表中x所表示的位。除去那些控制位（每字节开头的10等），这些x表示的位与UNICODE编码是一一对应的，位高低顺序也相同。 
  实际将UNICODE转换为UTF-8编码时应先去除高位0，然后根据所剩编码的位数决定所需最小的UTF-8编码位数。 
  因此那些基本ASCII字符集中的字符（UNICODE兼容ASCII）只需要一个字节的UTF-8编码（7个二进制位）便可以表示。 

* Base64
* [Base64笔记](http://www.ruanyifeng.com/blog/2008/06/base64.html)

# 20170824
* 正则表达式
* Mybatis 返回hashmap 
* 仔细聆听他人说问题

# 20170823
* 压缩算法lz77 lz78
* 微服务Rpc restful 
* 分布式事务
* Apache License

# 20170822
* 思维方式：看代码思考为什么，对比自己的思想和作者的设计器想；不但要知道结果还要反推为什么。

# 20170821
* major.version
* maven classifier

# 2017年08月22日15:39:19之前
* 日志查看技巧文章
* Jsp jms spring原理
* 通信原理
* 几大通信协议
* 文章:各种知识书写
* Log4j
* Spring Session策略
* rabbitMQ 数据延迟问题；
* Threadlocal 线程池 线程死亡过程
* Exception
* java代理(动态代理),反射,AOP原理
* java字节码增强
* Docker 搭建
* redis搭建
* MQ搭建
* 写Sql多动脑子,思考优化
* Git 常见使用命令操作
* 为什么 useGeneratedKeys="true"keyProperty="id"
* dubbo原理
* ioc和aop
* Tomcat
* mina（可以忽略）
* spring原理
* 长连接
* Socket
* 阿里云计算课程  https://yq.aliyun.com/articles/62910
* Hadoop
* [JVM性能调优监控工具jps、jstack、jmap、jhat、jstat等使用详解](http://blog.csdn.net/tzs_1041218129/article/details/61630981)
* Https
* 设计模式
* 一致性哈希算法
* 多线程交替输出变量
* tcp/ip
* mq模式 direct headers topic match fanout
* nio
* mysql 其它几本好书
* 通讯原理
* 长连接
* 机器学习
* zookeeper，Hbase，HDFS，Lucene，Redis,Cassandra,MongoDB
* SOA和Web Services（REST, SOAP）
* Jenkins, Maven/Sbt/Npm/Ant, Git/SVN
* storm,spark stream
* ThreadPoolExecutor 多线程创建的理解
* hadoop hive 本地搭建安装
* RandomAccess实现原理
* GC策略好好学习  [垃圾收集](https://www.ibm.com/developerworks/cn/java/i-garbage2/index.html?ca=drs-)
* jvm理解 [深入理解Java内存模型（一）——基础](http://www.infoq.com/cn/articles/java-memory-model-1)
* 继承关系内存分布
* Https流程
* bigdecimal原理
* 子类继承夫类的变量在内存中是如何存放的，比如书同名变量？
* IO
* java对象创建过程
* [Java架构师必会的技能（你都会了吗？）](https://juejin.im/entry/5992b4a65188254891517793?utm_source=gold_browser_extension)


#20171103
本地搭建zookeeper

#20171102
AI学习

#20171030
RateLimiter [RateLimit--使用guava来做接口限流](http://blog.csdn.net/jiesa/article/details/50412027)

#20171026
[Redis基础总结](http://blog.csdn.net/basycia/article/details/52175429)

#20171025
rabbitmq 分布式事务

# 20171024
[Asyncdb（二）：Java IO 初探](https://juejin.im/post/59ee9d965188254115700264?utm_source=gold_browser_extension)

# 20171023
多核心多线程 [多核多线程理解](http://www.cnblogs.com/gtarcoder/p/5295281.html)
看阿里巴巴java开发手册

#20170926
软件许可协议

#20170922
Nginx 服务器停止服务，IO耗尽
TCP nio
pinpoint 安装

#20170919
MySQL 慢查询
jvm内存占用分析
pinpoint调用失败分析

# 20170914
MySQL field [fiedl](https://segmentfault.com/a/1190000003742537)
十六进制英文名称：Hex number system
# 20170908
LinkedBlockingqueue  队列比较 （http://www.cnblogs.com/wzhanke/p/4763356.html）
redis使用
# 20170907
ResultCode 先校验flag再校验code
TransactionTimedOutException

# 20170904
Optional http://www.importnew.com/6675.html

# 20170902
Json-rpc
restful
Rpc

# 20170901
jvm内存
http://www.importnew.com/14486.html
jvm内存参数

# 20170824
正则表达式
Mybatis返回hashmap
仔细聆听他人说问题

# 20170823
压缩算法lz77 lz78
微服务Rpc restful
分布式事务
Apache License

# 20170822
思维方式：看代码思考为什么，对比自己的思想和作者的设计器想；不但要知道结果还要反推为什么。

# 20170821
major.version
maven classifier
