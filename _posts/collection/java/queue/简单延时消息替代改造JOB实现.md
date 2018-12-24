 很多同学在项目中遇到类似这样的需求，触发某一个事件之后在指定的时间后触发其它事件。比如说用户下单之后，如果超过30分支关闭订单；或者下单使用了促销，促销活动在指定的时间过期了如若未付款需要关闭订单。 解决这样的问题，我们通常的做法是加一个job来做这件事情，设置job每隔几分钟跑一次把需要处理的数据线查询出来，然后再去执行。这种解决办法自然是最简单又可靠的，只要job和DB正常运行不会存在漏掉任何任务。这种方法针对不同的使用场景也存在问题，job过快数据库压力大，job慢了业务上不能精确的处理。

![](https://github.com/moxingwang/resource/blob/master/image/%E6%B6%88%E6%81%AF%E5%BB%B6%E8%BF%9F%E6%94%B9%E9%80%A0%E5%89%8D.jpg?raw=true)

 本人的项目中最近刚刚改造过一个这样的场景，顺便记录下来。项目初期业务简单粗暴用户下单后订单未付款订单30分钟关闭，使用了促销的订单，每次在支付的时候去验单必须验证促销活动信息，如果活动过期拦截终止支付请求并且关闭订单，显然这么做用户体验不好。本次改动方向简单（减少数据库查询，业务上更加精确），解决思路同样简单使用延时消息。

# 常见的几种延时消息
### 开源MQ
#### RabbitMQ
 RabbitMQ本身不支持延时消息或者定时消息，不过可以利用其特性来模拟延时消息实现。

###### 死信模式
 RabbitMQ可以针对Queue设置x-expires 或者 针对Message设置 x-message-ttl，来控制消息的生存时间，如果超时(两者同时设置以最先到期的时间为准)，则消息变为dead letter(死信)，RabbitMQ的Queue可以配置x-dead-letter-exchange 和x-dead-letter-routing-key（可选）两个参数，如果队列内出现了dead letter，则按照这两个参数重新路由转发到指定的队列。代码如下:

* producer
````
    <rabbit:queue name="orderFifteenMinutesDelayQueue" durable="true" auto-delete="false" exclusive="false">
        <rabbit:queue-arguments>
            <entry key="x-message-ttl">
                <value  type="java.lang.Long">900000</value>
            </entry>
            <entry key="x-dead-letter-exchange" value="orderFifteenMinutesExchange"/>
        </rabbit:queue-arguments>
    </rabbit:queue>
    <rabbit:fanout-exchange name="orderFifteenMinutesDelayExchange" durable="true" auto-delete="false" id="orderFifteenMinutesDelayExchange">
        <rabbit:bindings>
            <rabbit:binding queue="orderFifteenMinutesDelayQueue"/>
        </rabbit:bindings>
    </rabbit:fanout-exchange>
    <rabbit:queue name="orderFifteenMinutesQueue" durable="true" auto-delete="false" exclusive="false" />
    <rabbit:direct-exchange name="orderFifteenMinutesExchange" durable="true" auto-delete="false" id="orderFifteenMinutesExchange">
        <rabbit:bindings>
            <rabbit:binding queue="orderFifteenMinutesQueue" key="orderFifteenMinutes" />
        </rabbit:bindings>
    </rabbit:direct-exchange>
    <rabbit:template exchange="orderFifteenMinutesExchange" id="orderFifteenMinutesTemplate" connection-factory="connectionFactory" message-converter="jsonMessageConverter" />

````
* consumer
````
    <rabbit:queue name="orderFifteenMinutesQueue" durable="true" auto-delete="false" exclusive="false"/>
    <rabbit:direct-exchange name="orderFifteenMinutesExchange" durable="true" auto-delete="false" id="orderFifteenMinutesExchange">
        <rabbit:bindings>
            <rabbit:binding queue="orderFifteenMinutesQueue" key="orderFifteenMinutes"/>
        </rabbit:bindings>
    </rabbit:direct-exchange>
    <bean id="orderFifteenMinutesListener" class="com.chinaredstar.ordercenter.mq.OrderFifteenMinutesListener"/>
    <rabbit:listener-container
            connection-factory="connectionFactory"
            acknowledge="manual"
            channel-transacted="false"
            message-converter="jsonMessageConverter">
        <rabbit:listener queues="orderFifteenMinutesQueue" ref="orderFifteenMinutesListener" method="onMessage"/>
    </rabbit:listener-container>
````

* 优缺点
> 不需要任何依赖，配置队列就行。最大的弊端就是无法动态传入延迟时间，如果需要新增过期时间需要新增队列配置，使用起来太不友好。

###### 插件（rabbitmq-delayed-message-exchange）
* 使用方法
> [rabbitmq/rabbitmq-delayed-message-exchange](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)
* 优缺点
> 对rabbitMQ有版本要求，同时需要安装插件，使用简单、灵活。
#### RocketMQ
* 使用方法
> [Schedule example](http://rocketmq.apache.org/docs/schedule-example/)
* 优缺点
> 使用简单，性能强悍可靠。不过Apache RocketMQ对延迟的Level有限制只支持18个固定的Level（固定Level的含义是延迟是特定级别的，比如支持3秒、5秒的Level，那么用户只能发送3秒延迟或者5秒延迟，不能发送8秒延迟的消息）。
### Redis key过期事件
> 在redis 2.8版本以后对redis 中Key过期时间进行订阅和发布，可通过这种模式实践。
* 优缺点
 使用虽然简单，但是不可靠无法消息确认，分布式环境中处理麻烦。

# 本次改造
 本次业务改动的时候，本来想直接使用支持消息延时/定时消息的MQ，但是受限（公司生产环境只使用了Rabbit MQ，那么最好就是安装插件了，这还得求着架构组，并且还要一定的测试，整体麻烦，上线时间紧急还是使用其它的方式简单实现可靠）。最后想到的是使用JAVA本身的队列-DelayQueue。DelayQueue是一个无界的BlockingQueue，用于放置实现了Delayed接口的对象，其中的对象只能在其到期时才能从队列中取走。这种队列是有序的，即队头对象的延迟到期时间最长。整体架构如下。
![](https://github.com/moxingwang/resource/blob/master/image/%E6%B6%88%E6%81%AF%E5%BB%B6%E8%BF%9F%E6%94%B9%E9%80%A0%E5%90%8E.jpg?raw=true)
## 实现流程
* Service服务创建订单成功后，把订单号和订单关闭延迟时间包装成一个对象
````
public class DelayQueueTaskMessage<T extends Serializable> implements Serializable, Comparable<DelayQueueTaskMessage> {
    private Long id;//订单id
    private int type;
    private Date endDate;
    private T message;
}
````
* job服务作为MQ consumer开启ACK，接收到消息后先持久化到MySQL数据库。
````
CREATE TABLE `db_order_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，自增长，步长＝1',
  `task_type` int(4) DEFAULT NULL COMMENT '类型 1 定时关闭',
  `task_value` varchar(1024) DEFAULT NULL COMMENT '执行内容',
  `task_status` tinyint(2) DEFAULT '0' COMMENT '状态 0 未执行 1成功',
  `deadline_date` datetime DEFAULT NULL COMMENT '计划执行时间',
  `execute_date` datetime DEFAULT NULL COMMENT '执行时间',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单任务调度表';
````
* job服务维护一个DelayQueue队列，通过上一步操作，task任务落地之后，再把task任务放到DelayQueue队列中（这里面其它逻辑比如说防止内存爆掉，队列元素超过阀值不再添加到队列，延迟时间过长也不用添加到队列等等），启动一个线程执行操作队列取队列元素。
````
private static final DelayQueue<DelayQueueTask> delayQueue = new DelayQueue<>();

@PostConstruct
public void init() {
    Runnable task = () -> {
        try {
            DelayQueueTask delayQueueTask = delayQueue.take();
            orderTaskService.execute(delayQueueTask.getMsg());
        } catch (Exception e) {
            logger.error("消息处理异常", e);
        }
    };
    Thread consumer = new Thread(task);
    consumer.start();
}
````
* 如果延续消息到期执行成功后回写db_order_task的状态。
* 添加一个补偿job（调度频率可以降低些，降低数据库的压力），这个job专门处理db_order_task表到期还未执行的数据（执行异常或者断电关机等等都有可能导致队列数据丢失等等），由于job是多态服务集群，必须要有分布式作业调度系统完成如 [XXL-JOB](http://www.xuxueli.com/xxl-job/#/）(保证任务不会被多态机器同时调度)。


# 总结
 本文中使用MQ结合DelayQueue再使用补偿机制的实现是一个可靠安全的模型，不但减轻了job刷库的压力并且提高了任务执行的精确度，在整个过程中消息也不会丢失，简单易用，对于普通的生产应用需求是足够的。

# 参考
* [基于redis的延迟消息队列设计](https://www.cnblogs.com/peachyy/p/7398430.html)
* [如何在MQ中实现支持任意延迟的消息](https://www.cnblogs.com/luckcs/articles/8202380.html)
* [rabbitmq 实现延迟队列的两种方式](https://blog.csdn.net/u014308482/article/details/53036770)
* [Spring Boot RabbitMQ 延迟消息实现完整版示例](https://www.jb51.net/article/139457.htm)