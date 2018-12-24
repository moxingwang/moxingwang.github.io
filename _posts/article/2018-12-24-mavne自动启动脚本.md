````
#!/bin/sh

cd /data/workspace/p-trade/p-trade
git pull

mvn clean install -f /data/workspace/p-trade/p-trade/p-trade-bill-common
mvn clean install -f /data/workspace/p-trade/p-trade/p-trade-bill-api
mvn clean install -f /data/workspace/p-trade/p-trade/p-trade-bill-integration
mvn clean install -f /data/workspace/p-trade/p-trade/p-trade-bill-jdbc
mvn clean package -f /data/workspace/p-trade/p-trade/p-trade-bill-service -Pdev -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
mvn clean package -f /data/workspace/p-trade/p-trade/p-trade-bill-rest  -Pdev -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

/data/www/p-trade-bill-rest-1.0.1.RELEASE/bin/p-trade-bill-rest stop
/data/www/p-trade-bill-service-1.0.1.RELEASE/bin/p-trade-bill-service stop

rm -rf /data/www/p-trade-bill-rest*
rm -rf /data/www/p-trade-bill-service*

cp /data/workspace/p-trade/p-trade/p-trade-bill-service/target/p-trade-bill-service-1.0.1.RELEASE-release.zip /data/www
cp /data/workspace/p-trade/p-trade/p-trade-bill-rest/target/p-trade-bill-rest-1.0.1.RELEASE-release.zip /data/www

unzip /data/www/p-trade-bill-service-1.0.1.RELEASE-release.zip -d /data/www
unzip /data/www/p-trade-bill-rest-1.0.1.RELEASE-release.zip -d /data/www

/data/www/p-trade-bill-service-1.0.1.RELEASE/bin/p-trade-bill-service start
/data/www/p-trade-bill-rest-1.0.1.RELEASE/bin/p-trade-bill-rest start
````