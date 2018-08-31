---
layout: post
title: '理解分析java集合操作之ConcurrentModificationException'
date: 2018-08-24
author: M.莫
cover: 'http://on2171g4d.bkt.clouddn.com/jekyll-banner.png'
tags: ConcurrentModificationException
---

相信不少同学在处理List的时候遇到过下面的Exception，
````
Exception in thread "main" java.util.ConcurrentModificationException
	at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:859)
	at java.util.ArrayList$Itr.next(ArrayList.java:831)
````
话不多说，接下来列举几个例子说明问题并且分析其原因。

#### 例一

````
package main.java.mo.basic;

import java.util.ArrayList;

/**
 * Created by MoXingwang on 2017/7/2.
 */
public class ConcurrentModificationExceptionTest {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("e");

        for (String string : strings) {
            if ("e".equals(string)) {
                strings.remove(string);
            }
        }
    }
}
````

* 执行结果

````
Exception in thread "main" java.util.ConcurrentModificationException
	at java.util.ArrayList$Itr.checkForComodification(ArrayList.java:859)
	at java.util.ArrayList$Itr.next(ArrayList.java:831)
	at main.java.mo.basic.ConcurrentModificationExceptionTest.main(ConcurrentModificationExceptionTest.java:17)
````

* 分析原因

首先我们知道增强for循环其实现原理就是Iterator接口，这一点非常重要，有了个这个知识点
我们才能分析为什么会出现异常，这个知识点也是最重要最核心的。

根据上面的异常信息可以看出，异常是从"for (String string : strings) {"，这一行抛
出的，这一行怎么会出错呢？理解增强for的实现原理了，我们就会知道，
执行这一行代码的时候会调用Iterator实现类的两个方法，hasNext()和next(),所以说这个知识点是最重要最核心的。

先看ArrayList.Iterator的部分源码,以及ArrayList.remove(Object o)的部分源码
````
int cursor;       // index of next element to return
int lastRet = -1; // index of last element returned; -1 if no such
int expectedModCount = modCount;

public boolean hasNext() {
    return cursor != size;
}

@SuppressWarnings("unchecked")
public E next() {
    checkForComodification();
    int i = cursor;
    if (i >= size)
        throw new NoSuchElementException();
    Object[] elementData = ArrayList.this.elementData;
    if (i >= elementData.length)
        throw new ConcurrentModificationException();
    cursor = i + 1;
    return (E) elementData[lastRet = i];
}
...
final void checkForComodification() {
 if (expectedModCount != ArrayList.this.modCount)
     throw new ConcurrentModificationException();
}

````
````
public boolean remove(Object o) {
    if (o == null) {
        for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
    } else {
        for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
    }
    return false;
}

/*
 * Private remove method that skips bounds checking and does not
 * return the value removed.
 */
private void fastRemove(int index) {
    modCount++;
    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
    elementData[--size] = null; // clear to let GC do its work
}
````
我们会发现当执行remove(Object o)方法后，ArrayList对象的size减一此时size==4,
modCount++了，然后Iterator对象中的cursor==5，hasNext发回了true，导致增强for循
环去寻找下一个元素调用next()方法，checkForComodification做校验的时候，发现modCount
已经和Iterator对象中的expectedModCount不一致，说明ArrayList对象已经被修改过，
为了防止错误，抛出异常ConcurrentModificationException。

回过头来，再一思考ArrayList的代码，让我们来看看ArrayList本身和内部类Itr，Itr
implements Iterator是为了返回给ArrayList.iterator()，在使用的时候可以说他们是
独立的两个类，其中各自有两个重要的属性;ArrayList中的size、modCount；以及Itr中的
cursor、expectedModCount，理论上他们是同步的，但是我们在某些操作的过程中导致会导致
他们不一致，比如说在这个例子中，我们调用的是ArrayList.remove()方法，修改了size和
modCount属性，但是Itr中的这cursor、expectedModCount却没有发生变化，当增强for
循环再次执行的时候，调用的却是Itr中的方法，最终发现了数据不一致。这就是本例ConcurrentModificationException
产生的根本原因。


既然问题我们分析清楚了，如何解决呢？这里我们顺着这个思路倒推，列出集中解决办法。

* 解决问题
   + 不使用增强for循环

   对于这个例子，很明显我们知道异常的产生原因是由于ArrayList中的属性和内部类Itr中的
   属性不一致导致的，那么可以假设在for循环和remove操作的时候不设计到Itr类不就得了。
   是的，思路很清晰，就这么简单。啥都不说先上代码。
   ````
   ArrayList<String> strings = new ArrayList<String>();
   strings.add("a");
   strings.add("b");
   strings.add("c");
   strings.add("d");
   strings.add("e");

   for (int i = 0; i < strings.size(); i++) {
       String element = strings.get(i);
       if("e".equals(element)){
           strings.remove(element);
           i --;//需要自己手动维护索引
       }
   }
   ````
   使用这种方式处理remove操作，比较尴尬的一点是需要自己手动维护索引，避免漏掉数据。

   + 使用Iterator中的remove方法，不要和ArrayList中的remove方法混着搞

   基于上面的思路，既然不想和Itr有来望，好吧，看来直接使用Itr类中的remove方法，
   使用Itr遍历对象不也是一个好的想法么。上代码。
   ````
   ArrayList<String> strings = new ArrayList<String>();
   strings.add("a");
   strings.add("b");
   strings.add("c");
   strings.add("d");
   strings.add("e");

   Iterator<String> iterator = strings.iterator();
   while (iterator.hasNext()){
       String element = iterator.next();
       if("e".equals(element)){
           iterator.remove();
       }
   }
   ````

   + 删除元素的时候不再遍历了，直接removeAll
   既然异常是对list做遍历和remove操作的时候出现的，好吧，暴力点，我能不遍历的时候做remove操作吗？
   好吧，思路正确，满足你。
   ````
   ArrayList<String> strings = new ArrayList<String>();
   strings.add("a");
   strings.add("b");
   strings.add("c");
   strings.add("d");
   strings.add("e");

   ArrayList<String> tempStrings = new ArrayList<String>();
   for (String string : strings) {
       if("e".equals(string)){
           tempStrings.add(string);
       }
   }
   strings.removeAll(tempStrings);
   ````

   + 其它方法
   思路总是多的，比如说加个锁保证数据正确，什么去掉这么到校验自己实现个ArrayList，
   怎么地都行，你想怎么玩就怎么玩，方便点的话直接使用java.util.concurrent包下面的CopyOnWriteArrayList。
   方法很多，怎么开心就好。

#### 例二
说完例一说例二，刚刚是ArrayList，现在试试LinkedList。
````
package main.java.mo.basic;

import java.util.LinkedList;

/**
 * Created by MoXingwang on 2017/7/2.
 */
public class ConcurrentModificationExceptionTest {
    public static void main(String[] args) {
        LinkedList<String> strings = new LinkedList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("e");

        for (String string : strings) {
            if ("e".equals(string)) {
                strings.remove(string);
            }
        }
    }
}
````

这段代码和例一的没啥区别，唯一不同的就是ArrayList换成了LinkedList，突然发现执行这段代码怎么就不报错了呢。
这不是搞事情么？好吧，再上一段代码。

````
package main.java.mo.basic;

import java.util.LinkedList;

/**
 * Created by MoXingwang on 2017/7/2.
 */
public class ConcurrentModificationExceptionTest {
    public static void main(String[] args) {
        LinkedList<String> strings = new LinkedList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("e");
        strings.add("f");
        strings.add("g");

        for (String string : strings) {
            if ("e".equals(string)) {
                strings.remove(string);
            }
        }
    }
}
````

再执行一下这一段代码，返回结果居然是这样：
````
Exception in thread "main" java.util.ConcurrentModificationException
	at java.util.LinkedList$ListItr.checkForComodification(LinkedList.java:953)
	at java.util.LinkedList$ListItr.next(LinkedList.java:886)
	at main.java.mo.basic.ConcurrentModificationExceptionTest.main(ConcurrentModificationExceptionTest.java:19)
````

仔细一看才发现strings里面多了两个元素，怎么差别就这么大呢，分析方法和例一完全一样，
想必按照例子一的分析一定非常简单的找到答案，这就就不举例子了。

#### 总结
总得来说，本文虽让没有对ConcurrentModificationException发生的情况深入涉及，
但是理解方法和思路都是一样的，文章中的两个例子告诉我们，
当在处理Iterable的实现类做元素remove操作，并且是在for循环中处理的时候，
理解了这些东西就会避免掉bug以及出现错误。