## ArrayList
首先说说ArrayList，估计他是大多数java程序员用过的最多的集合类了吧，ArrayList并没
有多么高大上的地方。想必大家都知道ArrayList就是围绕一个数组，包装了一些方法方便调用
者对其数组操作，对，就这么简单。推荐大家阅读源码一探究竟。接下来列举我平时使用
ArrayList的一些心得。

* 初始化ArrayList建议指定容量大小
>我们知道数组在初始化的时候一定要指定它的size,ArrayList在创建的时候如果不指定大小
默认大小是10个元素,随着元素的增多，超过了内部数组容量大小就会触发ensureExplicitCapacity
方法对数组扩容，为了避免不断扩容操作，建议在初始化的时候根据数据的大小指定容量。

* 选用普通for循环而不是Iterator
>为什么要使用普通for循环呢？对于ArrayList来说他实现了RandomAccess接口，循环速度
会快于Iterator，或者增强for循环（底层就是Iterator实现）。

* ArrayList非线程安全
>在多线程的环境下肯定不能使用ArrayList作为共享变量操作数据。

* toArray()，可能引起“java.lang.ClassCastException”
> 对于这个问题，需要理解清楚对象继承关系，以及typeInstance.toArray后虽然返回的Object[],然而实质
上typeInstance的类型，理解这一点非常重要。具体请参考[JDK-6260652 : (coll) Arrays.asList(x).toArray().getClass() should be Object[].class](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6260652)

## LinkedList
