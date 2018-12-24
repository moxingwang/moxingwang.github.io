## 文件copy
````aidl
sudo scp m@10.11.28.65:/Users/M/Downloads/jdk-8u151-linux-x64.tar.gz /usr/local/java
````

## 机器
* 10.11.27.38
* 10.11.27.30
* 10.11.27.20

* 10.11.27.41
* 10.11.27.43
* 10.11.27.45

## jdk安装
````aidl
export JAVA_HOME=/usr/local/java/jdk1.8.0_151  
export JRE_HOME=${JAVA_HOME}/jre  
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib  
export PATH=${JAVA_HOME}/bin:$PATH
````

## vi
* 行首：0
* 行未：$
* 新增一行: o

## 下载zk
````aidl
sudo wget http://mirrors.cnnic.cn/apache/zookeeper/zookeeper-3.4.8/zookeeper-3.4.8.tar.gz -P /usr/local/zk
````

## 环境变量设置
````aidl
ZOOKEEPER=/usr/local/zk/zookeeper-3.4.8
PATH=$PATH:$ZOOKEEPER/bin
````

## 报错zkEnv.sh: Syntax error: "(" unexpected (expecting "fi")
```aidl
sudo ln -sf bash /bin/sh
```

## nohup: failed to run command 'java': No such file or directory
````aidl
export JAVA_HOME=/usr/local/java/jdk1.8.0_151   
export PATH=${JAVA_HOME}/bin:$PATH

加入到zkServer.sh 头部
````

## dubbo admin配置
````aidl
dubbo.registry.address=zookeeper://192.168.199.191:2181?backup=192.168.199.192:2181,192.168.199.193:2181  
````



