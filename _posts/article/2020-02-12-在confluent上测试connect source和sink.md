- 测试目标

为了实现分库分表前期的安全操作, 希望分表的数据还是能够暂时合并到原表中, 使用基于kafka connect实现, debezium做connect source, kafka-jdbc-connector-sink做sink.

# 实现步骤
### 开启binlog的MySQL
- 创建测试数据库test
```
create database test;
```
- 初始化表
```
create table if not exists tx_refund_bill(
	id bigint unsigned auto_increment comment '主键' primary key,
	order_id bigint not null comment '订单id',
	bill_type tinyint not null comment '11'
)comment '退款费用明细' charset=utf8;

CREATE TABLE test_new1 LIKE tx_refund_bill;
```

- 数据测试sql
```
INSERT INTO tx_refund_bill (order_id, bill_type) VALUES (1,3);

update tx_refund_bill set order_id = 3 where id = 1;

select * from tx_refund_bill;

select * from test_new1;
```

# 在confluent快速搭建kafka connect
- [download confluent](https://www.confluent.io/download/)
- quick local start
    - 创建confluent配置目录
    ```
    mkdir ~/.confluent
    ```
    - 设置confluent环境
    ```
    export CONFLUENT_HOME=/home/xingwang/service/confluent-5.4.0
    export PATH=$CONFLUENT_HOME/bin:$PATH
    ```

- 安装debezium
    - [下载](https://www.confluent.io/hub/debezium/debezium-connector-mysql)
    - 解压后复制到/home/xingwang/service/confluent-5.4.0/share/java
- 安装kafka-connect-jdbc
    - confluent默认带了kafka-connect-jdbc,只需要额外下载mysql-connector-java-5.1.40.jar放到/home/xingwang/service/confluent-5.4.0/share/java/kafka-connect-jdbc就可以了

- start confluent
```
confluent local start
```

- log位置

log在/tmp/下

- confluent 管理页面

[http://172.17.228.163:9021/](http://172.17.228.163:9021/)


# 配置connect(配置可以直接在http client中执行(.http))
```

### 查看connectors
GET http://172.17.228.163:8083/connectors

### delete connnector
curl  -XDELETE 'http://172.17.228.163:8083/connectors/debezium'

### 创建source debezium connector
curl -H "Content-Type:application/json" -XPUT 'http://172.17.228.163:8083/connectors/debezium/config' -d '
{
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "localhost",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "Mo@123456",
    "database.server.id": "19991",
    "database.server.name": "test_0",
    "database.whitelist": "test",
    "include.schema.changes": "false",
    "snapshot.mode": "schema_only",
    "snapshot.locking.mode": "none",
    "database.history.kafka.bootstrap.servers": "localhost:9092",
    "database.history.kafka.topic": "dbhistory",
    "decimal.handling.mode": "string",
     "table.whitelist": "test.tx_refund_bill",
    "database.history.store.only.monitored.tables.ddl":"true",
    "database.history.skip.unparseable.ddl":"true"
}'

### 查看source debezium connector status
GET http://172.17.228.163:8083/connectors/debezium/status



### delete connnector
curl  -XDELETE 'http://172.17.228.163:8083/connectors/jdbc-sink'


### 创建sink jdbc connector
curl -H "Content-Type:application/json" -XPUT 'http://172.17.228.163:8083/connectors/jdbc-sink/config' -d '
{
    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
    "connection.url": "jdbc:mysql://localhost:3306/test?nullCatalogMeansCurrent=true",
    "connection.user": "root",
    "connection.password": "Mo@123456",
    "tasks.max": "1",
    "topics": "test_0.test.tx_refund_bill",
    "table.name.format": "test_new1", 

    "insert.mode": "upsert",
    "pk.fields": "id",
    "pk.mode": "record_value",

    "transforms": "ExtractField",
    "transforms.ExtractField.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
    "transforms.ExtractField.field": "after"
  }'


### 查看connectors status
GET http://172.17.228.163:8083/connectors/jdbc-sink/status

```

# 实验
- 在tx_refund_bill表中insert数据,观察test_new1的变化
- 在tx_refund_bill表中执行update语句,观察test_new1的变化

# reference
- [confluent doc](https://www.confluent.io/confirmation/)
- [Kafka连接器深度解读之JDBC源连接器](https://www.liangzl.com/get-article-detail-114352.html)
- [kafka-jdbc-connector-sink实现kafka中的数据同步到mysql](https://blog.csdn.net/txgANG/article/details/103228719)
- [Mysql Sink : unknown table X in information_schema Exception](https://github.com/confluentinc/kafka-connect-jdbc/issues/573)
- [Kafka Connect JDBC Sink - pk.fields for each topic (table) in one sink configuration](https://stackoverflow.com/questions/54438684/kafka-connect-jdbc-sink-pk-fields-for-each-topic-table-in-one-sink-configura)