# 运行docker镜像【官方centos】
* 启动容器

````
docker run -it -d -p 9000-9900:9000-9900 --name cenosElasticsearch centos
docker run -it -d -p 9200:9200 -p 9300:9300 --name cenosElasticsearch3 e11524101e04
````

* 查看容器并进入
````
docker ps
docker attach e584c6fb2eff
````
![](https://github.com/moxingwang/elastic/blob/master/source/WX20180707-154611.png?raw=true)
> 这里启动容器选择了一段ip和主机ip映射「-p 9000-9900:9000-9900」可以使用docker port 命令查看具体映射
````
docker port e584c6fb2eff
````

# 安装环境和必要软件
* 安装JAVA环境
````
yum install java -y
````
* 安装wget
````
yum install wget -y
````
* 安装vim
````
yum install vim -y
````
* 安装net-tools
````
yum install net-tools -y
````

# 去官网下载最新elasticsearch [官网](https://www.elastic.co/downloads/elasticsearch)
> 这里我们下载文件到/usr/local/
````
cd /usr/local/
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.3.1.tar.gz
````

* 解压
````
tar -zxvf elasticsearch-6.3.1.tar.gz
````

# 修改elasticsearch.yml配置文件

````
vi config/elasticsearch.yml
````
> 找到network.host这一项，并且改为network.host: 0.0.0.0，这里修改其实就是为了宿主机能够直接访问。

# ES不能使用root用户启动，所以创建普通用户es，并给予操作ES安装目录的权限
* 修改root密码
````
passwd
````
* 添加用户设置组和密码
````
groupadd es
useradd es -g es -p es
passwd es
````
* 设置权限
````
chown -R es:es elasticsearch-6.3.1
````

# 启动elasticsearch
* 切换用户
````
su es
````
* 后端启动
````
bin/elasticsearch -d
````
* 查看日志文件
````
tail logs/elasticsearch.log
````

# 访问
* 容器访问
````
curl http://127.0.0.1:9200/
````
![](https://github.com/moxingwang/elastic/blob/master/source/virtualhost.png?raw=true)

* 主机访问
````
http://127.0.0.1:9200/
````
![](https://github.com/moxingwang/elastic/blob/master/source/realhost.png?raw=true)

# 结尾
* 当前镜像已经commit上传到阿里云镜像仓库
````
docker pull registry.cn-hangzhou.aliyuncs.com/m65536/centos-elastic
````


