## 按照教程
````aidl
http://www.jianshu.com/p/1448d1550c8b
http://www.powerxing.com/install-hadoop/
http://blog.csdn.net/yhao2014/article/details/44938237
https://www.cnblogs.com/ivan0626/p/4144277.html
````

## 修改hostname
````aidl
hostnamectl set-hostname server1

添加host2

10.11.27.41 server1
10.11.27.43 server2
10.11.27.45 server3

/etc/sysconfig

NETWORKING=yes
HOSTNAME=server1
NTPSERVERARGS=iburst

````

## ssh免密登录
````aidl
ssh-keygen -t rsa
cat id_rsa.pub >> authorized_keys


ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC2Uu1qodJtZ24Kn6L1SPJhyRZOOUghNRvT4GHKi+72Tyr6+mtWwzJN/m91B53GFzQ42Q+BlaHCzhDDp9OiDCTTJ6OXAFmlI76xSaiiSj+2ZtofQxlIjPh6eqVzF3AiJ22RhOFif/MDuP9AiY97/4LgA4Gck1IVElHLnZ1i2UqWl3yBcNiGXfKPLZErfGgT+WrgPA861UA3UJ4gCieYclPiHcAn0F85By924oTylHfcGSel8SPTSYx8DIIjiY8ukBz1odHZCopNLNxzP4JNTQ5XxxmvoqAVIvFzHhmylwQSRjTtrs74qWpD/4GlAyX8zUvRajEaBxi/h8ei2rZfkFdP root@server2

ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC4ic0+O/cBy0wlkQZ0OnLwApWHkHXB50GeYbKkjLpUSDzev4eO+5o6A5BRA0ev1TlELuHSZUGIcfhEXEZr/YXk5YCx59mSwQRh6pAwVq+O01Qo80ozA5DsaqJwqICuaLO6Lewzwtw7JMtalZXK1sF0/hDdGPHnBYnfFT6WjvQyCzReKeDk1DRD7Wlzfm6gChoSNiOGP5YIgy/PsElE6zhMZEZzW96kAZM9sgzL79S4Qz4nE/DeWiQgh0wL0nYX0cubfrOssl33QlJButo3IZqMWgWHmOmw+DMHGau4PMm6Q30Jpe/PbnxAyFIXsIdeEmDPpQm6CcSW26prvai4skFZ root@server1

ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQColSgDD+R48lABMsyk9vjgYH4zrKw97Q5Y+BiHxfRGvqBo6LVvBfsCvPsuL+LjVg/RW8qSSESLpVCP1GUG3WPW805N5DNJmp/oMnI/fyteBcaJFuaN2i7kGIq2gK2x4lcqkiBr0vDVx5eDclpN87N/866bbXaE4RmQ25lWa8RYvsGJ/kIRlE9aXSINg3beJAxaBiGr3y119ppbVnKqleaoFEYi60Yd23JKusDLWbkajq7/0Do0d1hw6WfY31E7F4yQqICdu+7gR0styr+2aXTy76DbFJPkv9FwOBipOSWYil6hqPRA0ahC5+LHgS8fQ6MyFvbt5A10PEJau/qIZDhp root@server3

````


## core-site.xml
````aidl
<configuration>  
    <!-- 指定hdfs的nameservice为ns1 -->  
    <property>  
        <name>fs.defaultFS</name>  
        <value>hdfs://mycluster</value>  
    </property>  
  
    <!-- 指定hadoop临时目录 -->  
    <property>  
        <name>hadoop.tmp.dir</name>  
        <value>/usr/local/hadoop/tmp</value>  
    </property>  
  
    <!-- 指定zookeeper地址 -->  
    <property>  
        <name>ha.zookeeper.quorum</name>  
        <value>server1:2181,h1s2:2181,server2:2181,server3:2181</value>  
    </property>  
</configuration>  
````

## hdfs-site.xml
````aidl
<configuration>  
    <!--指定hdfs的nameservice为mycluster，需要和core-site.xml中的保持一致 -->  
    <property>  
        <name>dfs.nameservices</name>  
        <value>mycluster</value>  
    </property>  
      
    <!-- mycluster下面有两个NameNode，分别是nn1，nn2 -->  
    <property>  
        <name>dfs.ha.namenodes.mycluster</name>  
        <value>server1,server2</value>  
    </property>  
      
    <!-- server1的RPC通信地址 -->  
    <property>  
        <name>dfs.namenode.rpc-address.mycluster.server1</name>  
        <value>server1:9000</value>  
    </property>  
      
    <!-- server1的http通信地址 -->  
    <property>  
        <name>dfs.namenode.http-address.mycluster.server1</name>  
        <value>server1:50070</value>  
    </property>  
      
    <!-- server2的RPC通信地址 -->  
    <property>  
        <name>dfs.namenode.rpc-address.mycluster.server2</name>  
        <value>server2:9000</value>  
    </property>  
      
    <!-- server2的http通信地址 -->  
    <property>  
        <name>dfs.namenode.http-address.mycluster.server2</name>  
        <value>server2:50070</value>  
    </property>  
      
    <!-- 指定NameNode的元数据在JournalNode上的存放位置 -->  
    <property>  
        <name>dfs.namenode.shared.edits.dir</name>  
        <value>qjournal://server1:8485;server2:8485/mycluster</value>  
    </property>  
      
    <!-- 指定JournalNode在本地磁盘存放数据的位置 -->  
    <property>  
        <name>dfs.journalnode.edits.dir</name>  
        <value>/usr/local/hadoop/journal</value>  
    </property>  
      
    <!-- 开启NameNode失败自动切换 -->  
    <property>  
        <name>dfs.ha.automatic-failover.enabled</name>  
        <value>true</value>  
    </property>  
      
    <!-- 配置失败自动切换实现方式 -->  
    <property>  
        <name>dfs.client.failover.proxy.provider.mycluster</name>  
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>  
    </property>  
      
    <!-- 配置隔离机制方法，多个机制用换行分割，即每个机制暂用一行-->  
    <property>  
        <name>dfs.ha.fencing.methods</name>  
        <value>  
            sshfence  
            shell(/bin/true)  
        </value>  
    </property>  
      
    <!-- 使用sshfence隔离机制时需要ssh免登陆 -->  
    <property>  
        <name>dfs.ha.fencing.ssh.private-key-files</name>  
        <value>/root/.ssh/id_rsa</value>  
    </property>  
      
    <!-- 配置sshfence隔离机制超时时间 -->  
    <property>  
        <name>dfs.ha.fencing.ssh.connect-timeout</name>  
        <value>30000</value>  
    </property>  
</configuration>  
````

## mapred-site.xml
````aidl
<configuration>  
    <property>  
        <name>mapreduce.framework.name</name>  
        <value>yarn</value>  
        <final>true</final>  
     </property>  
</configuration>  
````

## yarn-site.xml
````aidl
<configuration>  
    <!-- 开启RM高可靠 -->  
    <property>  
        <name>yarn.resourcemanager.ha.enabled</name>  
        <value>true</value>  
    </property>  
  
    <!-- 指定RM的cluster id -->  
    <property>  
        <name>yarn.resourcemanager.cluster-id</name>  
        <value>yrc</value>  
    </property>  
  
    <!-- 指定RM的名字 -->  
    <property>  
        <name>yarn.resourcemanager.ha.rm-ids</name>  
        <value>rm1,rm2</value>  
    </property>  
  
    <!-- 分别指定RM的地址 -->  
    <property>  
        <name>yarn.resourcemanager.hostname.rm1</name>  
        <value>server1</value>  
    </property>  
  
    <property>  
        <name>yarn.resourcemanager.hostname.rm2</name>  
        <value>server1</value>  
    </property>  
  
    <!-- 指定zk集群地址 -->  
    <property>  
        <name>yarn.resourcemanager.zk-address</name>  
        <value>server1:2181,server2:2181,server3:2181</value>  
    </property>  
  
    <property>  
        <name>yarn.nodemanager.aux-services</name>  
        <value>mapreduce_shuffle</value>  
    </property>  
</configuration>
````

## 修改slaves
````aidl
/usr/local/hadoop/hadoop-2.8.2/etc/hadoop slaves
````

