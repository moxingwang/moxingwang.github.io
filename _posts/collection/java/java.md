# We cannot execute /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/bin/java

```
vim ~/.bash_profile 
export JAVA_HOME=$(/usr/libexec/java_home)
source ~/.bash_profile
```


# maven相关项目
### 如何运行maven项目
```
mvn exec:java -Dexec.mainClass="com.alibaba.fescar.tm.dubbo.impl.AccountServiceImpl"
```

[使用Maven运行Java main的3种方式](https://blog.csdn.net/qbg19881206/article/details/19850857)

### maven初始化创建项目
```
mvn archetype:generate -DgroupId=top.moxingwang -DartifactId=fescar -Dversion=1.0-SNAPSHOT -Dpackage=top.moxingwang.fescar
```

