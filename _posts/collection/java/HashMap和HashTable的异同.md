> HashMap和HashTable都是一种以键值对存储数据的容器，An object that maps keys to values。

## 相同之处
1. 实现接口
> 都实现了Map<K,V>, Cloneable, Serializable。

2. 实现原理
> 数据结构哈希表是由数组+链表组成的。

## 不同之处
1. 出现版本

| HashTable | HashMap |
| :--- | :----: |
| JDK1.0 | JDK1.2 |

2. 作者不同

| HashTable | HashMap |
| :--- | :----: |
| Arthur van Hoff、Josh Bloch、Neal Gafter | Doug Lea、Arthur van Hoff、Josh Bloch、Neal Gafter |

3. 继承类不同

| HashTable | HashMap |
| :--- | :----: |
| Dictionary<K,V> | AbstractMap<K,V> |

4. NULL

| HashTable | HashMap |
| :--- | :----: |
| key和value都不支持null | key和value都能为null |

5. 锁

| HashTable | HashMap |
| :--- | :----: |
| synchronized | 非线程安全 |

6. 支持的遍历种类不同

| HashTable | HashMap |
| :--- | :----: |
| Enumeration、Iterator | Iterator |

7. 通过Iterator迭代器遍历时，遍历的顺序不同

| HashTable | HashMap |
| :--- | :----: |
| “从后往前”的遍历数组；再对数组具体某一项对应的链表，从表头开始进行遍历。 | “从前向后”的遍历数组；再对数组具体某一项对应的链表，从表头开始进行遍历。 |

8. 容量的初始值

| HashTable | HashMap |
| :--- | :----: |
| 11 | 16 |

9. 扩容方式

| HashTable | HashMap |
| :--- | :----: |
| “原始容量x2 + 1” | “原始容量x2” |

10. hash值算

* HashTable

````
private int hash(Object k) {
    // hashSeed will be zero if alternative hashing is disabled.
    return hashSeed ^ k.hashCode();
}
````

* HashMap

````
 final int hash(Object k) {
    int h = hashSeed;
    if (0 != h && k instanceof String) {
        return sun.misc.Hashing.stringHash32((String) k);
    }

    h ^= k.hashCode();

    // This function ensures that hashCodes that differ only by
    // constant multiples at each bit position have a bounded
    // number of collisions (approximately 8 at default load factor).
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
}
````

11. API

> 有部分API他们是不同的，需要仔细阅读对比源码查看。
