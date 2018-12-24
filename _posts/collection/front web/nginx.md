* [centos安装](http://blog.csdn.net/yx0628/article/details/53148451)
* [nginx.cn](http://www.nginx.cn/nginx-how-to)

* 代理配置

````aidl
    server {
        listen       8002;
        server_name  127.0.0.1;::

        #charset koi8-r;

        #access_log  logs/host.access.log  main;


        location /p-trade-oc-admin/ {
            proxy_pass http://urms.uat1.rs.com:80;
        }

        location / {
            root /Users/M/work/redstar/p-trade/p-trade-oc/p-trade-oc-admin/src/main/resources/public/;
            index pages/BackOrder/backOrderList.html;
            try_files $uri  $uri/ /index.html = 404;
        }

    }

````