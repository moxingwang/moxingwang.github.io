为了彻底搞明白`ThreadLocal`的工作原理,下面会截取代码和画图详细说明.

先搞清楚`Thread`,`ThreadLocal`,`ThreadLocalMap`这三个类的关系.


```
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```

```
public class ThreadLocal<T> {
	static class ThreadLocalMap {

	}
}
```


上面两端代码截取jdk8源码,`Thread`对象内部定义了成员变量`ThreadLocal.ThreadLocalMap threadLocals = null`,`ThreadLocalMap`为`ThreadLocal`的一个静态内部类,三者的关系就这么简单.

![](https://raw.githubusercontent.com/m65536/resource/master/image/java/thread/thread_local_0.png)
