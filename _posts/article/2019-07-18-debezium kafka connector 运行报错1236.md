今天发现stag环境kafka connector运行报错（ curl 172.18.1.1:8083/connectors/order-center-connector/status）

```
{
    "name": "order-center-connector",
    "connector": {
        "state": "RUNNING",
        "worker_id": "172.18.1.1:8083"
    },
    "tasks": [
        {
            "state": "FAILED",
            "trace": "org.apache.kafka.connect.errors.ConnectException: The slave is connecting using CHANGE MASTER TO MASTER_AUTO_POSITION = 1, but the master has purged binary logs containing GTIDs that the slave requires. Error code: 1236; SQLSTATE: HY000.\n\tat io.debezium.connector.mysql.AbstractReader.wrap(AbstractReader.java:200)\n\tat io.debezium.connector.mysql.AbstractReader.failed(AbstractReader.java:167)\n\tat io.debezium.connector.mysql.BinlogReader$ReaderThreadLifecycleListener.onCommunicationFailure(BinlogReader.java:955)\n\tat com.github.shyiko.mysql.binlog.BinaryLogClient.listenForEventPackets(BinaryLogClient.java:921)\n\tat com.github.shyiko.mysql.binlog.BinaryLogClient.connect(BinaryLogClient.java:559)\n\tat com.github.shyiko.mysql.binlog.BinaryLogClient$7.run(BinaryLogClient.java:793)\n\tat java.lang.Thread.run(Thread.java:745)\nCaused by: com.github.shyiko.mysql.binlog.network.ServerException: The slave is connecting using CHANGE MASTER TO MASTER_AUTO_POSITION = 1, but the master has purged binary logs containing GTIDs that the slave requires.\n\tat com.github.shyiko.mysql.binlog.BinaryLogClient.listenForEventPackets(BinaryLogClient.java:882)\n\t... 3 more\n",
            "id": 0,
            "worker_id": "172.18.1.1:8083"
        }
    ],
    "type": "source"
}
```

搜索查了下，按照[规避Debezium master purged GTID问题](https://www.twblogs.net/a/5bc1481e2b717711c9246ab8/zh-cn)的说法，分析了分析博主是正确的，但是按照他的实现方式（如下）还是不能够解决问题。

```
curl -H "Content-Type:application/json" -XPUT 'http://172.18.1.1:8083/connectors/order-center-connector/config' -d '
 {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "sha2app220.stag.rs.com",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "password1234567890",
    "database.server.id": "19991",
    "database.server.name": "trade_order_0",
    "database.whitelist": "db_order",
    "include.schema.changes": "false",
    "snapshot.mode": "schema_only",
    "snapshot.locking.mode": "none",
    "gtid.source.includes":"631357f9-95a0-11e6-a28c-005056b50019.*,631c2fd3-95a0-11e6-a28c-005056b51ac6.*,d9af3b6a-cfe3-11e7-a3ce-005056ab1bc3:1-36631495,fefa6b5b-d00f-11e7-a4ee-005056ab5e4d.*",
    "database.history.kafka.bootstrap.servers": "base6511.stag.rs.com:9092,base6512.stag.rs.com:9092,base6513.stag.rs.com:9092",
    "database.history.kafka.topic": "dbhistory.trade_order_0",
    "decimal.handling.mode": "string",
    "table.whitelist": "db_order.tx_order,db_order.tx_order_addition,db_order.tx_customer_service",
    "database.history.store.only.monitored.tables.ddl":"true",
    "database.history.skip.unparseable.ddl":"true"
}'
```

最后发现解决这个问题比较简单，只需要把之前的`order-center-connector`删除掉，重新建立不同名称的connector就行了，如下


```
curl -H "Content-Type:application/json" -XPUT 'http://172.18.1.1:8083/connectors/order-center-connector1/config' -d '
 {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "sha2app220.stag.rs.com",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "password1234567890",
    "database.server.id": "19991",
    "database.server.name": "trade_order_0",
    "database.whitelist": "db_order",
    "include.schema.changes": "false",
    "snapshot.mode": "schema_only",
    "snapshot.locking.mode": "none",
    "database.history.kafka.topic": "dbhistory.trade_order_0",
    "decimal.handling.mode": "string",
    "table.whitelist": "db_order.tx_order,db_order.tx_order_addition,db_order.tx_customer_service",
    "database.history.store.only.monitored.tables.ddl":"true",
    "database.history.skip.unparseable.ddl":"true"
}'
```






