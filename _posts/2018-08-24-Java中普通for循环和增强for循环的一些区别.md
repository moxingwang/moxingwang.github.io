## Java中for的几种常见形式
1. For loop using index.
````aidl
for (int i = 0; i < arr.length; i++) { 
    type var = arr[i];
    body-of-loop
}
````
2. Loop using explicit iterator.
````aidl
for (Iterator<type> iter = coll.iterator(); iter.hasNext(); ) {
    type var = iter.next();
    body-of-loop
}
````
3. Foreach loop over all elements in arr.
````aidl
for (type var : coll) {
    body-of-loop
}
````

## For循环用来处理哪些数据结构
1. 数组

````aidl
int[] a = {1,2,3,4,5,6};
int[] b = new int[]{1,2,3,4,5,6};
int[] c = new int[6];

for (int i = 0; i < a.length; i++) {
    System.out.println(i);
}
for (int i : a) {
    System.out.println(i);
}
````

2. 实现了java.util.Iterator的类

````aidl
import java.util.Iterator;

/**
 * Created by MoXingwang on 2017/6/30.
 */
public class IterableTest<E> implements Iterable<E> {

    public static void main(String[] args) {
        IterableTest<Integer> integers = new IterableTest<Integer>();
        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator() {

            @Override
            public boolean hasNext() {
                //...
                return false;
            }

            @Override
            public Object next() {
                //...
                return null;
            }

            @Override
            public void remove() {
                //...
            }
        };
    }
}
````

## 普通for遍历和增强for的一些区别
增强的for循环的底层使用迭代器来实现，所以它就与普通for循环有一些差异
1. 增强for使用增强for循环的时候不能使用集合删除集合中的元素；
2. 增强for循环不能使用迭代器中的方法，例如remove()方法删除元素；
3. 与普通for循环的区别：增强For循环有遍历对象，普通for循环没有遍历对象;

对于实现了RandomAccess接口的集合类，推荐使用普通for，这种方式faster than Iterator.next
> The RandomAccess interface identifies that a particular java.util.List implementation has fast random access. (A more accurate name for the interface would have been "FastRandomAccess.") This interface tries to define an imprecise concept: what exactly is fast? The documentation provides a simple guide: if repeated access using the List.get( ) method is faster than repeated access using the Iterator.next( ) method, then the List has fast random access. The two types of access are shown in the following code examples.
  

## 参考资料
* [The For-Each Loop](https://docs.oracle.com/javase/1.5.0/docs/guide/language/foreach.html)
* [The RandomAccess Interface](http://etutorials.org/Programming/Java+performance+tuning/Chapter+11.+Appropriate+Data+Structures+and+Algorithms/11.6+The+RandomAccess+Interface/)
* [增强for循环](http://www.jianshu.com/p/47e2ed0ad6be)
* [For-each Loop](http://www.fredosaurus.com/notes-java/flow/loops/foreach.html)