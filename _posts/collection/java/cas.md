* [浅谈CAS(Compare and Swap) 原理](https://www.cnblogs.com/Leo_wl/p/6899716.html)
* [面试问题（java cas有什么优点和问题）](http://blog.csdn.net/hxpjava1/article/details/79408692)


### 优点
* 利用CPU的CAS指令，同时借助JNI来完成Java的非阻塞算法。
* 基于CPU保证基本内存操作的原子性。

### 缺点
* ABA问题。
* 自旋CAS如果长时间不成功，会给CPU带来非常大的执行开销。
* 只能保证一个共享变量的原子操作。