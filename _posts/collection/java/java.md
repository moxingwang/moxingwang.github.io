# We cannot execute /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home/bin/java

```
vim ~/.bash_profile 
export JAVA_HOME=$(/usr/libexec/java_home)
source ~/.bash_profile
```
