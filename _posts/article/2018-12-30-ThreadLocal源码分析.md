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


上面两段代码截取jdk8源码,`Thread`对象内部定义了成员变量`ThreadLocal.ThreadLocalMap threadLocals = null`,`ThreadLocalMap`为`ThreadLocal`的一个静态内部类,三者的关系就这么简单.

![](https://raw.githubusercontent.com/m65536/resource/master/image/java/thread/thread_local_0.png)


# Reference
* [使用threadlocal不当可能会导致内存泄露](http://ifeve.com/%E4%BD%BF%E7%94%A8threadlocal%E4%B8%8D%E5%BD%93%E5%8F%AF%E8%83%BD%E4%BC%9A%E5%AF%BC%E8%87%B4%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2/)
* [ThreadLocal源码深度剖析(rehash讲解)](https://juejin.im/post/5a5efb1b518825732b19dca4)