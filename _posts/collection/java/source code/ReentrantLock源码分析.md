ReentrantLock源码分析
=============

# 概述
 在JAVA中通常实现锁有两种方式，一种是synchronized关键字，另一种是Lock接口。synchronized是基于jvm层面实现的，Lock是基于jdk底层实现，接下来主要来分析ReentrantLock源码，理解ReentrantLock实现原理。 

 为了理解锁的重要性和存在的意义，首先应该先思考以下几个问题，边思考边去阅读源码。 

 1. 程序中为什么要使用锁？
 2. 常见实现锁的方法，你知道的有哪些？
 3. Lock与synchronized的区别在哪里？

# ReentrantLock源码
 ReentrantLock类在java.util.concurrent.locks包中，它的上一级的包java.util.concurrent主要是常用的并发控制类，它是基于AQS（AbstractQueuedSynchronizer）实现的，这里先不说那么多原理，那么，我们就从AbstractQueuedSynchronizer的其中一个实现类ReentrantLock说起，理解AQS的实现原理，先来看看他们直接的关系。

![图1](https://raw.githubusercontent.com/moxingwang/resource/master/image/Sync结构.png)图1
![图2](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentranLock内部类Sync.png)图2

 ReentrantLock类的API调用都委托给一个静态内部类Sync，该类继承了AbstractQueuedSynchronizer类；而Sync分表有两个子类FairSync和NonfairSync，这也是我们常说的公平锁与非公平锁，他们两者有什么区分呢？我们先从非公平锁NonfairSync开始分析，最后再总结他们之间的异同。

````
Lock lock = new ReentrantLock();

        try {
            lock.lock();

        }finally {
            lock.unlock();
        }
````

 以上代码是对Lock最常见的使用方法，先上锁然后再释放锁，如果忘记释放锁就会产生死锁问题，通常在finally代码块中释放锁，ReentrantLock有两个构造器，无参构造器默认创建了的是非公平锁，接下来我们先以非公平锁模拟多个线程竞争锁，来具体分析源码。 

````
 public ReentrantLock() {
        sync = new NonfairSync();
    }

    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
````

### 模拟线程竞争非公平锁分析源码
 首先假设有以下代码片段（着里抽象描述代码结构，不按这里执行）。

````
public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();

        try {
            reentrantLock.lock();
            //do something
        } finally {
            reentrantLock.unlock();
        }

        Thread t1 = new Thread(() -> {
            try {
                reentrantLock.lock();
                //do something
            } finally {
                reentrantLock.unlock();
            }
        });
        t1.setName("线程2");
        t1.start();


        Thread t2 = new Thread(() -> {
            try {
                reentrantLock.lock();
                //do something
            } finally {
                reentrantLock.unlock();
            }
        });
        t2.setName("线程3");
        t2.start();
        
        System.out.println("测试完成");
    }
````

### lock

 ReentrantLock被实例化后（实例对象称为rLock,方便下文描述），第一个线程调用lock方法获取锁，该方法首先使用CAS去更新AQS中state的值，如果更新成功那么当前线程抢占锁成功，显然ReentrantLock实例化后默认值就是0，抢占成功，既当前锁被线程1独占。

````
static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }
````

 * compareAndSetState做了什么？
 
  compareAndSetState方法是贯穿于整个ReentrantLock实现原理的重中之重，理解这个方法必须先要理解CAS的原理，这里简单的说一下CAS原理。比较和替换是设计并发算法时用到的一种技术。简单来说，比较和替换是使用一个期望值和一个变量的当前值进行比较，如果当前变量的值与我们期望的值相等，就使用一个新值替换当前变量的值。在JAVA中CAS的操作被封装到了Unsafe这个类中，看源码的时候常常看到以compareAndSwap打头的方法，看到这样的方法，不必要大惊小怪，原理都一样，都是CAS操作。并且这些方法都是native方法，利用JNI来完成CPU指令的操作,JAVA的CAS最终利用了CPU的原子操作来保证了JAVA原子操作。
 
 * setExclusiveOwnerThread做了什么？ 

 setExclusiveOwnerThread只是一个简单的set操作，他更新了rLock中的exclusiveOwnerThread属性，exclusiveOwnerThread是AQS类中的一个实例变量（"private transient Thread exclusiveOwnerThread;"）用来引用当前锁的持有者。

 此时假设thread1还没有执行完到unlock，即还未释放锁，另一个线程thread2进入，那么thread2首先会进行抢占式的去获取锁调用compareAndSetState，此时thread1还未释放锁，compareAndSetState方法返回false，thread2抢占锁失败。接下来调用acquire方法，此方法在AbstractQueuedSynchronizer中，源码如下。

````
 public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
````
【代码块1】

 着里的方法调用比较复杂，首先我们给一张图，说明这些方法都在哪些类中。

 ![](https://raw.githubusercontent.com/moxingwang/resource/master/image/非公平锁qcquire调用图.png)

 先来看ReentrantLock中的nonfairTryAcquire方法，源码如下：

````
final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            //获取AQS的state属性，getState方法在AQS类中，直接返回了state变量的值。
            int c = getState();
            //=0表示没有线程持有锁，
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    //竞争锁成功
                    return true;
                }
            }
            //当前持有锁的线程就是本身，那么重入，这里也是重入锁的核心，之前我对这一行代码非常疑惑，看了知乎的一个话题后霍然开朗，主要是没有理解重入锁和自旋锁的概念。
            //这里实现了偏向锁
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                //限制了重入锁的次数是小于Integer.MAX_VALUE,这是为什么呢?我们知道int的最大值是2147483647，当加一后二进制符号为1，此刻为-2147483648。
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                //竞争锁成功
                return true;
            }
            //未竞争到锁
            return false;
        }
````

* state

 state是AQS中的一个实例变量（private volatile int state;），他主要负责记录是否有线程持有锁，以及同一个线程重入的次数。当state=0的时候表示没有线程持有锁，state>0表示已有线程持有锁，他的值就表示重入的次数。当然了state是基于CAS原子操作的，compareAndSetState方法就是用来修改state的值。

* 理解自旋锁和重入锁
 
 [java的可重入锁用在哪些场合？](https://www.zhihu.com/question/23284564)

 线程2进入nonfairTryAcquire方法后，此时state为1，getExclusiveOwnerThread为线程1，最终return false，回到【代码块1】线程2会执行到『acquireQueued(addWaiter(Node.EXCLUSIVE), arg)』这里，代码会先走到addWaiter方法，我们先看看addWaiter的源码。

````
private Node addWaiter(Node mode) {
        //在这里mode仅仅用来指定Node的模式（共享或者独占，作为Node对象属性nextWaiter的引用）
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        //如果队列中已经有了Node，直接把node添加到队列的尾部
        if (pred != null) {
            node.prev = pred;
            //设置全局变量tail为新增的node，使用CAS保证多个线程下，设置tail的原子性。CAS成功才真正的添加到队列的尾部。
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        //如果队列中不存在等待线程，或者compareAndSetTail失败，调用enq方法
        enq(node);
        return node;
    }
````

````
private Node enq(final Node node) {
        //自旋
        //在多个线程进入的情况下，最终还是CAS保证了原子性。
        for (;;) {
            Node t = tail;
            //如果队列中没有元素，初始化一个空的node，并且设置为header，当然了，tail也是指向这个node
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                //添加到队列的尾部
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    //自旋锁的唯一出口
                    return t;
                }
            }
        }
    }
````
    

 addWaiter方法的作用就是把当前无法获得锁的线程包装成一个Node添加到队列的尾部。 enq是一个自旋锁，保证了初始化一个空的node作为header，同时保证了多个线程的情况下添加队列到尾部的安全性。既然addWaiter方法add的是Node对象，接着看Node对象的源码。

````
static final class Node {
        //标志共享模式
        static final Node SHARED = new Node();
        //标志独占模式
        static final Node EXCLUSIVE = null;
        //因为超时或者中断，结点会被设置为取消状态，被取消状态的结点不应该去竞争锁，只能保持取消状态不变，不能转换为其他状态。处于这种状态的结点会被踢出队列，被GC回收。 
        static final int CANCELLED =  1;
        //从前面的代码状态转换可以看得出是前面有线程在运行，需要前面线程结束后，调用unpark()方法才能激活自己。
        static final int SIGNAL    = -1;
        //线程基于Condition对象发生了等待，进入了相应的队列，自然也需要Condition对象来激活
        static final int CONDITION = -2;
        //使用在共享模式头结点有可能牌处于这种状态，表示锁的下一次获取可以无条件传播。
        static final int PROPAGATE = -3;
        //等待状态,初始状态为0（新节点的状态）
        volatile int waitStatus;
        //头节点
        volatile Node prev;
        //引用链表的下一个节点
        volatile Node next;
        //引用当前节点所代表的线程
        volatile Thread thread;
        //Node既可以作为同步队列节点使用，也可以作为Condition的等待队列节点使用(将会在后面讲Condition时讲到)。在作为同步队列节点时，nextWaiter可能有两个值：EXCLUSIVE、SHARED标识当前节点是独占模式还是共享模式；在作为等待队列节点使用时，nextWaiter保存后继节点。在这里我们先只关心作为同步队列节点使用，nextWaiter用来引用Node.SHARED或者Node.EXCLUSIVE。
        Node nextWaiter;

        /**
         * Returns true if node is waiting in shared mode.
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
````

 继续说线程2在【代码块1】的执行位置，addWaiter执行完后，此时AQS的链表应该是这样一个结构。
  ![](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentrantLock/thread2Entry.jpg)
 
 链表中的head是一个空的node，tail引用线程2，在看acquireQueued方法的源码。

````
final boolean acquireQueued(final Node node, int arg) {
        //用来检查线程最终没有没有获得锁，如果未获得锁，从队列中释放Thread。
        boolean failed = true;
        try {
            //用来标识线程是否被中断过
            boolean interrupted = false;
            //自旋
            for (;;) {
                final Node p = node.predecessor();
                //尝试获得锁，直接进入这个if操作完成后，acquireQueued方法才会退出。
                if (p == head && tryAcquire(arg)) {
                    //线程被上一个执行完的线程唤醒后获得锁，自己又作为header
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                //parkAndCheckInterrupt方法阻塞线程
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            //如果在这期间线程被中断，就抛出中断异常，如果有其他异常产生，就取消这次获取。
            if (failed)
                //清理状态，node出队。
                cancelAcquire(node);
        }
    }
````

 thread2进入for循环后，predecessor获取到的node，可以知道获取到的node就是header，再次尝试调用tryAcquire方法，根据上文中对tryAcquire方法的分析，tryAcquire返回false，接下来逻辑走到if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())逻辑，先看shouldParkAfterFailedAcquire方法的源码。

````
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
            /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             */
            return true;
        if (ws > 0) {
            /*
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
````
 
 从方法名就能够看出此方法的大概意思，在尝试获取锁失败后检查是否应该进入线程阻塞。当thread2线程第一次进入后，header是new Node(),header的waitStates为0，CAS设置成了-1（Node.SIGNAL），表明此时线程的状态已经进入阻塞前提了，接下来回到acquireQueued方法的自旋位置，一个for循环后继续走到了shouldParkAfterFailedAcquire放，这个时候返回true，接下来我们看parkAndCheckInterrupt方法的源码，parkAndCheckInterrupt方法代码很少，做法也是比较简单就是让当前线程thraed2进入阻塞。

````
private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
````
 
 接下来继续看acquireQueued方法里面的最后finally语句里面的cancelAcquire方法，这个方法所做的事情就是在线程被中断，抛出中断异常的时候取消这次获取锁。下面给你一张图方便分析。

````
private void cancelAcquire(Node node) {
        // Ignore if node doesn't exist
        if (node == null)
            return;
        //node不再关联到任何线程
        node.thread = null;

        //跳过被cancel的前继node，找到一个有效的前驱节点pred
        Node pred = node.prev;
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;

        //引用前驱节点的下一个节点
        Node predNext = pred.next;

        //将node的waitStatus置为CANCELLED      
        node.waitStatus = Node.CANCELLED;

        //<1>当前node是tail
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            //<2>当前node既不是head也不是tail
            int ws;
            //这个长长的if其实就是判断pred不为head,waitStatus=-1(等待调用unpark()方法能激活自己的node)
            if (pred != head &&
                ((ws = pred.waitStatus) == Node.SIGNAL ||
                 (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                //<3>当前node是head
                //唤醒node的后续节点的线程
                unparkSuccessor(node);
            }

            node.next = node; // help GC
        }
    }
````

![](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentrantLock/cancelAcquire.jpg)
 
 结合上面的图来分析这段代码，可以看出这个方法主要是node出队，针对Node是head、tail、既不是head也不是tail分了三种情况处理逻辑，仔细阅读源码都可以理解。

 到此位置，我们再回到thread2，此时的thread2已经进入线程阻塞，为了方便我们分析，假设另一个线程同时重复了thread2的操作进入队列也被阻塞了，如下图。

 ![](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentrantLock/thread3Entry.jpg)

### unLock
 
 假如此时thread1执行到了unLock方法，接下来我们看解锁的源码。

![](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentrantLock/unLock_time.png)

````
public void unlock() {
        sync.release(1);
    }
````

````
public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            //唤醒正在等待的header
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
````

````
protected final boolean tryRelease(int releases) {
            //每次重入+1，unLock的时候-1
            int c = getState() - releases;
            //保证了加锁和释放锁必须是同一个线程。
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                //释放node里面的thread引用
                setExclusiveOwnerThread(null);
            }
            //更新node的状态
            setState(c);
            return free;
        }
````

````
private void unparkSuccessor(Node node) {
        //ws<0表明正在等待获取锁
        int ws = node.waitStatus;
        if (ws < 0)
            //更新状态为持有锁
            compareAndSetWaitStatus(node, ws, 0);

        //这里要理解为什么真正的解锁对象是header.next.
        //其实header永远是一个未持有线程的对象，从一开始调用enq(final Node node) 方法的时候，header是new Node(),当持有锁的线程释放的时候，唤醒了header的next线程，next线程被唤醒后，在acquireQueued方法中自旋直到获得锁，获得锁后其Node被设置成了header,当这个线程执行完后又要释放，node的引用的thread设置成了null，着里永远保证了header为空，或者其持有线程已经释放锁。
        Node s = node.next;
        //s为空的时候或者，或者node被取消的情况
        if (s == null || s.waitStatus > 0) {
            s = null;
            //倒序遍历找到正在等待被唤醒的node的线程
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            //唤醒对应node的线程
            LockSupport.unpark(s.thread);
    }
````

 unLock的逻辑相对比较简单，不过想对每一行代码都理解清楚，必须要对加锁的逻辑都理解清楚，以及header是如何变化的，在什么时候headr引用更新，还有就是waitStatus是如何变化的。waitStatus的变化稍微复杂一点。这里我们分别总结一下这个属性的变化过程。

 ![](https://raw.githubusercontent.com/moxingwang/resource/master/image/ReentrantLock/waitStatusChange.jpg)

### 非公平锁和公平锁的区别

 不看源码真的不好理解他们两者的区别，假如你去百度搜索"ReentrantLock 公平锁和非公平锁的区别"，去看好多帖子，可能会看的云里雾里各种举例子，还不如直接上代码理解的快。他们两者的区别主要在于tryAcquire方法，NonfairSync调用调用tryAcquire方法后，然后调用到了nonfairTryAcquire方法。下面贴出源码。

````
final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                //直接插队
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
````

````
protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                //hasQueuedPredecessors检测当前面没有排在该节点（Node）前面
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
````

 这里引用用其它文章的内容说明非公平锁的优势([ReentrantLock公平锁与非公平锁](https://blog.csdn.net/caomiao2006/article/details/53385669))。
> 在公平的锁上，线程按照他们发出请求的顺序获取锁，但在非公平锁上，则允许‘插队’：当一个线程请求非公平锁时，如果在发出请求的同时该锁变成可用状态，那么这个线程会跳过队列中所有的等待线程而获得锁。     非公平的ReentrantLock 并不提倡 插队行为，但是无法防止某个线程在合适的时候进行插队。

> 在公平的锁中，如果有另一个线程持有锁或者有其他线程在等待队列中等待这个所，那么新发出的请求的线程将被放入到队列中。而非公平锁上，只有当锁被某个线程持有时，新发出请求的线程才会被放入队列中。

> 非公平锁性能高于公平锁性能的原因：在恢复一个被挂起的线程与该线程真正运行之间存在着严重的延迟。
假设线程A持有一个锁，并且线程B请求这个锁。由于锁被A持有，因此B将被挂起。当A释放锁时，B将被唤醒，因此B会再次尝试获取这个锁。与此同时，如果线程C也请求这个锁，那么C很可能会在B被完全唤醒之前获得、使用以及释放这个锁。这样就是一种双赢的局面：B获得锁的时刻并没有推迟，C更早的获得了锁，并且吞吐量也提高了。

> 当持有锁的时间相对较长或者请求锁的平均时间间隔较长，应该使用公平锁。在这些情况下，插队带来的吞吐量提升（当锁处于可用状态时，线程却还处于被唤醒的过程中）可能不会出现。

### 总结

 要理解知识点光看别人写的文章不行，还要自己去仔细阅读分析源码才能真正掌握知识点，理解的更深刻，我也是刚刚开始看源码，边学习边理解。


# 参考
* [ReentrantLock解析](http://blog.csdn.net/yanlinwang/article/details/40450769)
* [AbstractQueuedSynchronizer源码剖析（六）- 深刻解析与模拟线程竞争资源](http://blog.csdn.net/pfnie/article/details/53191892)
* [ReentrantLock实现原理深入探究](http://www.cnblogs.com/xrq730/p/4979021.html)
* [AbstractQueuedSynchronizer的介绍和原理分析](http://ifeve.com/introduce-abstractqueuedsynchronizer/)
* [Java并发机制及锁的实现原理](http://blog.csdn.net/sunxianghuang/article/details/51932179)
* [Java锁--Lock实现原理(底层实现)](http://blog.csdn.net/Luxia_24/article/details/52403033)
* [synchronized的JVM底层实现（很详细 很底层）](http://blog.csdn.net/niuwei22007/article/details/51433669)
* [Java显式锁学习总结之六：Condition源码分析](https://www.cnblogs.com/sheeva/p/6484224.html)
* [Java AbstractQueuedSynchronizer源码阅读3-cancelAcquire()](https://www.jianshu.com/p/01f2046aab64)
* [Java线程中断的本质和编程原则](http://blog.csdn.net/dlite/article/details/4218105)
* [AQS的原理浅析](http://ifeve.com/java-special-troops-aqs/)
* [ReentrantLock公平锁与非公平锁](https://blog.csdn.net/caomiao2006/article/details/53385669)

