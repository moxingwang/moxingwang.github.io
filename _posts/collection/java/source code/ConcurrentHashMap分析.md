* [ConcurrentHashMap源码分析（JDK8版本）](http://blog.csdn.net/u010723709/article/details/48007881)
* [JAVA CAS原理深度分析](http://blog.csdn.net/Hsuxu/article/details/9467651)
* [Java中CAS详解](http://blog.csdn.net/ls5718/article/details/52563959)
* [Java多线程里总线锁定和缓存一致性的问题](http://blog.csdn.net/java1993666/article/details/77880651)
* [JAVA CAS原理深度分析](http://zl198751.iteye.com/blog/1848575)
* [《Java源码分析》：ConcurrentHashMap JDK1.8](http://blog.csdn.net/u010412719/article/details/52145145)
* [Java7/8 中的 HashMap 和 ConcurrentHashMap 全解析](https://www.javadoop.com/post/hashmap#toc13)
* [ConcurrentHashMap 1.8 源码分析](https://my.oschina.net/ovirtKg/blog/777520)
* [Java 8 中的ConcurrentHashMap源码分析](http://www.voidcn.com/article/p-ztozinbh-vy.html)
* [java8集合框架(三)－Map的实现类（ConcurrentHashMap）](http://wuzhaoyang.me/2016/09/05/java-collection-map-2.html)
* [探索jdk8之ConcurrentHashMap 的实现机制](http://www.cnblogs.com/huaizuo/p/5413069.html)
* [自旋锁与互斥锁的对比、手工实现自旋锁](http://blog.csdn.net/freeelinux/article/details/53695111)
* [java-并发-ConcurrentHashMap高并发机制-jdk1.8](http://blog.csdn.net/jianghuxiaojin/article/details/52006118)

````
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n;
        }
        int nextn = nextTab.length;
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        boolean advance = true;
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0;;) {
            Node<K,V> f; int fh;
            while (advance) {
                int nextIndex, nextBound;
                if (--i >= bound || finishing)
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                    i = -1;
                    advance = false;
                }
                else if (U.compareAndSwapInt
                         (this, TRANSFERINDEX, nextIndex,
                          nextBound = (nextIndex > stride ?
                                       nextIndex - stride : 0))) {
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K,V> ln, hn;
                        if (fh >= 0) {
                            int runBit = fh & n;
                            Node<K,V> lastRun = f;
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                else
                                    hn = new Node<K,V>(ph, pk, pv, hn);
                            }
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                        else if (f instanceof TreeBin) {
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
````

````apple js
//很重要的一个字段。
//负数：table正在初始化或扩容，－1初始化，－(1＋参与扩容的线程数)
//当table为null，保存要初始化table的size。
//初始化过后，保存下一次扩容的阈值
private transient volatile int sizeCtl;
//bucket，hash桶
transient volatile Node<K,V>[] table;
//只会在扩容时有用的临时表
private transient volatile Node<K,V>[] nextTable;
//ConcurrentHashMap的元素个数＝baseCount＋SUM(counterCells)!!
//元素基础个数，通过CAS更新，当CAS失败则将要加的值加到counterCells数组
private transient volatile long baseCount;
//下一个线程领扩容任务时，分配的hash桶起始索引
private transient volatile int transferIndex;
//用数组来处理当CAS失败时，元素统计提高效率的方案。参见java.util.concurrent.atomic.LongAdder
private transient volatile CounterCell[] counterCells;

````

## 问题
* CAS后为什么还要if null的判断？

## 要点
* new ForwardingNode是放到了老table的桶位，引用了新的table。