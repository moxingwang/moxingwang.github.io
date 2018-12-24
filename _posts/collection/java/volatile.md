# 一 volatile用来解决什么问题

声明为volatile的变量在多线程共享的情况下，java内存模型保证了所有线程看到的变量一致的。（[Java Language Specification volatile Fields](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.1.4)）

>java程序运行在java虚拟机平台上，java程序员不可能直接去控制底层线程对寄存器高速缓存内存之间的同步，那么java从语法层面，应该给开发人员提供一种解决方案，这个方案就是诸如 synchronized, volatile,锁机制（如同步块，就绪队 列，阻塞队列）等等。


# 二 问题产生的原因

## 1. jvm内存模型

* jvm内存模型

按照jvm内存模型的规范，java多线程存在共享数据区域。[Run-Time Data Areas](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5)。

* 工作内存

线程的working memory是cpu的寄存器和高速缓存的抽象描述。cpu在计算的时候，并不总是从内存读取数据，它的数据读取顺序优先级是：寄存器－高速缓存－内存。

## 2. CPU缓存

首先要知道进程是组织资源的最小单位,而线程是安排CPU执行的最小单位单。而进程多线程可以同时用到CPU的双核心，每个CPU核心都有自己独立的高速缓存。[英特尔® 64 位和 IA-32 架构开发人员手册：卷 3A CHAPTER 8 # 8.8 MULTI-CORE ARCHITECTURE ](http://www.intel.cn/content/www/cn/zh/architecture-and-technology/64-ia-32-architectures-software-developer-vol-3a-part-1-manual.html?wapkw=ia-32+%E6%9E%B6%E6%9E%84%E5%BC%80%E5%8F%91%E4%BA%BA%E5%91%98%E6%89%8B%E5%86%8C)。

正确理解CPU缓存的知识，推荐参考官方文档[英特尔® 64 位和 IA-32 架构开发人员手册：卷 3A CHAPTER 11](http://www.intel.cn/content/www/cn/zh/architecture-and-technology/64-ia-32-architectures-software-developer-vol-3a-part-1-manual.html?wapkw=ia-32+%E6%9E%B6%E6%9E%84%E5%BC%80%E5%8F%91%E4%BA%BA%E5%91%98%E6%89%8B%E5%86%8C)。

对于CPU缓存的知识一点要明确这几个点：

* 2.1 CPU要读取一个数据时，首先从Cache中查找，如果找到就立即读取并送给CPU处理；如果没有找到，就用相对慢的速度从内存中读取并送给CPU处理，同时把这个数据所在的数据块调入Cache中，可以使得以后对整块数据的读取都从Cache中进行，不必再调用内存。

* 2.2 缓存是由缓存行组成的。CPU存取缓存都是按照一行为最小单位操作的，一般一行缓存行有64字节，使用缓存时，并不是一个一个字节使用，而是一行缓存行、一行缓存行这样使用。

* 2.3 CPU操作完数据后并不是马上写入主存，CPU有多种写入策略，比如说回写，CPU会在适当的时间把缓存中的脏数据写入主存，保证缓存和主存一致。

* 2.4 地址翻译，由于计算机程序一般使用虚拟地址，一个必须决定的设计策略是缓存的地址标签及索引是使用虚拟地址还是物理地址。

## 3. reordering

# 三 volatile是如何解决问题的

知道了java多线程可以共享数据、CPU缓存工作原理，以及volatile要解决的问题就是保证多线程对共享数据的读的可见性，接下来说明volatile是如何保证内存可见性的。

## 1. 实现原理

理解了CPU缓存的工作原理以及jvm内存模型，多线程共享数据可见性的主要问题在于CPU高速缓存里面的数据什么时间写入到主存，以及写入主存之后如何通知其他线程缓存的数据失效。

* happens-before Order [Java Language Specification 17.4.5. Happens-before Order](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4.5)


## 2. 证明实现原理

# 提出问题

## 1. 多线程在单核CPU的情况下，会有可见性的问题吗？

# 四 volatile的使用




`参考`
* [Java内存访问重排序的研究](https://tech.meituan.com/java-memory-reordering.html)
* [英特尔® 64 位和 IA-32 架构开发人员手册](http://www.intel.cn/content/www/cn/zh/search.html?toplevelcategory=none&query=%20IA-32%20%E6%9E%B6%E6%9E%84%E5%BC%80%E5%8F%91%E4%BA%BA%E5%91%98%E6%89%8B%E5%86%8C&keyword=%20IA-32%20%E6%9E%B6%E6%9E%84%E5%BC%80%E5%8F%91%E4%BA%BA%E5%91%98%E6%89%8B%E5%86%8C&:cq_csrf_token=undefined)
* [Java Language and Virtual Machine Specifications](https://docs.oracle.com/javase/specs/)
* [JSR 133 (Java Memory Model) FAQ](http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html#volatile)
* [JVM内存模型、指令重排、内存屏障概念解析](http://www.cnblogs.com/chenyangyao/p/5269622.html)
* [Volatile and memory barriers](http://jpbempel.blogspot.co.uk/2013/05/volatile-and-memory-barriers.html)
* [Does cache line flush write the whole line to the memory?](https://stackoverflow.com/questions/18001954/does-cache-line-flush-write-the-whole-line-to-the-memory)
* [What Volatile Means in Java](http://jeremymanson.blogspot.hu/2008/11/what-volatile-means-in-java.html)
* [How Volatile in Java works? Example of volatile keyword in Java](http://javarevisited.blogspot.com/2011/06/volatile-keyword-java-example-tutorial.html)
* [Java线程内存模型,线程、工作内存、主内存](https://zhuanlan.zhihu.com/p/25474331)
* [CPU缓存](https://zh.wikipedia.org/wiki/CPU%E7%BC%93%E5%AD%98)
* [Multithreading and Cache Coherence](http://docs.roguewave.com/threadspotter/2011.2/manual_html_linux/manual_html/ch_intro_coherence.html)
* [Java可见性机制的原理](http://developer.51cto.com/art/201611/521120.htm)
* [Fixing the Java Memory Model, Part 2](https://www.ibm.com/developerworks/library/j-jtp03304/)
* [关于单CPU，多CPU上的原子操作 intel.com](https://software.intel.com/zh-cn/blogs/2010/01/14/cpucpu)
* [聊聊并发（一）——深入分析Volatile的实现原理](http://www.infoq.com/cn/articles/ftf-java-volatile)