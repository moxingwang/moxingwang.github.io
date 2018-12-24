# 安装java
### 下载
[Java SE Development Kit 8 Downloads](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
### 安装
* 将文件.tar.gz移动到/usr/java
* 解压：tar -zxvf 文件.tar.gz
* 打开/etc/profile（vim /etc/profile）在最后面添加如下内容：
````apple js
export JAVA_HOME=/usr/jdk安装目录
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH
````
* win环境变量配置
````aidl
%JAVA_HOME%\bin;%Java_Home%\jre\bin;
````
* source /etc/profile





# 安装maven
### 下载
[Downloading Apache Maven 3.5.3](http://maven.apache.org/download.cgi)
### 安装
* 将下载文件放到/usr/maven
* 解压：tar -zxvf 文件
* 配置环境在/etc/profile加入
````apple js
export MAVEN_HOME=/usr/maven/maven文件
export MAVEN_HOME
export PATH=$PATH:$MAVEN_HOME/bin
````
* source /etc/profile



# 安装RabbitMQ
* [CentOS 安装 Erlang](https://blog.zfanw.com/install-erlang-on-centos/) 
* 下载rabbitMQ [Installing on RPM-based Linux (RHEL, CentOS, Fedora, openSUSE)](https://www.rabbitmq.com/install-rpm.html)
* 安装
````$xslt
yum -y install rabbitmq-server-3.6.6-1.el6.noarch.rpm
````
* 启动web管理界面
````$xslt
rabbitmq-plugins enable rabbitmq_management
````

* 开机自动启动
````$xslt
systemctl enable rabbitmq-server
````

* 启动关闭
````$xslt
rabbitmq-server start
rabbitmq-server stop
````

* 增加用户设置角色
````$xslt
rabbitmqctl add_user dev dev_user
rabbitmqctl set_user_tags dev administrator
rabbitmqctl set_permissions -p "/" dev "." "." ".*"
````

* 附 [centos 7 安装rabbitmq 3.6.12](https://blog.csdn.net/lsb2002/article/details/78128489)



# 安装Mysql
> 提前关闭防火墙
```
systemctl stop firewalld.service #停止firewall
systemctl disable firewalld.service #禁止firewall开机启动
```
> [https://dev.mysql.com/doc/refman/5.7/en/linux-installation-yum-repo.html](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-yum-repo.html)
* 查看Linux发行版本
````$xslt
cat /etc/redhat-release
````
* 下载MySQL官方的Yum Repository [Download MySQL Yum Repository](https://dev.mysql.com/downloads/repo/yum/)
* 安装MySQL的Yum Repository
````$xslt
wget https://dev.mysql.com/get/mysql80-community-release-el7-1.noarch.rpm
yum localinstall mysql80-community-release-el7-1.noarch.rpm
````
* 选择版本
```
yum repolist all | grep mysql

yum-config-manager --disable mysql80-community
yum-config-manager --enable mysql57-community

```
* 安装MySQL数据库的服务器版本
````$xslt
yum -y install mysql-community-server
````
* 启动数据库
````$xslt
service mysqld start

service mysqld status

````
* 重启和关闭
```
service mysqld restart
service mysqld stop
```
* 获取初始密码
````$xslt
grep "password" /var/log/mysqld.log
````
* 修改root用户密码
````$xslt
mysql -uroot -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Password@123';
use mysql;
update user set host = '%'  where user ='root';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'Password@123' WITH GRANT OPTION;
flush privileges;
````

* 设置密码的安全级别 附[6.5.3.2 Password Validation Options and Variables](https://dev.mysql.com/doc/refman/8.0/en/validate-password-options-variables.html)
````apple js
SHOW VARIABLES LIKE 'validate_password.%';
set global validate_password.policy=0;
````

* mysql8 ：客户端连接caching-sha2-password问题
````apple js
ALTER USER 'root'@'%' IDENTIFIED BY 'password' PASSWORD EXPIRE NEVER; #修改加密规则 
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'password'; #更新一下用户的密码 
FLUSH PRIVILEGES; #刷新权限 
ALTER USER 'root'@'%' IDENTIFIED BY 'password';
````

## MySQL binlog配置
* 修改 my.cnf
```
log-bin=/var/lib/mysql/mysql-bin
server-id=1
```

* 查看
```
show variables like '%log_bin%'
```

### mysql time zone
```
SET GLOBAL time_zone = '+8:00';
```

* 附 [CentOS 7.2使用yum安装MYSQL 5.7.10](https://typecodes.com/linux/yuminstallmysql5710.html)