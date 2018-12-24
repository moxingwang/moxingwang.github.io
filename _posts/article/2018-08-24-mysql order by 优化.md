> version : 5.7, from [8.2.1.14 ORDER BY Optimization](https://dev.mysql.com/doc/refman/5.7/en/order-by-optimization.html)

本节描述MySQL何时可以使用索引来满足ORDER BY子句，当不能使用索引时使用filesort，以及优化器中有关ORDER BY的执行计划信息。

一个order by语句对于有没有使用limit可能存在执行差异。详细内容查看[8.2.1.17 LIMIT Query Optimization](https://dev.mysql.com/doc/refman/5.7/en/limit-optimization.html)。


# 使用索引实现order by

在某些情况下，MySQL可能会使用索引来满足一个ORDER BY子句，并避免执行filesort 操作时涉及的额外排序。

虽然ORDER BY并不完全精确地匹配索引，但是索引还是会被使用，只要在WHERE子句中，所有未被使用的那部分索引（一个索引多个字段-联合索引的情况）以及所有ORDER BY字段都是一个常量就没问题，都会走到索引而不是filesort。

这里我们有一张表tx_order,
````
CREATE TABLE `tx_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT ,
  `serial_number` varchar(255) NOT NULL ,
  `order_status` int unsigned DEFAULT 0 NOT NULL ,
  `market_id` varchar(10) DEFAULT NULL ,
  `market_name` varchar(255) DEFAULT NULL ,
  `shop_id` varchar(50) DEFAULT NULL ,
  `shop_name` varchar(100) DEFAULT NULL ,
  `mobile` varchar(64) DEFAULT NULL ,
  `create_date` datetime DEFAULT NULL ,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2333702 DEFAULT CHARSET=utf8;
````
并且添加索引
````
alter table tx_order add index idx_market_date(market_id,create_date);
````
在接下来的sql中分析order by对索引的使用情况。其中MySQL优化器实际执行sql是否使用索引还是表扫描取决于两者的效率。

* 下面这个sql中，优化器使用了idx_market_date索引避开了表扫描.
````
desc select market_id,create_date from tx_order.tx_order order by market_id,create_date;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index

````

然而这句sql中的查询字段都在索引中，如果查询字段不被包含在索引中，如「select market_id,create_date,market_name」。这种情况下，扫描整个索引并且查找表行以查不在索引中的列，这样的操作的代价可能比表扫描更高，此时优化器可能不会使用索引。
````
desc select market_id,create_date,market_name from tx_order.tx_order order by market_id,create_date;

1	SIMPLE	tx_order		ALL					1671956	100	Using filesort

````

在InnoDB中，我们知道主键（聚集索引）本身是索引的一部分，下面这个查询中索引就会被使用。
````
desc select id,market_id,create_date from tx_order.tx_order order by market_id,create_date;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index

````

* 下面这种情况，在where条件中索引中的一个字段是一个常量，并且where子语句产生的范围索引的性能比表扫描高的多，那么这样的查询会选择索引而不是表扫描。
````
desc select market_id,create_date from tx_order.tx_order where  market_id = '1009' order by create_date;

1	SIMPLE	tx_order		ref	idx_market_date	idx_market_date	33	const	170398	100	Using where; Using index

````

* 下面两条sql比较特殊，也可以对比前面几个order by ... asc的语句。看看下面的执行结果我们可以思考这是为什么。首先添加索引的时候暂时是无法指定字段排序的，alter table tx_order add index idx_market_date(market_id asc,create_date desc)，虽然这样的写法语法是支持的，但是当前版本的MySQL不做任逻辑何支持，都是统一安装默认升序排列。在一个联合索引中，查询按照索引中的字段排序，如果排序方式不一致，优化器还是会部分走表扫描的。
````
desc select market_id,create_date from tx_order.tx_order order by market_id desc ,create_date desc ;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index

desc select market_id,create_date from tx_order.tx_order order by market_id asc ,create_date desc ;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index; Using filesort

````

* 下面的查询where子语句中的范围索引优于表扫描，优化器会选择索引解析order by。
````
desc select market_id,create_date from tx_order.tx_order where market_id > '1009' order by market_id asc;

1	SIMPLE	tx_order		range	idx_market_date	idx_market_date	33		835978	100	Using where; Using index

desc select market_id,create_date from tx_order.tx_order where market_id < '1009' order by market_id desc;

1	SIMPLE	tx_order		range	idx_market_date	idx_market_date	33		230966	100	Using where; Using index
````

* 下面这个查询中，order by的不再是market_id，但是所有查询的行market_id都是一个常量，所以还是会走到索引的解析order by。
````
desc select market_id,create_date from tx_order.tx_order where market_id = '1009' and create_date>'2018-01-01' order by create_date desc;

1	SIMPLE	tx_order		range	idx_market_date	idx_market_date	39		94002	100	Using where; Using index
````

在一些情况下，虽然MySQL对where条件处理的时候用会用到索引，但是不能够用索引来解析order by, 看下面的例子。

* order by使用到的索引非连续，MySQL解析order by还是会扫描表，我这里有一个索引 idx_market_id(market_id,order_status,create_date)，看下面的sql执行结果。
````
desc select market_id,create_date from tx_order.tx_order where  market_id='1009' order by market_id ,create_date ;

1	SIMPLE	tx_order		ref	idx_market_id,idx_market_type_create_date	idx_market_id	33	const	138084	100	Using where; Using index; Using filesort
````

* 混合排序asc,desc
````
desc select market_id,create_date from tx_order.tx_order order by market_id asc ,create_date desc;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index; Using filesort
````

* order by字段使用函数，优化器解析order by放弃索引
````
desc select mobile from tx_order.tx_order order by  abs(mobile);

1	SIMPLE	tx_order		index		idx_mobile	768		1671956	100	Using index; Using filesort

````

* 在多表关联查询中，并且ORDER BY中的列并不是全部来自第1个用于搜索行的非常量表。(这是EXPLAIN输出中的没有const联接类型的第1个表）。
````
desc select a.market_id from tx_order.tx_order a ,tx_order_item b where a.id = b.order_id and a.market_id = '1009'  order by a.market_id,b.sku;

1	SIMPLE	b		ALL	idx_order_create				1	100	Using filesort
1	SIMPLE	a		eq_ref	PRIMARY,idx_market_date	PRIMARY	8	tx_order.b.order_id	1	10.19	Using where

````

* 有不同的ORDER BY和GROUP BY表达式。
````
desc select market_id,create_date from tx_order.tx_order   group by market_id,create_date order by create_date;

1	SIMPLE	tx_order		index	idx_market_date	idx_market_date	39		1671956	100	Using index; Using temporary; Using filesort

````

* 对于指定了排序索引长度的索引。在这种情况下，索引不能完全解析排序顺序，需要使用filesort来排序。例如，建立索引alter table tx_order add index idx_mobile(mobile(5)); 然而mobile varchar(64).
````
desc select mobile from tx_order.tx_order order by mobile desc ;

1	SIMPLE	tx_order		ALL					1671956	100	Using filesort

````

* 有些情况，使用的表索引的类型不能按顺序保存行。例如，对于HEAP表的HASH索引情况即如此。

* 排序索引的可用性可能受列别名的使用影响。

在下面的语句中，排序受到影响，不会使用索引.
````
desc select abs(market_id) as aa from tx_order.tx_order order by aa;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index; Using filesort

````

但是，下面的语句中，虽然查询字段有使用别名，但是真实的排序字段还是索引中的字段，那么排序还是使用索引的。
````
desc select abs(market_id) as aa from tx_order.tx_order order by market_id;

1	SIMPLE	tx_order		index		idx_market_date	39		1671956	100	Using index

````

在默认情况下，对于"group by col2,col2,..."这样的语句，MySQL会同时会包含"order by col2,col2,..."等同于你显示的加速"order by col2,col2,..."排序，这种情况下优化器的处理是没有性能损失的。

对于这个默认情况，如果你想避开默认排序，可以使用 order by null 来避免，例如：
````
desc select market_id,count(market_id) from tx_order.tx_order group by market_id order by null ;
````

优化器可能仍然选择使用排序来实现分组操作。ORDER BY NULL 禁止对结果进行排序，而不是通过分组操作进行先前排序以确定结果。

> 注意 

> GROUP BY默认情况下隐式排序（即，在没有列ASC或 列的DESC指示符的情况下GROUP BY）。但是，不推荐依赖于隐式 GROUP BY排序（即，在没有ASC或 DESC指示符的情况下排序）或显式排序GROUP BY（即，通过 对列使用显式ASC或DESC指示符GROUP BY）。要生成给定的排序顺序，有必要供一个 ORDER BY子句。


# 使用filesort实现排序
当无法使用索引排序的时候，MySQL使用filesort扫描表给结果集排序，相应的filesort在整个查询过程中产生了额外的排序阶段。

为了支持filesort，优化器实现会分配一定数量的内存sort_buffer_size区域，这块内存区域是每个session独占的，并且可以更改这个变量值。

如果filesort数据集太大，在内存中无法实现排序，优化器会使用一块磁盘作为临时文件来做排序。某些查询特别适合内存排序完成filesort的操作，例如优化器可以有效的利用内存排序，而不需要临时文件实现。例如

````
desc select * from tx_order.tx_order order by market_name desc limit 10;

1	SIMPLE	tx_order		ALL					1671956	100	Using filesort

````

Using temporary的例子
````
desc select market_name from tx_order.tx_order order by RAND() desc limit 10;

1	SIMPLE	tx_order		ALL					1671956	100	Using temporary; Using filesort

````

# 影响order by优化
对于filesort的慢查询，可以尝试修改 max_length_for_sort_data 标量来达到效果，控制filesort选择算法的触发点，可以尝试调低 max_length_for_sort_data 值。（如果增大了max_length_for_sort_data的值，并且磁盘使用率上升，cpu使用率下降，）详细资料请阅读 [Mysql 排序优化与索引使用（转)](https://www.cnblogs.com/moss_tan_jun/p/6021822.html)。

要提高ORDER BY速度，请检查是否可以让MySQL使用索引而不是额外的排序阶段。如果无法做到这一点，请尝试以下策略：

* 增加 sort_buffer_size 变量值。理想情况下，该值应足够大，以使整个结果集适合排序缓冲区（以避免写入磁盘和合并传递），但至少该值必须足够大以容纳15个元组。（最多可以合并15个临时磁盘文件，每个文件至少有一个元组在内存中必须有空间。）

	请考虑存储在排序缓冲区中的列值的大小受 max_sort_length系统变量值的影响。例如，如果元组存储长字符串列的值并且您增加了值 max_sort_length，则排序缓冲区元组的大小也会增加，并且可能需要您增加 sort_buffer_size。对于作为字符串表达式（例如调用字符串值函数的那些）计算的列值，filesort算法无法分辨表达式值的最大长度，因此必须分配 max_sort_length 每个元组的字节数。

	要监视合并传递的数量（合并临时文件），请检查 Sort_merge_passes 状态变量。

* 增加 read_rnd_buffer_size 变量值，以便一次读取更多行。

* 将tmpdir 系统变量更改为指向具有大量可用空间的专用文件系统。变量值可以列出以循环方式使用的几个路径; 您可以使用此功能将负载分散到多个目录中。:在Unix上用冒号字符（）分隔路径，;在Windows上用分号字符（）分隔路径。路径应命名位于不同物理磁盘上的文件系统中的目录 ，而不是同一磁盘上的不同分区。

# 通过执行计划查看sql解析
使用 EXPLAIN （参见[8.8.1 Optimizing Queries with EXPLAIN](https://dev.mysql.com/doc/refman/5.7/en/using-explain.html)），可以检查MySQL是否可以使用索引来解析ORDER BY子句.

* 如果输出Extra列 EXPLAIN不包含Using filesort，则使用索引并且filesort不执行。
* 如果输出Extra列 EXPLAIN包含 Using filesort，则不使用索引并filesort执行。

另外，filesort执行的时候优化器的trace可以输出filesort_summary信息快。例如：
````
"filesort_summary": {
  "rows": 100,
  "examined_rows": 100,
  "number_of_tmp_files": 0,
  "sort_buffer_size": 25192,
  "sort_mode": "<sort_key, packed_additional_fields>"
}
````

对于MySQL的trace，详细请参考[Chapter 8 Tracing the Optimizer](https://dev.mysql.com/doc/internals/en/optimizer-tracing.html).

# 总结
想要写出高效可靠的排序查询，你需要搞明白order by大概的执行过程，这里可以参考[How MySQL executes ORDER BY](http://s.petrunia.net/blog/?p=24),[Mysql 排序优化与索引使用（转)](https://www.cnblogs.com/moss_tan_jun/p/6021822.html)这两篇文章。

我们在写sql语句并且使用order by的时候，首先考虑满足索引条件，如果不满足那么满足内存中filesort，最坏的情况就是临时文件出现了，当然这种情况是我们最不想看到的。

同时这里要说一下我的个人经验：

1. 联合索引是个好东西，能够应用到项目中的很多使用场景，详细优化可以参照[8.3 Optimization and Indexes](https://dev.mysql.com/doc/refman/5.7/en/optimization-indexes.html)。
2. sql改写，复杂的单条sql可以改写成两条或者三条，使用上索引。
3. 建立好的表结构，为字段分配最合身的类型和长度。

开放过程中多去琢磨sql，多看执行计划，有效的避免慢查询，提高服务的性能。

# 参考
* [How MySQL executes ORDER BY](http://s.petrunia.net/blog/?p=24)
* [Mysql 排序优化与索引使用（转)](https://www.cnblogs.com/moss_tan_jun/p/6021822.html)