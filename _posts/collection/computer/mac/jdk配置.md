## mac配置
```
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

export M2_HOME="/usr/local/apache-maven-3.3.9"
export M2="$M2_HOME/bin"
export PATH="$M2:$PATH"
export PATH="/usr/local/mysql/bin:$PATH"
```

## win配置
```aidl
(1)新建->变量名"JAVA_HOME"，变量值"C:\Java\jdk1.8.0_05"（即JDK的安装路径） 
(2)编辑->变量名"Path"，在原变量值的最后面加上“;%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin” 
(3)新建->变量名“CLASSPATH”,变量值“.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar”
```