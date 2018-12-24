````
mvn deploy:deploy-file -DgroupId=com.chinaredstar -DartifactId=oms-order-api -Dversion=1.6.0 -Dpackaging=jar -Dfile=oms-order-api.jar -Durl=http://nexus.core.rs.com/nexus/content/repositories/releases/ -DrepositoryId=releases
````