* 删除列
````apple js
ALTER TABLE 【表名字】 DROP 【列名称】
````
* 增加列
````apple js
ALTER TABLE 【表名字】 ADD 【列名称】 INT NOT NULL COMMENT ‘注释说明’
````
* 修改列的类型信息
````apple js
ALTER TABLE 【表名字】 CHANGE 【列名称】【新列名称（这里可以用和原来列同名即可）】 BIGINT NOT NULL COMMENT ‘注释说明’
````
* 重命名列
````apple js
ALTER TABLE 【表名字】 CHANGE 【列名称】【新列名称】 BIGINT NOT NULL COMMENT ‘注释说明’
````
* 重命名表
````apple js
ALTER TABLE 【表名字】 RENAME 【表新名字】
````
* 删除表中主键
````apple js
Alter TABLE 【表名字】 drop primary key
````
* 添加主键
````apple js
ALTER TABLE sj_resource_charges ADD CONSTRAINT PK_SJ_RESOURCE_CHARGES PRIMARY KEY (resid,resfromid)
````
* 添加索引
````apple js
ALTER TABLE sj_resource_charges add index INDEX_NAME (name)
````
* 添加唯一限制条件索引
````apple js
ALTER TABLE sj_resource_charges add unique emp_name2(cardnumber)
````
* 删除索引 
````apple js
alter table tablename drop index emp_name;
````