## 概述
最近在看WeakHashMap源码的时候注意到java.lang.ref.WeakReference,java.lang.ref.ReferenceQueue这两个类,
正好写一篇文章来帮助自己理解和记忆。

## java.lang.ref
Java.lang.ref 是 Java 类库中比较特殊的一个包，它提供了与 Java 垃圾回收器密切相关的引用类。这些引用类对象可以指向其它对象，但它们不同于一般的引用，因为它们的存在并不防碍 Java 垃圾回收器对它们所指向的对象进行回收。其好处就在于使者可以保持对使用对象的引用，同时 JVM 依然可以在内存不够用的时候对使用对象进行回收。

Reference 是一个抽象类，而 SoftReference，WeakReference，PhantomReference 以及 FinalReference 都是继承它的具体类。

### StrongReference

### SoftReference

### WeakReference

### PhantomReference

## 参考
* [深入探讨 java.lang.ref 包](https://www.ibm.com/developerworks/cn/java/j-lo-langref/)
* [JVM源码分析之FinalReference完全解读](http://www.infoq.com/cn/articles/jvm-source-code-analysis-finalreference)
* [JVM结构、GC工作机制详解](http://www.jianshu.com/p/a94912709e29)
* [JAVA GC 原理详解](https://segmentfault.com/a/1190000008384410)
* [Java垃圾回收基础的系列文章](http://youli9056.github.io/blog/java-garbage-collection-introduction/)
* [JVM 架构解读](http://www.codeceo.com/article/jvm-architecture-explained.html)