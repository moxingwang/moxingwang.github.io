````aidl
MAVEN_HOME=/usr/local/maven/apache-maven-3.5.2
export MAVEN_HOME
export PATH=${PATH}:${MAVEN_HOME}/bin
````

## 打包
````$xslt
mvn -U clean install -P dev  -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -am
````
