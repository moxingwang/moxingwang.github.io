* [基于Docker的Mysql主从复制](https://segmentfault.com/a/1190000013159922)

```aidl
docker run --name mysql -p 3306:3306 -v C:\Users\xingwang.mo\Documents\docker\mysql57\mysqlData\master\cnf\conf.d:/etc/mysql/conf.d  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7
```