


# console
````aidl


# create database test default charset utf8;

CREATE TABLE tbj (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category` JSON,
  `tags` JSON,
  PRIMARY KEY (`id`)
);



select * from tbj;

INSERT INTO `tbj` (category, tags) VALUES ('{"id": 1, "name": "lnmp.cn"}', '[1, 2, 3]');

SELECT * FROM tbj WHERE category = CAST('{"id": 1, "name": "lnmp.cn"}' as JSON);
SELECT * FROM tbj WHERE category = CAST('{"id": 1, "name": "lnmp.cn"}' as JSON);
SELECT * FROM tbj WHERE category->'$.name' = 'lnmp.cn';
SELECT * FROM tbj WHERE category->'$.id' = 2;

SELECT * FROM tbj WHERE category = '{"id": 1, "name": "lnmp.cn"}';



CREATE TABLE tbj2 (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tags` varchar(1111) null ,
  PRIMARY KEY (`id`)
);
alter table tbj2 change  tags tags JSON;

insert into tbj2 (tags) value ('{"id": 1, "name": "lnmp.cn"}');
insert into tbj2 (tags) value ('{"id": 1, "name": "lnmp.cn","markdIds":["1001","1002","1003"]}');
# drop table tbj2;
select * from tbj2;

SELECT * FROM tbj2 WHERE tags->'$.name' = 'lnmp.cn';
SELECT * FROM tbj2 WHERE tags->'$.name' = 'lnmp.cn';
SELECT tags->"$.marketIds" FROM tbj2 WHERE tags->'$.name' = 'lnmp.cn';
SELECT tags->"$" FROM tbj2 WHERE tags->'$.name' = 'lnmp.cn';
SELECT * FROM tbj2 WHERE JSON_EXTRACT(tags,'$.name' ) = 'lnmp.cn';
SELECT * FROM tbj2 WHERE JSON_EXTRACT(tags,'$.name.markdIds' ) = JSON_ARRAY("1001","1002","1003");
SELECT * FROM tbj2 WHERE JSON_SEARCH(tags,'$.name.markdIds' ) = JSON_ARRAY("1001","1002","1003");

SELECT * FROM `tbj2` WHERE JSON_SEARCH(tags, 'all', 'lnmp%') IS NOT NULL;

SELECT * FROM `tbj2` WHERE JSON_SEARCH(tags, 'all', 'lnmp%') IS NOT NULL;


select json_array_contains_json('["foo", 1, true, 3]', 'true') as has_truth;

SELECT * FROM tbj2 WHERE tags->'$.markdIds' = '["1001","1002","1003"]';
SELECT * FROM tbj2 WHERE tags->'$.markdIds' = JSON_ARRAY("1001","1002","1003");
SELECT * FROM tbj2 WHERE tags->'$.markdIds' = JSON_ARRAY("1001","1002","1003");

SELECT * FROM tbj2 WHERE tags->'$.markdIds' = '"1001","1002","1003"';
SELECT * FROM tbj2 WHERE tags->'$.markdIds' = '1001';

select * from tbj2;

SELECT grade->'$[*]' FROM levels WHERE grade->'$[*].id' like '%77642501-5f1d-4757-b277-9df7571637cb%';



````