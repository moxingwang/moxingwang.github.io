## 部署参照
````aidl
http://www.cnblogs.com/yyhh/p/6106472.html

http://blog.csdn.net/xiaozhuanddapang/article/details/75554807
````

sudo scp m@10.11.28.213:/Users/M/Downloads/pinpoint-web-1.6.2.war /root/pinpoint
sudo scp m@10.11.28.213:/Users/M/Downloads/pinpoint-collector-1.6.2.war /root/pinpoint
sudo scp m@10.11.28.213:/Users/M/Downloads/hbase-create.hbase /root/pinpoint

## 解压war
````aidl
unzip -oq common.war -d common
````

## tomcat访问不了需要修改server.xml
````aidl
<Host name="10.168.17.135"  appBase="webapps"
            unpackWARs="true" autoDeploy="true">

        <!-- SingleSignOn valve, share authentication between web applications
             Documentation at: /docs/config/valve.html -->
        <!--
        <Valve className="org.apache.catalina.authenticator.SingleSignOn" />
        -->

        <!-- Access log processes all example.
             Documentation at: /docs/config/valve.html
             Note: The pattern used is equivalent to using pattern="common" -->
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="10.168.17.135_access_log" suffix=".txt"
               pattern="%h %l %u %t "%r" %s %b" />

      </Host>

````