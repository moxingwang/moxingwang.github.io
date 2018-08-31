## InnoDB锁

## InnoDB事物

## 死锁举例




```
-- 第一种
start transaction ;

# 4
UPDATE tx_order
SET order_status = '12',
  after_sale_status = '0',
  promotion_total_amount = '0.00',
  payable_amount = '2000.00',
  refunded_amount = '2000.00',
  paid_amount = '2000.00',
  last_update_date = now( )
WHERE
  id = 784339
;

# 1
update tx_order.tx_order set last_update_date=now() where id = 784330;


COMMIT;




start transaction ;
# 2
UPDATE tx_order
SET order_status = '12',
  after_sale_status = '0',
  promotion_total_amount = '0.00',
  payable_amount = '2000.00',
  refunded_amount = '2000.00',
  paid_amount = '2000.00',
  last_update_date = now( )
WHERE
  id = 784339
;
# 3
update tx_order.tx_order set last_update_date=now() where id = 784330;


COMMIT;
```

# reference
* [14.5 InnoDB Locking and Transaction Model](https://dev.mysql.com/doc/refman/5.7/en/innodb-locking-transaction-model.html)
* [MySQL 加锁处理分析](http://hedengcheng.com/?p=771#_Toc374698318)