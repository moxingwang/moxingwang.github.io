为了彻底搞明白`ThreadLocal`的工作原理,下面会截取代码和画图详细说明.


# 简单ThreadLocal工作流程

* 首先搞清楚`Thread`,`ThreadLocal`,`ThreadLocalMap`这三个类的关系.


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


上面两段代码截取jdk8源码,`Thread`对象内部定义了成员变量`ThreadLocal.ThreadLocalMap threadLocals = null`,`ThreadLocalMap`为`ThreadLocal`的一个静态内部类,三者的代码关系就这么简单.


![](https://github.com/m65536/resource/blob/master/image/java/thread/thread_local_0.png?raw=true)


1. 


# 源码分析
```
//n为当前table有效元素的个数
private boolean cleanSomeSlots(int i, int n) {
    boolean removed = false;
    Entry[] tab = table;
    int len = tab.length;
    do {
        //nextIndex方法能够保证从i到i-1遍历整个table
        i = nextIndex(i, len);
        Entry e = tab[i];
        
         //Eentry不为空但是key为空的entry对象
        if (e != null && e.get() == null) {
            n = len;
            removed = true;
            i = expungeStaleEntry(i);
        }
        
        //无符号右移位,执行n次
    } while ( (n >>>= 1) != 0);
    return removed;
}
```

```
private int expungeStaleEntry(int staleSlot) {
    Entry[] tab = table;
    int len = tab.length;

    // expunge entry at staleSlot
    tab[staleSlot].value = null;
    tab[staleSlot] = null;
    size--;

    // Rehash until we encounter null
    Entry e;
    int i;
    for (i = nextIndex(staleSlot, len);
         (e = tab[i]) != null;
         i = nextIndex(i, len)) {
        ThreadLocal<?> k = e.get();
        if (k == null) {
            e.value = null;
            tab[i] = null;
            size--;
        } else {
        	//重新计算hash
            int h = k.threadLocalHashCode & (len - 1);
            if (h != i) {
                tab[i] = null;

                // Unlike Knuth 6.4 Algorithm R, we must scan until
                // null because multiple entries could have been stale.
                while (tab[h] != null)
                    h = nextIndex(h, len);
                tab[h] = e;
            }
        }
    }
    return i;
}

```

# Reference
* [使用threadlocal不当可能会导致内存泄露](http://ifeve.com/%E4%BD%BF%E7%94%A8threadlocal%E4%B8%8D%E5%BD%93%E5%8F%AF%E8%83%BD%E4%BC%9A%E5%AF%BC%E8%87%B4%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2/)
* [ThreadLocal源码深度剖析(rehash讲解)](https://juejin.im/post/5a5efb1b518825732b19dca4)