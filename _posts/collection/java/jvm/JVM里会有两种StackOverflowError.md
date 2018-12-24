JVM里会有两种StackOverflowError, 一种是对应JVM stack, 一种是对应Native Method stack. 我们一般说的都是JVM stack.
每一个JVM线程维护自己的JVM stack. JVM stack里面存放 JVM栈帧. 栈帧中存放 数据和中间结果(本地变量数组, 操作符栈, 和对runtime 常量池的引用). 这些数据都比较小(对象都在堆中, 栈帧仅存放对象引用), 所以想单纯通过 在栈帧中存放大数据的方法 去引入StackOverflowError, 基本是不现实的.一般都是因为方法调用嵌套层数过大.
http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.5.2

JVM stack的大小是可以调节的, sun的windows jvm6 x64,jvm栈默认大小为1024k.可以通过-Xss1024k来调节.
http://www.oracle.com/technetwork/java/hotspotfaq-138619.html#threads_oom

````aidl
private int aa=0;
private void aaa(){
    System.out.println(aa++);
    aaa();
}

@Test
public void test3243(){
    aaa();
}

````

````aidl
-server -Xss128k
输出946时溢出java栈

-server -Xss512k
输出5418时溢出java栈

-server -Xss1280k
输出14363时溢出java栈

-server -Xss12800k
输出252223时溢出java栈

````