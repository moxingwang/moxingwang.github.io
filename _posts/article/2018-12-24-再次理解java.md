# 理解与学习的思路
> 不断学习和理解，反复提问为什么，仔细推敲和琢磨，多思考。

# 计算机

## 硬件

### CPU
### 内存
### 总线
### 存储
### 冯洛伊曼体系
### 操作系统执行原理
> 程序的执行过程实际上是不断地取出指令、分析指令、执行指令的过程。几乎所有的冯•诺伊曼型计算机的CPU，其工作都可以分为5个阶段：取指令、指令译码、执行指令、访存取数和结果写回。

## 软件

### 汇编
### c语言
### Java
### 面向过程编程
### 面向对象编程
### 函数式编程

# Java

## java基础

* Java发展历史
![image](https://github.com/moxingwang/resource/blob/master/image/java_history.png)
* 特点
* jdk
* java Api
* jvm
* jre
* 工作流程原理简单介绍
> 我们写的java程序是遵循java语言规范的，是面向java开发者能够读的懂的语言。计算机不能够直接读懂我们写的java文件代码。
我们想要让cpu做事情，让它处理运算，而cup只能够读懂特殊的指令。我们写的java程序代码首先被编译成class文件，
能够让jvm读懂，再由ClassLoader把这些class文件加载到jvm运行时的数据区域，并最终由jvm翻译、调用c/c++执行。

![image](https://github.com/moxingwang/resource/blob/master/image/java%E6%96%87%E4%BB%B6%E7%BC%96%E8%AF%91%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E7%AE%80%E5%8D%95%E5%9B%BE.png?raw=true)

参考
[Java详解之——Javac 编译原理](http://blog.csdn.net/qq756161569/article/details/50486946)

## java技术体系
![image](https://github.com/moxingwang/resource/blob/master/image/java%E6%8A%80%E6%9C%AF%E4%BD%93%E7%B3%BB%E5%9B%BE.png)

## java文件编译

![image](http://images2015.cnblogs.com/blog/866881/201602/866881-20160216214708767-653617912.jpg)

参考
[Javac编译原理](http://www.cnblogs.com/java-zhao/p/5194064.html)

## jvm启动过程概述

![image](http://images0.cnblogs.com/blog/641601/201508/211701559568932.png)

参考
[Java虚拟机详解02----JVM内存结构](http://www.cnblogs.com/smyhvae/p/4748392.html)
[JVM启动过程——JVM之一](http://www.cnblogs.com/muffe/p/3540001.html)

## jvm架构
jvm就是jvm规范的一个实例，可用使用多种语言实现jvm虚拟机。hostspot 是stack-based architecture；

![image](http://static.codeceo.com/images/2016/10/JVM-Architecture.png)

参考
[JVM 架构解读](http://www.codeceo.com/article/jvm-architecture-explained.html)
[The JVM Architecture Explained](https://dzone.com/articles/jvm-architecture-explained)

JVM分为三个主要子系统,分别是
* 类加载子系统
* 运行时数据区
* 执行引擎

### 类加载子系统

Java的动态类加载功能由类加载器子系统处理。它在运行时首次引用类的时候加载、链接、并初始化类文件。

#### 加载

类将通过这些组件加载。Boot Strap Class Loader，Extension Class Loader和Application Class Loader是有助于实现的三个类加载器。
1. Boot Strap Class Loader——负责加载来自于Bootstrap类路径的类，就是rt.jar。此加载程序将给予最高优先级。
2. Extension Class Loader——负责加载在ext文件夹（jre \ lib）内的类。
3. Application Class Loader——负责加载应用程序级类路径，路径提到环境变量等

上面的类记载器在加载类文件时遵循Delegation Hierarchy 算法。

#### 链接
1. 验证——字节码验证器将验证生成的字节码是否正确，如果验证失败，我们将得到verification error。
2. 准备——对于所有的静态变量，内存将被分配和配置默认值。
3. 解决——所有的符号存储器引用都将替换为来自Method Area的原始引用。

#### 初始化

这是类加载的最后阶段，这里所有的静态变量都将被赋予原始值，并执行静态块。





### 运行时数据区（jvm内存模型）

运行时数据区分为5个主要组件。
![image](https://github.com/moxingwang/resource/blob/master/image/jvm%20data%20areas%20structure.png)

#### the pc register
> 每个线程都有单独的PC寄存器，用于保存当前执行指令的地址，一旦指令执行，PC寄存器将更新到下一条指令。
程序计数器（Program Counter Register）是一块较小的内存空间，
它的作用可以看做是当前线程所执行的字节码的行号指示器。
在虚拟机的概念模型里（仅是概念模型，各种虚拟机可能会通过一些更高效的方式去实现），
字节码解释器工作时就是通过改变这个计数器的值来选取下一条需要执行的字节码指令，分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖这个计数器来完成。 

> 由于Java虚拟机的多线程是通过线程轮流切换并分配处理器执行时间的方式来实现的，在任何一个确定的时刻，一个处理器（对于多核处理器来说是一个内核）只会执行一条线程中的指令。
因此，为了线程切换后能恢复到正确的执行位置，每条线程都需要有一个独立的程序计数器，各条线程之间的计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存。 
                                                      
> 如果线程正在执行的是一个Java方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是Natvie方法，这个计数器值则为空（Undefined）。
此内存区域是唯一一个在Java虚拟机规范中没有规定任何OutOfMemoryError情况的区域。

为什么要有pc register，并且是私有的？
> 当在执行多线程时候，CPU会不停的切换任务(对于多核来说是一个内核)，本质上在一个确定的时间点，只会执行某一个线程的指令。 
那么这时候为了能够准确的记录各个线程正在执行的当前字节码指令的地址，最后的办法就是为每一个线程一人分配一个计数器， 
这样一来各个线程独立计算互不干扰，虽然理论上浪费了些空间，但问题则变得简单多了。

* java virtual machine stacks
> 首先我要知道jvm虚拟机的实现大概有两种，一种是基于栈的架构（stack-based 不同），另一种是基于寄存器的架构（register-based）。
sun jvm是基于栈架构的实现。

> 在jvm的内存模型里面，栈、堆...各司其职,概括的来说栈内存用来存储局部变量和方法调用。栈中存放了多个栈帧。
与程序计数器一样，Java 虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。
虚拟机栈描述的是Java方法执行的内存模型：每个方法被执行的时候都会同时创建一个栈帧（Stack Frame①）用于存储局部变量表、操作栈、动态链接、方法出口等信息。
每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程。

##### 栈帧
> 栈帧(Stack Frame)是用于支持虚拟机进行方法调用和方法执行的数据结构，它是虚拟机运行时数据区的虚拟机栈(Virtual Machine Stack)的栈元素。栈帧存储了方法的局部变量表，操作数栈，动态连接和方法返回地址等信息。第一个方法从调用开始到执行完成，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程。
   每一个栈帧都包括了局部变量表，操作数栈，动态连接，方法返回地址和一些额外的附加信息。在编译代码的时候，栈帧中需要多大的局部变量表，多深的操作数栈都已经完全确定了，并且写入到了方法表的Code属性中，因此一个栈帧需要分配多少内存，不会受到程序运行期变量数据的影响，而仅仅取决于具体虚拟机的实现。
   一个线程中的方法调用链可能会很长，很多方法都同时处理执行状态。对于执行引擎来讲，活动线程中，只有虚拟机栈顶的栈帧才是有效的，称为当前栈帧(Current Stack Frame)，这个栈帧所关联的方法称为当前方法(Current Method)。执行引用所运行的所有字节码指令都只针对当前栈帧进行操作。
![image](https://github.com/moxingwang/resource/blob/master/image/20141214124019390.png)

> 1.局部变量表
     局部变量表是一组变量值存储空间，用于存放方法参数和方法内部定义的局部变量。在Java程序编译为Class文件时，就在方法表的Code属性的max_locals数据项中确定了该方法需要分配的最大局部变量表的容量。
     在方法执行时，虚拟机是使用局部变量表完成参数变量列表的传递过程，如果是实例方法，那么局部变量表中的每0位索引的Slot默认是用于传递方法所属对象实例的引用，在方法中可以通过关键字“this”来访问这个隐含的参数，其余参数则按照参数列表的顺序来排列，占用从1开始的局部变量Slot，参数表分配完毕后，再根据方法体内部定义的变量顺序和作用域来分配其余的Slot。局部变量表中的Slot是可重用的，方法体中定义的变量，其作用域并不一定会覆盖整个方法，如果当前字节码PC计算器的值已经超出了某个变量的作用域，那么这个变量对应的Slot就可以交给其它变量使用。
     局部变量不像前面介绍的类变量那样存在“准备阶段”。类变量有两次赋初始值的过程，一次在准备阶段，赋予系统初始值；另外一次在初始化阶段，赋予程序员定义的值。因此即使在初始化阶段程序员没有为类变量赋值也没有关系，类变量仍然具有一个确定的初始值。但局部变量就不一样了，如果一个局部变量定义了但没有赋初始值是不能使用的。
  
>  2.操作数栈
     操作数栈也常被称为操作栈，它是一个后入先出栈。同局部变量表一样，操作数栈的最大深度也是编译的时候被写入到方法表的Code属性的max_stacks数据项中。操作数栈的每一个元素可以是任意Java数据类型，包括long和double。32位数据类型所占的栈容量为1，64位数据类型所占的栈容量为2。栈容量的单位为“字宽”，对于32位虚拟机来说，一个”字宽“占4个字节，对于64位虚拟机来说，一个”字宽“占8个字节。
     当一个方法刚刚执行的时候，这个方法的操作数栈是空的，在方法执行的过程中，会有各种字节码指向操作数栈中写入和提取值，也就是入栈与出栈操作。例如，在做算术运算的时候就是通过操作数栈来进行的，又或者调用其它方法的时候是通过操作数栈来行参数传递的。
     另外，在概念模型中，两个栈帧作为虚拟机栈的元素，相互之间是完全独立的，但是大多数虚拟机的实现里都会作一些优化处理，令两个栈帧出现一部分重叠。让下栈帧的部分操作数栈与上面栈帧的部分局部变量表重叠在一起，这样在进行方法调用返回时就可以共用一部分数据，而无须进行额外的参数复制传递了，重叠过程如下图：
![image](https://github.com/moxingwang/resource/blob/master/image/20141214124042156.png)

> 3.动态连接
    每个栈帧都包含一个指向运行时常量池中该栈帧所属性方法的引用，持有这个引用是为了支持方法调用过程中的动态连接。在Class文件的常量池中存有大量的符号引用，字节码中的方法调用指令就以常量池中指向方法的符号引用为参数。这些符号引用一部分会在类加载阶段或第一次使用的时候转化为直接引用，这种转化称为静态解析。另外一部分将在每一次的运行期期间转化为直接引用，这部分称为动态连接。
  
>  4.方法返回地址
     当一个方法被执行后，有两种方式退出这个方法。第一种方式是执行引擎遇到任意一个方法返回的字节码指令，这时候可能会有返回值传递给上层的方法调用者(调用当前方法的的方法称为调用者)，是否有返回值和返回值的类型将根据遇到何种方法返回指令来决定，这种退出方法方式称为正常完成出口(Normal Method Invocation Completion)。
     另外一种退出方式是，在方法执行过程中遇到了异常，并且这个异常没有在方法体内得到处理，无论是Java虚拟机内部产生的异常，还是代码中使用athrow字节码指令产生的异常，只要在本方法的异常表中没有搜索到匹配的异常处理器，就会导致方法退出，这种退出方式称为异常完成出口(Abrupt Method Invocation Completion)。一个方法使用异常完成出口的方式退出，是不会给它的调用都产生任何返回值的。
     无论采用何种方式退出，在方法退出之前，都需要返回到方法被调用的位置，程序才能继续执行，方法返回时可能需要在栈帧中保存一些信息，用来帮助恢复它的上层方法的执行状态。一般来说，方法正常退出时，调用者PC计数器的值就可以作为返回地址，栈帧中很可能会保存这个计数器值。而方法异常退出时，返回地址是要通过异常处理器来确定的，栈帧中一般不会保存这部分信息。
     方法退出的过程实际上等同于把当前栈帧出栈，因此退出时可能执行的操作有：恢复上层方法的局部变量表和操作数栈，把返回值(如果有的话)压入调用都栈帧的操作数栈中，调用PC计数器的值以指向方法调用指令后面的一条指令等.
  
> 5.附加信息 虚拟机规范允许具体的虚拟机实现增加一些规范里没有描述的信息到栈帧中，例如与高度相关的信息，这部分信息完全取决于具体的虚拟机实现。在实际开发中，一般会把动态连接，方法返回地址与其它附加信息全部归为一类，称为栈帧信息。

参考
[深入理解Java虚拟机笔记---运行时栈帧结构](http://blog.csdn.net/xtayfjpk/article/details/41924283)

#### heap
> 对于大多数应用来说，Java 堆（java Heap）是Java虚拟机所管理的内存中最大的一块。Java 堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例都在这里分配内存。这一点在Java虚拟机规范中的描述是：所有的对象实例以及数组都要在堆上分配①，但是随着JIT 编译器的发展与逃逸分析技术的逐渐成熟，栈上分配、标量替换②优化技术将会导致一些微妙的变化发生，所有的对象都分配在堆上也渐渐变得不是那么“绝对”了。
          Java 堆是垃圾收集器管理的主要区域，因此很多时候也被称做“GC堆”（Garbage Collected Heap，幸好国内没翻译成“垃圾堆”）。如果从内存回收的角度看，由于现在收集器基本都是采用的分代收集算法，所以Java堆中还可以细分为：新生代和老年代；再细致一点的有Eden 空间、From Survivor空间、To Survivor 空间等。如果从内存分配的角度看，线程共享的Java堆中可能划分出多个线程私有的分配缓冲区（Thread Local Allocation Buffer，TLAB）。不过，无论如何划分，都与存放内容无关，无论哪个区域，存储的都仍然是对象实例，进一步划分的目的是为了更好地回收内存，或者更快地分配内存。在本章中，我们仅仅针对内存区域的作用进行讨论，Java堆中的上述各个区域的分配和回收等细节将会是下一章的主题。
           根据Java 虚拟机规范的规定，Java堆可以处于物理上不连续的内存空间中，只要逻辑上是连续的即可，就像我们的磁盘空间一样。在实现时，既可以实现成固定大小
  的，也可以是可扩展的，不过当前主流的虚拟机都是按照可扩展来实现的（通过-Xmx和-Xms控制）。如果在堆中没有内存完成实例分配，并且堆也无法再扩展时，将会抛出
  OutOfMemoryError 异常。

#### method area
> 方法区（Method Area）与Java堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做Non-Heap（非堆），目的应该是与Java堆区分开来。
          对于习惯在HotSpot 虚拟机上开发和部署程序的开发者来说，很多人愿意把方法区称为“永久代”（Permanent Generation），本质上两者并不等价，仅仅是因为HotSpot虚
  拟机的设计团队选择把GC 分代收集扩展至方法区，或者说使用永久代来实现方法区而已。对于其他虚拟机（如BEA JRockit、IBM J9等）来说是不存在永久代的概念的。即使是HotSpot 虚拟机本身，根据官方发布的路线图信息，现在也有放弃永久代并“搬家”至Native Memory来实现方法区的规划了。
          Java 虚拟机规范对这个区域的限制非常宽松，除了和Java堆一样不需要连续的内存和可以选择固定大小或者可扩展外，还可以选择不实现垃圾收集。相对而言，垃圾收集行为在这个区域是比较少出现的，但并非数据进入了方法区就如永久代的名字一样“永久”存在了。这个区域的内存回收目标主要是针对常量池的回收和对类型的卸载，一般来说这个区域的回收“成绩”比较难以令人满意，尤其是类型的卸载，条件相当苛刻，但是这部分区域的回收确实是有必要的。在Sun公司的BUG 列表中，曾出现过的若干个严重的BUG就是由于低版本的HotSpot 虚拟机对此区域未完全回收而导致内存泄漏。
          根据Java 虚拟机规范的规定，当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常。
  
#### run-time constant pool
> 运行时常量池（Runtime Constant Pool）是方法区的一部分。Class文件中除了有类的版本、字段、方法、接口等描述等信息外，还有一项信息是常量池（Constant Pool
  Table），用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加载后存放到方法区的运行时常量池中。
           Java 虚拟机对Class文件的每一部分（自然也包括常量池）的格式都有严格的规定，每一个字节用于存储哪种数据都必须符合规范上的要求，这样才会被虚拟机认可、装载和执行。但对于运行时常量池，Java虚拟机规范没有做任何细节的要求，不同的提供商实现的虚拟机可以按照自己的需要来实现这个内存区域。不过，一般来说，除了保存Class文件中描述的符号引用外，还会把翻译出来的直接引用也存储在运行时常量池中。运行时常量池相对于Class文件常量池的另外一个重要特征是具备动态性，Java 语言并不要求常量一定只能在编译期产生，也就是并非预置入Class文件中常量池的内容才能进入方法区运行时常量池，运行期间也可能将新的常量放入池中，这种特性被开发人员利用得比较多的便是String类的intern() 方法。
          既然运行时常量池是方法区的一部分，自然会受到方法区内存的限制，当常量池无法再申请到内存时会抛出OutOfMemoryError异常

#### native method stacks
> 本地方法栈（Native Method Stacks）与虚拟机栈所发挥的作用是非常相似的，其区别不过是虚拟机栈为虚拟机执行Java方法（也就是字节码）服务，而本地方法栈则是为虚拟机使用到的Native 方法服务。虚拟机规范中对本地方法栈中的方法使用的语言、使用方式与数据结构并没有强制规定，因此具体的虚拟机可以自由实现它。甚至有的虚拟机（譬如Sun HotSpot虚拟机）直接就把本地方法栈和虚拟机栈合二为一。与虚拟机栈一样，本地方法栈区域也会抛出StackOverflowError和OutOfMemoryError异常。

### volatile内存语义
    
### synchronized
#### 作用域
* 对于普通同步方法，锁是当前实力对象。
* 对于静态同步方法，锁是当前类的class对象。
* 对于同步方法块，锁是synchronized括号里配置的对象。

参考
[Thread Synchronization](http://www.artima.com/insidejvm/ed2/threadsynchP.html)
[深入理解Java并发之synchronized实现原理](http://blog.csdn.net/javazejian/article/details/72828483#理解java对象头与monitor)

#### 实现原理
* 代码块的实现基于Monitor对象，monitorenter和monitorexit配合使用。

* 同步方法锁信息放在对象的对象头里面


### 指令重排
> 计算机在执行程序时，为了提高性能，编译器和处理器的常常会对指令做重排，一般分以下3种编译器优化的重排

* 编译器优化的重排
> 编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序。

* 指令并行的重排
> 现代处理器采用了指令级并行技术来将多条指令重叠执行。如果不存在数据依赖性(即后一个执行的语句无需依赖前面执行的语句的结果)，处理器可以改变语句对应的机器指令的执行顺序

* 内存系统的重排
> 由于处理器使用缓存和读写缓存冲区，这使得加载(load)和存储(store)操作看上去可能是在乱序执行，因为三级缓存的存在，导致内存与缓存的数据同步存在时间差。

> 其中编译器优化的重排属于编译期重排，指令并行的重排和内存系统的重排属于处理器重排，在多线程环境中，这些重排优化可能会导致程序出现内存可见性问题，下面分别阐明这两种重排优化可能带来的问题


### hapen
> happens-before 原则来辅助保证程序执行的原子性、可见性以及有序性的问题，它是判断数据是否存在竞争、线程是否安全的依据，happens-before 原则内容如下

* 程序顺序原则，即在一个线程内必须保证语义串行性，也就是说按照代码顺序执行。

* 锁规则 解锁(unlock)操作必然发生在后续的同一个锁的加锁(lock)之前，也就是说，如果对于一个锁解锁后，再加锁，那么加锁的动作必须在解锁动作之后(同一个锁)。

* volatile规则 volatile变量的写，先发生于读，这保证了volatile变量的可见性，简单的理解就是，volatile变量在每次被线程访问时，都强迫从主内存中读该变量的值，而当该变量发生变化时，又会强迫将最新的值刷新到主内存，任何时刻，不同的线程总是能够看到该变量的最新值。

* 线程启动规则 线程的start()方法先于它的每一个动作，即如果线程A在执行线程B的start方法之前修改了共享变量的值，那么当线程B执行start方法时，线程A对共享变量的修改对线程B可见

* 传递性 A先于B ，B先于C 那么A必然先于C

* 线程终止规则 线程的所有操作先于线程的终结，Thread.join()方法的作用是等待当前执行的线程终止。假设在线程B终止之前，修改了共享变量，线程A从线程B的join方法成功返回后，线程B对共享变量的修改将对线程A可见。

* 线程中断规则 对线程 interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生，可以通过Thread.interrupted()方法检测线程是否中断。

* 对象终结规则 对象的构造函数执行，结束先于finalize()方法



#### 对象的创建过程
![image](https://github.com/moxingwang/resource/blob/master/image/%E5%AF%B9%E8%B1%A1%E5%88%9B%E5%BB%BA%E5%86%85%E5%AD%98%E5%88%86%E9%85%8D%E8%BF%87%E7%A8%8B.png)

#### 对象内存布局

#### 对象访问定位

### jvm执行引擎

参考
[深入JVM字节码执行引擎](http://blog.csdn.net/dd864140130/article/details/49515403)
[java虚拟机字节码执行引擎浅析](http://blog.csdn.net/chdjj/article/details/23468761)


### jvm GC

#### 哪些内存需要回收

#### 如何回收

#### 垃圾回收算法

#### 垃圾收集器

#### jvm虚拟机和OS CPU

#### 线程通信

#### jvm 锁机制

参考
[深入JVM锁机制1-synchronized](http://blog.csdn.net/chen77716/article/details/6618779)
[synchronized、锁、多线程同步的原理是咋样的](http://www.jianshu.com/p/5dbb07c8d5d5)
[CPU并发特性CAS、Volatile](http://blog.sina.com.cn/s/blog_ee34aa660102wsuv.html)
[缓存一致性（Cache Coherency）入门](http://www.infoq.com/cn/articles/cache-coherency-primer/)
[原子操作和竞争](http://www.infoq.com/cn/articles/atomic-operations-and-contention)
[单核,多核CPU的原子操作](https://my.oschina.net/jcseg/blog/316726)
[关于单CPU，多CPU上的原子操作](https://software.intel.com/zh-cn/blogs/2010/01/14/cpucpu)

### jvm对象生命周期

参考
[解读JVM对象生命周期](http://developer.51cto.com/art/201009/227897_all.htm)

# 参考资料
* [深入探讨 java.lang.ref 包](https://www.ibm.com/developerworks/cn/java/j-lo-langref/)
* [JVM源码分析之FinalReference完全解读](http://www.infoq.com/cn/articles/jvm-source-code-analysis-finalreference)
* [JVM结构、GC工作机制详解](http://www.jianshu.com/p/a94912709e29)
* [JAVA GC 原理详解](https://segmentfault.com/a/1190000008384410)
* [Java垃圾回收基础的系列文章](http://youli9056.github.io/blog/java-garbage-collection-introduction/)
* [【深入理解Java虚拟机-0】思维导图汇总](http://hippo-jessy.com/2017/02/03/%E3%80%90%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3Java%E8%99%9A%E6%8B%9F%E6%9C%BA-0%E3%80%91%E6%80%9D%E7%BB%B4%E5%AF%BC%E5%9B%BE%E6%B1%87%E6%80%BB/)
* [深入理解JVM读书笔记思维导图，深入理解jvm读书笔记](http://www.bkjia.com/Javabc/861553.html)
* [Java 代码编译和执行的整个过程](http://wiki.jikexueyuan.com/project/java-vm/java-debug.html)
* [Java 虚拟机规范](http://files.cnblogs.com/files/zhuYears/Java%E8%99%9A%E6%8B%9F%E6%9C%BA%E8%A7%84%E8%8C%83%EF%BC%88JavaSE7%EF%BC%89.pdf)
* [Java Memory Model](http://tutorials.jenkov.com/java-concurrency/java-memory-model.html)
* [Java Virtual Machine Specification 官方文档](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html)
* [Is stack in CPU or RAM?](https://stackoverflow.com/questions/15433390/is-stack-in-cpu-or-ram)
* [JVM memory model](http://coding-geek.com/jvm-memory-model/)
* [《Java虚拟机原理图解》5. JVM类加载器机制与类加载过程](http://blog.csdn.net/luanlouis/article/details/50529868)
* [JVM体系结构与工作方式概览](https://segmentfault.com/a/1190000006914597?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io)
* [ JVM 指令集与 X86 等真实cpu指令集的异同](http://blog.csdn.net/zhaoyw2008/article/details/9321313)
* [ Java 虚拟机内存模型 与 cpu类比](http://blog.csdn.net/zhaoyw2008/article/details/9316189)
* [虚拟机随谈（一）：解释器，树遍历解释器，基于栈与基于寄存器，大杂烩（牛逼）](http://rednaxelafx.iteye.com/blog/492667)
* [Dalvik 虚拟机和 Sun JVM 在架构和执行方面有什么本质区别？](https://www.zhihu.com/question/20207106)
* [Java工程师要懂的硬件知识-前言](http://www.voidcn.com/blog/xiaoyufu007/article/p-6013180.html)
* [Linux与JVM的内存关系分析](http://www.voidcn.com/blog/xiaoyufu007/article/p-6013180.html)
* [扒一扒ReentrantLock以及AQS实现原理](https://my.oschina.net/andylucc/blog/651982)
* [全面理解Java内存模型(JMM)及volatile关键字](http://blog.csdn.net/javazejian/article/details/72772461)
