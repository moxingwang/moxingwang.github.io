## 代码一
````$xslt
package main.java.mo.multithread.volatiletest;

public class CacheVolatile {
    private  int i = 0;

    public void write() {
        i++;
    }

    public int read() {
        while (i == 0) {

        }
        return i;
    }

    public static void main(String[] args) {
        CacheVolatile cacheVolatile = new CacheVolatile();

        Thread thread = new Thread(() -> {
            cacheVolatile.read();
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10000; i++)
            cacheVolatile.write();

    }
}
````

## 代码二
````$xslt
package main.java.mo.multithread.volatiletest;

public class CacheVolatile {
    private  int i = 0;

    public void write() {
        i++;
    }

    public int read() {
        while (i == 0) {
            System.out.println(i);
        }
        return i;
    }

    public static void main(String[] args) {
        CacheVolatile cacheVolatile = new CacheVolatile();

        Thread thread = new Thread(() -> {
            cacheVolatile.read();
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10000; i++)
            cacheVolatile.write();

    }
}
````

## 代码三
````$xslt
package main.java.mo.multithread.volatiletest;

public class CacheVolatile {
    private volatile int i = 0;

    public void write() {
        i++;
    }

    public int read() {
        while (i == 0) {
            
        }
        return i;
    }

    public static void main(String[] args) {
        CacheVolatile cacheVolatile = new CacheVolatile();

        Thread thread = new Thread(() -> {
            cacheVolatile.read();
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10000; i++)
            cacheVolatile.write();

    }
}
````

## 分析
这三段代码执行结果自然各有差异，分析这三段代码可以让你理解volatile和缓存的知识点。