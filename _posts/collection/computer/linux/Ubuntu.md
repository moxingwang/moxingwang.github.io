## 参考

## install java8
* [Ubuntu 安装 JDK 7 / JDK8 的两种方式](http://www.cnblogs.com/a2211009/p/4265225.html)
* [Ubuntu的add-apt-repository: command not found](http://blog.csdn.net/dogfish/article/details/67150703)
* [Hadoop安装教程_单机/伪分布式配置](http://www.powerxing.com/install-hadoop/)
````aidl
sudo apt-get install software-properties-common python-software-properties  
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo update-java-alternatives -s java-8-oraclesudo
vi ~/.bashrc 
export JAVA_HOME = /usr/lib/jvm/java-8-oracle
````
## install git
````aidl
sudo apt-get install git
````

## install maven
* [Ubuntu下Maven安装和使用](http://blog.csdn.net/ac_dao_di/article/details/54233520)
````aidl
wget http://apache.fayea.com/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar zxvf apache-maven-3.3.9-bin.tar.gz

sudo vi /etc/profileexport

export M2_HOME=/home/apache-maven-3.3.9
PATH=$M2_HOME/bin:$PATH

````

## 关闭图形界面
[Ubuntu 16.04 开机默认命令行界面](https://wiki.zthxxx.me/wiki/%E6%8A%80%E6%9C%AF%E5%BC%80%E5%8F%91/Linux/Ubuntu/Ubuntu-16-%E5%BC%80%E6%9C%BA%E9%BB%98%E8%AE%A4%E5%91%BD%E4%BB%A4%E8%A1%8C%E7%95%8C%E9%9D%A2/)

## ssh
[ubuntu开启SSH服务](http://www.cnblogs.com/xiazh/archive/2010/08/13/1798844.html)