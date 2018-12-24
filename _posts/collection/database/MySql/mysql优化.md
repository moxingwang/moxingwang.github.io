# 优化概述
## 在数据库级别进行优化
* 建立合理的表结构，特别是，列是否具有正确的数据类型，并且每个表是否具有适合工作类型的列。例如大部分应用程序频繁更新的列只是少部分的。
* 是否有合理的索引。
* 您是否为每个表使用适当的存储引擎，并利用使用的每个存储引擎的优势和功能？事务性存储引擎如InnoDB，非事务性存储引擎MyISAM对于性能和可伸缩性来说非常重要。MySQL默认InnoDB。
* 选择合适的锁策略。
* 是否正确使用了用于缓存的所有内存区域。如InnoDB缓冲池的配置。

## 在硬件级别进行优化
* 磁盘
* CPU周期
* 内存宽带

# 优化SQL语句
## 优化SELECT语句
* 对于SELECT ... WHERE，首先要检查的是是否可以添加索引，在WHERE子句中使用的列上设置索引。
* 隔离并调整查询，例如k函数调用，这会占用过多时间。根据查询的结构，可以为结果集中的每一行调用一次函数，甚至可以为表中的每一行调用一次函数，从而大大减轻任何低效率。
* 最大限度地减少 查询中的全表扫描次数 ，尤其是对于大型表。
* 了解不同引擎的调优特性。
*

## 索引下推优化
* [MySQL 执行计划(Using where,Using index 和 Using index condition)](https://segmentfault.com/q/1010000004197413)
### file sort
* [MySQL如何执行ORDER BY](http://s.petrunia.net/blog/?p=24)
* [MySQL中Order By实现原理分析](http://database.51cto.com/art/200903/116780.htm)




# 总结
* order by desc 的效果没有asc的效果好
````
alter table tx_order ADD INDEX idx_shop_create_date_2(shop_id,create_date desc );
desc select * from tx_order.tx_order where  shop_id = '111'    order by create_date asc ;
desc select * from tx_order.tx_order where  shop_id = '111'    order by create_date desc ;
````
* mysql会根据数据量的大小来确定是否需要全部扫描。


* count(*) count(字段)的区别
>

* 指定索引字段的排序
> eg: alter table tx_order_item add index idx_product_creat_date(product_id(10) desc ,create_date desc ) using btree ;
> 这种方式MySQL8才有效
> https://coyee.com/article/11341-mysql-8-0-descending-indexes-can-speed-up-your-queries













* [MySQL官方手册](https://dev.mysql.com/doc/refman/5.7/en/select-optimization.html)