`zookeeper`知识详细总结


#### 集群任务

    ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/workers.png?raw=true)


### 主从架构

    ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/worker-master-slae.png?raw=true)

    > 在分布式系统设计中一个得到广泛应用的架构：一个主-从（master-worker）架构,该系统中遵循这个架构的一个重要例子是HBase——一个Google的数据存储系统（BigTable）模型的实现，在最高层，主节点服务器（HMaster）负责跟踪区域服务器（HRegionServer）是否可用，并分派区域到服务器。

    * master-worker模式面临的问题
        * 主节点崩溃
            > 如果主节点发送错误并失效，系统将无法分配新的任务或重新分配已失败的任务。这就需要重选备份主节点接管主要主节点的角色，进行故障转移,数据恢复等等，更糟的是，如果一些从节点无法与主要主节点通信，如由于网络分区（network partition）错误导致，这些从节点可能会停止与主要主节点的通信，而与第二个主要主节点建立主-从关系。针对这个场景中导致的问题，我们一般称之为脑裂（split-brain）：系统中两个或者多个部分开始独立工作，导致整体行为不一致性。我们需要找出一种方法来处理主节点失效的情况，关键是我们需要避免发生脑裂的情况。
        * 从节点崩溃
            > 如果从节点崩溃，已分配的任务将无法完成。如果从节点崩溃了，所有已派发给这个从节点且尚未完成的任务需要重新派发。其中首要需求是让主节点具有检测从节点的崩溃的能力。主节点必须能够检测到从节点的崩溃，并确定哪些从节点是否有效以便派发崩溃节点的任务。一个从节点崩溃时，从节点也许执行了部分任务，也许全部执行完，但没有报告结果。如果整个运算过程产生了其他作用，我们还有必要执行某些恢复过程来清除之前的状态。
        * 通信故障
            > 如果主节点和从节点之间无法进行信息交换，从节点将无法得知新任务分配给它。如果一个从节点与主节点的网络连接断开，比如网络分区（network partition）导致，重新分配一个任务可能会导致两个从节点执行相同的任务。如果一个任务允许多次执行，我们在进行任务再分配时可以不用验证第一个从节点是否完成了该任务。如果一个任务不允许，那么我们的应用需要适应多个从节点执行相同任务的可能性。

    * 主从模式总结
        * 主节点选举
            > 这是关键的一步，使得主节点可以给从节点分配任务。
        * 崩溃检测
            > 主节点必须具有检测从节点崩溃或失去连接的能力。
        * 组成员关系管理
            > 主节点必须具有知道哪一个从节点可以执行任务的能力。
        * 元数据管理
            > 主节点和从节点必须具有通过某种可靠的方式来保存分配状态和执行状态的能力。

    * 期望

         ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/coordinate-dream.png?raw=true)

        > 理想的方式是，以上每一个任务都需要通过原语(内核或微核提供核外调用的过程或函数称为原语(primitive))的方式暴露给应用，对开发者完全隐藏实现细节。ZooKeeper提供了实现这些原语的关键机制，因此，开发者可以通过这些实现一个最适合他们需求、更加关注应用逻辑的分布式应用。

* 什么是zookeeper
    * 来源
        > Zookeeper 最早起源于雅虎研究院的一个研究小组。在当时，研究人员发现，在雅虎内部很多大型系统基本都需要依赖一个类似的系统来进行分布式协调，但是这些系统往往都存在分布式单点问题。所以，雅虎的开发人员就试图开发一个通用的无单点问题的分布式协调框架，以便让开发人员将精力集中在处理业务逻辑上。

    * zookeeper是什么
        > ZooKeeper是一种用于分布式应用程序的高性能协调服务.

        > ZooKeeper is a high-performance coordination service for distributed applications. It exposes common services - such as naming, configuration management, synchronization, and group services - in a simple interface so you don't have to write them from scratch. You can use it off-the-shelf to implement consensus, group management, leader election, and presence protocols. And you can build on it for your own, specific needs.
        
        > ZooKeeper是一个典型的分布式数据一致性解决方案,其设计目标是将那些复杂且容易出错的分布式一致性服务封装起来，构成一个高效可靠的原语集，并以一系列简单易用的接口提供给用户使用。分布式应用程序可以基于 ZooKeeper 实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能。
    
    
    * 初识

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/dubbo-architecture-future.jpg?raw=true)

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/kafka-zookeeper.png?raw=true)

    * zk架构
        * 角色

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zkservice-1.jpg?raw=true)

            * Leader
                > Leader作为整个ZooKeeper集群的主节点，负责响应所有对ZooKeeper状态变更的请求。它会将每个状态更新请求进行排序和编号，以便保证整个集群内部消息处理的FIFO。

            * Follower
                > Follower主要是响应本服务器上的读请求外，另外follower还要处理leader的提议，并在leader提交该提议时在本地也进行提交。另外需要注意的是，leader和follower构成ZooKeeper集群的法定人数，也就是说，只有他们才参与新leader的选举、响应leader的提议。
            * Observe
                > 为客户端提供读服务器，如果是写服务则转发给Leader。不参与选举过程中的投票，也不参与“过半写成功”策略。在不影响写性能的情况下提升集群的读性能。

            * client
                > 连接zookeeper服务器的使用着，请求的发起者。独立于zookeeper服务器集群之外的角色。
                    
                ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zookeeper-construct-readandwrite.png?raw=true)
        
        * 数据模型znode
            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zknamespace.jpg?raw=true)
        
        * ZAB协议
            * 崩溃恢复
            * 原子广播

        
    * 特点
        * 简单化：ZooKeeper允许各分布式进程通过一个共享的命名空间相互联系，该命名空间类似于一个标准的层次型的文件系统。
        * 顺序一致性：按照客户端发送请求的顺序更新数据。
        * 原子性：更新要么成功，要么失败，不会出现部分更新。
        * 单一性 ：无论客户端连接哪个 server，都会看到同一个视图。
        * 可靠性：一旦数据更新成功，将一直保持，直到新的更新。
        * 及时性：客户端会在一个确定的时间内得到最新的数据。
        * 速度优势：ZooKeeper特别适合于以读为主要负荷的场合。ZooKeeper可以运行在数千台机器上，如果大部分操作为读，例如读写比例为10:1，ZooKeeper的效率会很高。

    * 运用场景
        * 数据发布与订阅（配置中心）
        * 负载均衡
        * 命名服务(Naming Service)
        * 分布式通知/协调
        * 集群管理与Master选举
        * 分布式锁
        * 分布式队列

* Standalone模式演示开始，本地启动
    * 配置
        * tickTime
            > ZooKeeper 中使用的基本时间单元, 以毫秒为单位, 默认值是 2000。它用来调节心跳和超时。
        * initLimit
            > 默认值是 10, 即 tickTime 属性值的 10 倍。它用于配置允许 followers 连接并同步到 leader 的最大时间。如果 ZooKeeper 管理的数据量很大的话可以增加这个值。
        * syncLimit
            > 默认值是 5, 即 tickTime 属性值的 5 倍。它用于配置leader 和 followers 间进行心跳检测的最大延迟时间。如果在设置的时间内 followers 无法与 leader 进行通信, 那么 followers 将会被丢弃。
        * dataDir
            > ZooKeeper 用来存储内存数据库快照的目录, 并且除非指定其它目录, 否则数据库更新的事务日志也将会存储在该目录下。
        * clientPort
            > 服务器监听客户端连接的端口, 也即客户端尝试连接的端口, 默认值是 2181。
    
    * /bin/命令
        * zkCleanup：清理Zookeeper历史数据，包括事务日志文件和快照数据文件
        * zkCli：Zookeeper的一个简易客户端
        * zkEnv：设置Zookeeper的环境变量
        * zkServer：Zookeeper服务器的启动、停止、和重启脚本

    * 监控命令
        > 在客户端可以通过 telnet 或 nc 向 ZooKeeper 提交相应的服务信息查询命令。使用方式`echo mntr | nc localhost 2181 `.
        * conf: 输出相关服务配置的详细信息。比如端口、zk数据及日志配置路径、最大连接数，session超时时间、serverId等
        * cons: 列出所有连接到这台服务器的客户端连接/会话的详细信息。包括“接受/发送”的包数量、session id 、操作延迟、最后的操作执行等信息.
        * stat: 输出服务器的详细信息：接收/发送包数量、连接数、模式（leader/follower）、节点总数、延迟。 所有客户端的列表。
        * envi: 输出关于服务器的环境详细信息（不同于conf命令），比如host.name、java.version、java.home、user.dir=/data/zookeeper-3.4.6/bin之类信息
        * ...


* 复制模式配置
    * 配置server id
        > zookeeper集群模式下还要配置一个myid文件,这个文件需要放在dataDir目录下,文件中写入一个id即可。
    * zoo.cfg配置集群server列表
        * 集群模式多了 server.id=host:port1:port2 的配置。
            ```
            server.1= 192.168.1.9:2888:3888
            server.2= 192.168.1.124:2888:3888
            server.3= 192.168.1.231:2888:3888
            ```
        > 其中，id 被称为 Server ID，用来标识该机器在集群中的机器序号（在每台机器的 dataDir 目录下创建 myid 文件，文件内容即为该机器对应的 Server ID 数字）。host 为机器 IP，port1 用于指定 Follower 服务器与 Leader 服务器进行通信和数据同步的端口，port2 用于进行 Leader 选举过程中的投票通信。
    

* 核心概念
    * 数据模型znode

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zknamespace.jpg?raw=true)

        * 存储   
            * 内存数据
                > Zookeeper的数据模型是树结构，在内存数据库中，存储了整棵树的内容，包括所有的节点路径、节点数据、ACL信息，Zookeeper会定时将这个数据存储到磁盘上。
                * DataTree
                    > 　DataTree是内存数据存储的核心，是一个树结构，代表了内存中一份完整的数据。DataTree不包含任何与网络、客户端连接及请求处理相关的业务逻辑，是一个独立的组件。
                * DataNode
                    > DataNode是数据存储的最小单元，其内部除了保存了结点的数据内容、ACL列表、节点状态之外，还记录了父节点的引用和子节点列表两个属性，其也提供了对子节点列表进行操作的接口。
                * ZKDatabase
                    > Zookeeper的内存数据库，管理Zookeeper的所有会话、DataTree存储和事务日志。ZKDatabase会定时向磁盘dump快照数据，同时在Zookeeper启动时，会通过磁盘的事务日志和快照文件恢复成一个完整的内存数据库。
            * 事务日志
                > 事务日志指zookeeper系统在正常运行过程中，针对所有的更新操作，在返回客户端“更新成功”的响应前，zookeeper会保证已经将本次更新操作的事务日志已经写到磁盘上，只有这样，整个更新操作才会生效。
        
        * 临时（Ephemeral）znode
            * as long as the session
            * 只能是在叶子节点上创建
        * 持久（PERSISTENT）znode
        * 顺序（SEQUENTIAL）znode
            * 在父节点下有序自增
            * int 
        * zxid
            * 有序
            * 全局唯一
        * zookeeper znode stat 结构

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zkgetcommanresponse.png?raw=true)

            * czxid Created ZXID表示该数据节点被创建时的事务ID
            * mzxid Modified ZXID 表示该节点最后一次被更新时的事务ID
            * pzxid 表示该节点的子节点列表最后一次被修改时的事务ID。只有子节点列表变更了才会变更pZxid,子节点内容变更不会影响pZxid
            * ctime Created Time表示节点被创建的时间
            * mtime Modified Time表示节点最后一次被更新的时间
            * dataVersion 数据节点版本号
            * cversion 子节点的版本号
            * aclVersion 节点的ACL版本号
            * ephemeralOwner 创建该临时节点的会话的SessionID。如果节点是持久节点，这个属性为0
            * dataLength 数据内容的长度
            * numChildren 当前节点的子节点个数

    * ZooKeeper Sessions
        > ZooKeeper的每个客户端都维护一组服务端信息，在创建连接时由应用指定，客户端随机选择一个服务端进行连接，连接成功后，服务端为每个连接分配一个唯一标识。客户端在创建连接时可以指定溢出时间，客户端会周期性的向服务端发送PING请求来保持连接，当客户端检测到与服务端断开连接后，客户端将自动选择服务端列表中的另一个服务端进行重连。

        * 创建会话
            ```
            ZooKeeper zk = new ZooKeeper(serverList, sessionTimeout, watcher);
            zk.create("/test", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            ```
            > 创建客户端session时，应用必须传入一组以逗号分隔的host:port列表，每个都对应一个ZooKeeper服务端，ZooKeeper客户端将选择任意一个服务端并尝试与其连接(这组serverlist会在初始化的时候打乱)，如果连接失败，或者由于某些原因导致客户端与服务端连接断开，客户端将自动的选择列表中的另一个服务端进行连接，直到成功。当session创建成功后，ZooKeeper服务端为session分配一个唯一标识。
        
            * 创建过程
                * client进行tcp建立连接
                * 当tcp连接成功之后，client发送一个ConnectRequest包，将ZooKeeper构造函数传入的sessionTimeout数值发给Server。zookeeper server会验证客户端发来的sessionTimeout值;zookeeper server中有连个配置项.
                    
                    * minSessionTimeout 单位毫秒。默认2倍tickTime
                    * maxSessionTimeout 单位毫秒。默认20倍tickTime
                
                    （tickTime也是一个配置项。是Server内部控制时间逻辑的最小时间单位）

                    > 如果客户端发来的sessionTimeout超过min-max这个范围，server会自动截取为min或max.

                * server等表决通过后，会为这个session生成一个password，连同sessionId，sessionTimeOut一起返回给客户端（ConnectResponse）。客户端如果需要重连Server，可以新建一个ZooKeeper对象，将上一个成功连接的ZooKeeper 对象的sessionId和password传给Server
                    ZooKeeper zk = new ZooKeeper(serverList, sessionTimeout, watcher, sessionId,passwd);ZKServer会根据sessionId和password为同一个client恢复session，如果还没有过期的话。

        * 会话状态
            > Zookeeper会话在整个运行期间的生命周期中，会在不同的会话状态中之间进行切换，这些状态可以分为CONNECTING, ASSOCIATING, CONNECTED, CLOSED, AUTH_FAILED。

            ![](https://github.com/moxingwang/resource/blob/master/image/kafka/zk-session-1.png?raw=true)

            > 一旦客户端开始创建Zookeeper对象，那么客户端状态就会变成CONNECTING状态，同时客户端开始尝试连接服务端，连接成功后，客户端状态变为CONNECTED，通常情况下，由于断网或其他原因，客户端与服务端之间会出现断开情况，一旦碰到这种情况，Zookeeper客户端会自动进行重连服务，同时客户端状态再次变成CONNCTING，直到重新连上服务端后，状态又变为CONNECTED，在通常情况下，客户端的状态总是介于CONNECTING和CONNECTED之间。但是，如果出现诸如会话超时、权限检查或是客户端主动退出程序等情况，客户端的状态就会直接变更为CLOSE状态。


        * session激活
            > 在ZooKeeper中，服务器和客户端之间维持的是一个长连接，在 SESSION_TIMEOUT 时间内，服务器会确定客户端是否正常连接(客户端会定时向服务器发送heart_beat),服务器重置下次SESSION_TIMEOUT时间。；同时在Zookeeper的实际设计中，只要客户端有请求发送到服务端，那么就会触发一次会话激活，总结下来两种情况都会触发会话激活。

            * 客户端向服务端发送请求，包括读写请求，就会触发会话激活。
            * 客户端会定时向服务器发送heart_beat。

        
        * 会话清理
            > leader server的SessionTracker管理线程会管理者session,执行session的过期检查,如果会话过期就执行清理操作.

        * 会话重连
            * CONNECTIONLOSS 
            * SESSIONEXPIRED

        * 客户端连接指定根路径
            > 在ZooKeeper 3.2.0增加了可选的“chroot”后缀，可以改变当前客户端的根路径。例如，如果使用”localhost:2181/app/a”，客户端将使用”/app/a”作为其根路径，所有的路径都会相对于该路径。比如操作路径”/foo/bar”将真正对应到”/app/a/foo/bar”。这个特征在多租户环境下是非常有用的，可以简化客户端的应用逻辑。

    * ZooKeeper Watches
        > 在ZooKeeper中，所有的读操作（getData，getChildren和exists）都可以设置监听,一个Watch事件是一个一次性的触发器，当被设置了Watch的数据发生了改变的时候，则服务器将这个改变发送给设置了Watch的客户端，以便通知它们。

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/watcher-notice-art.png?raw=true)

        * zookeeper机制的特点
            * 一次性的触发器（one-time trigger）  
                > 当数据改变的时候，那么一个Watch事件会产生并且被发送到客户端中。但是客户端只会收到一次这样的通知，如果以后这个数据再次发生改变的时候，之前设置Watch的客户端将不会再次收到改变的通知，因为Watch机制规定了它是一个一次性的触发器。      
            * 发送到客户端（Sent to the client）    
                > 这个表明了Watch的通知事件是从服务器发送给客户端的，是异步的，这就表明不同的客户端收到的Watch的时间可能不同，但是ZooKeeper有保证：当一个客户端在看到Watch事件之前是不会看到结点数据的变化的。例如：A=3，此时在上面设置了一次Watch，如果A突然变成4了，那么客户端会先收到Watch事件的通知，然后才会看到A=4。
            * 监听方式（The data for which the watch was set）
                > znode 节点本身具有不同的改变方式,setData() 会触发设置在某一节点上所设置的数据监视(假定数据设置成功)，而一次成功的 create() 操作则会出发当前节点上所设置的数据监视以及父节点的子节点监视。一次成功的 delete() 操作将会触发当前节点的数据监视和子节点监视事件，同时也会触发该节点父节点的child watch。WatchEvent是最小的通信单元，结构上只包含通知状态、事件类型和节点路径。ZooKeeper服务端只会通知客户端发生了什么，并不会告诉具体内容。

        * 监听事件类型
            * Created event：调用exists方法设置监听；
            * Deleted event：调用exists、getData、getChildren设置监听；
            * Changed event：调用getData设置监听；
            * Child event：调用getChildren设置监听。
    
    * ACL 权限控制
        > zk做为分布式架构中的重要中间件，通常会在上面以节点的方式存储一些关键信息，默认情况下，所有应用都可以读写任何节点，在复杂的应用中，这不太安全，ZK通过ACL机制来解决访问权限问题.

        * 身份认证方式
            * world：默认方式，相当于全世界都能访问
            * auth：代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户)
            * digest：即用户名:密码这种方式认证，这也是业务系统中最常用的
            * ip：使用Ip地址认证


* 回顾zookeeper架构

    ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zkservice-1.jpg?raw=true)

* ZAB协议
    > ZAB协议（Zookeeper Atomic Broadcast Protocol）是Zookeeper系统专门设计的一种支持崩溃恢复的原子广播协议。Zookeeper使用该协议来实现分布数据一致性并实现了一种主备模式的系统架构来保持各集群中各个副本之间的数据一致性。采用zab协议的最大目标就是建立一个高可用可扩展的分布式数据主备系统。即在任何时刻只要leader发生宕机，都能保证分布式系统数据的可靠性和最终一致性。

    * 特点
        * 一致性保证
            * 可靠提交(Reliable delivery) -如果一个事务 A 被一个server提交(committed)了，那么它最终一定会被所有的server提交
            * 全局有序(Total order) - 假设有A、B两个事务，有一台server先执行A再执行B，那么可以保证所有server上A始终都被在B之前执行
            * 因果有序(Causal order) - 如果发送者在事务A提交之后再发送B,那么B必将在A之前执行
        * 只要大多数（法定数量）节点启动，系统就行正常运行
        * 当节点下线后重启，它必须保证能恢复到当前正在执行的事务

    * ZAB协议工作原理
        > ZAB协议要求每个leader都要经历三个阶段，即发现，同步，广播。

            * 发现：即要求zookeeper集群必须选择出一个leader进程，同时leader会维护一个follower可用列表。将来客户端可以与这个follower中的节点进行通信。
            * 同步：leader要负责将本身的数据与follower完成同步，做到多副本存储。这样也是体现了CAP中高可用和分区容错。follower将队列中未处理完的请求消费完成后，写入本地事物日志中。
            * 广播：leader可以接受客户端新的proposal请求，将新的proposal请求广播给所有的follower。

    * ZAB两种模式
        * 崩溃恢复
            > 当服务初次启动，或者 leader 节点挂了，系统就会进入恢复模式，直到选出了有合法数量 follower 的新 leader，然后新 leader 负责将整个系统同步到最新状态。
        * 消息广播模式
            > Zab 协议中，所有的写请求都由 leader 来处理。正常工作状态下，leader 接收请求并通过广播协议来处理。
        

* 选举
    * 问题: 如何选举leader
        > 某个服务可以配置为多个实例共同构成一个集群对外提供服务。其每一个实例本地都存有冗余数据，每一个实例都可以直接对外提供读写服务。在这个集群中为了保证数据的一致性，需要有一个Leader来协调一些事务。那么问题来了：如何确定哪一个实例是Leader呢？

        * 选举的难点
            1. 没有一个仲裁者来选定Leader
            2. 每一个实例本地可能已经存在数据，不确定哪个实例上的数据是最新的

    
    * 分布式选举算法
        * Paxos
        * Raft
        * ZooKeeper ZAB


* zookeeper选主
    * 搞清楚几个问题
        * 一个Server是如何知道其它的Server？
            > 在ZooKeeper集群中，Server的信息都在zoo.conf配置文件中，根据配置文件的信息就可以知道其它Server的信息。
        * 成为Leader的必要条件？
            > Leader要具有最高的zxid；集群中大多数的机器（至少n/2+1）得到响应并follow选出的Leader。
        * 如果所有zxid都相同(例如: 刚初始化时)，此时有可能不能形成n/2+1个Server，怎么办？
            > ZooKeeper中每一个Server都有一个ID，这个ID是不重复的，如果遇到这样的情况时，ZooKeeper就推荐ID最大的哪个Server作为Leader。
        * ZooKeeper中Leader怎么知道Fllower还存活，Fllower怎么知道Leader还存活？
            > Leader定时向Fllower发ping消息，Fllower定时向Leader发ping消息，当发现Leader无法ping通时，就改变自己的状态(LOOKING)，发起新的一轮选举。

    * leader选主时机
        1. Server初始化
        2. server运行期间无法和leader保持连接
    
    * 核心概念
        * ZooKeeper服务器状态

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zookeeper-construct-1.png?raw=true)

            * LOOKING：寻找leader状态
            * LEADING：领导状态（节点为leader）
            * FOLLOWING：跟随者状态
            * OBSERVING：观察者状态（此状态不参与选举）
        * myid
            > 每个Zookeeper服务器，都需要在数据文件夹下创建一个名为myid的文件，该文件包含整个Zookeeper集群唯一的ID（整数）。例如某Zookeeper集群包含三台服务器，hostname分别为zoo1、zoo2和zoo3，其myid分别为1、2和3，则在配置文件中其ID与hostname必须一一对应，如下所示。在该配置文件中，server.后面的数据即为myid.
            ```
            server.1=zoo1:2888:3888
            server.2=zoo2:2888:3888
            server.3=zoo3:2888:3888
            ```
        * zxid
            > 每次对Zookeeper的状态的改变都会产生一个zxid（ZooKeeper Transaction Id），zxid是全局有序的，如果zxid1小于zxid2，则zxid1在zxid2之前发生。为了保证顺序性，该zkid必须单调递增。因此Zookeeper使用一个64位的数来表示，高32位是Leader的epoch，从1开始，每次选出新的Leader，epoch加一。低32位为该epoch内的序号，每次epoch变化，都将低32位的序号重置。这样保证了zkid的全局递增性。
        * logicalclock 
            > 每个服务器会维护一个自增的整数，名为logicalclock，它表示这是该服务器发起的第多少轮投票。

    * 选主步骤
        * 状态变更
            > 服务器启动的时候每个server的状态时Looking，如果是leader挂掉后进入选举，那么余下的非Observer的Server就会将自己的服务器状态变更为Looking，然后开始进入Leader的选举状态；
        * 自增选举轮次
            > Zookeeper规定所有有效的投票都必须在同一轮次中。每个服务器在开始新一轮投票时，会先对自己维护的logicalclock进行自增操作。
        * 初始化选票
            > 每个服务器在广播自己的选票前，会将自己的投票箱清空。该投票箱记录了所收到的选票。例：服务器2投票给服务器3，服务器3投票给服务器1，则服务器1的投票箱为(2, 3), (3, 1), (1, 1)。票箱中只会记录每一投票者的最后一票，如投票者更新自己的选票，则其它服务器收到该新选票后会在自己票箱中更新该服务器的选票。
        * 发起投票
            > 每个server会产生一个（sid，zxid）的投票，系统初始化的时候zxid都是0，如果是运行期间，每个server的zxid可能都不同，这取决于最后一次更新的数据。将投票发送给集群中的所有机器；
        * 接收外部投票
            > 服务器会尝试从其它服务器获取投票，并记入自己的投票箱内。如果无法获取任何外部投票，则会确认自己是否与集群中其它服务器保持着有效连接。如果是，则再次发送自己的投票；如果否，则马上与之建立连接。
        * 判断选举轮次
            > 收到外部投票后，首先会根据投票信息中所包含的logicalclock来进行不同处理.
                * 外部投票的logicalclock大于自己的logicalclock。说明该服务器的选举轮次落后于其它服务器的选举轮次，立即清空自己的投票箱并将自己的logicalclock更新为收到的logicalclock，然后再对比自己之前的投票与收到的投票以确定是否需要变更自己的投票，最终再次将自己的投票广播出去。
                * 外部投票的logicalclock小于自己的logicalclock。当前服务器直接忽略该投票，继续处理下一个投票。
                * 外部投票的logickClock与自己的相等。当时进行选票PK。
        * 处理投票
            > 对自己的投票和接收到的投票进行PK：
                1. 先检查zxid，较大的优先为leader；
                2. 如果zxid一样，sid较大的为leader；
                3. 根据PK结果更新自己的投票，在次发送自己的投票；
        * 统计投票
            > 每次投票后，服务器统计投票信息，如果有过半机器接收到相同的投票，那么leader产生，如果否，那么进行下一轮投票；
        * 改变server状态
            > 一旦确定了Leader，server会更新自己的状态为Following或者是Leading。选举结束。

    * 几种leader选举场景
        * 集群启动选举
        * Follower重启选举
        * Leader重启选举

    * 举例
        * 集群启动选举

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/start_election_1.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/start_election_2.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/start_election_3.png?raw=true)

        * Follower重启选举

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/follower_restart_election_1.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/follower_restart_election_2.png?raw=true)

        * Leader重启选举

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/leader_restart_election_1.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/leader_restart_election_2.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/leader_restart_election_3.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/leader_restart_election_4.png?raw=true)

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/leader_restart_election_5.png?raw=true)


* 数据同步
    > 在完成leader选举阶段后，准Leader可以获取集群中最新的提议历史。准Leader在该阶段会把最新的提议历史同步到集群中的所有节点。当同步完成时(过半)，准Leader才会真正成为Leader，执行Leader的工作。

    * 恢复模式需要解决的两个重要问题
        * 已经被处理的消息不能丢
        * 被丢弃的消息不能再次出现




* 原子广播
    * 分布式一致问题
        > 分布式中有这么一个疑难问题，客户端向一个分布式集群的服务端发出一系列更新数据的消息，由于分布式集群中的各个服务端节点是互为同步数据的，所以运行完客户端这系列消息指令后各服务端节点的数据应该是一致的，但由于网络或其他原因，各个服务端节点接收到消息的序列可能不一致，最后导致各节点的数据不一致。

    * 分布式一致性

        * CAP

            ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/cap-0.jpg?raw=true)

            > 分布式系统的最大难点，就是各个节点的状态如何同步。CAP 定理是这方面的基本定理，也是理解分布式系统的起点。
            * Consistency (一致性)
                > 写操作之后的读操作，必须返回该值。
            * Availability (可用性)
                > 意思是只要收到用户的请求，服务器就必须给出回应。每次请求都能获取到非错的响应——但是不保证获取的数据为最新数据。
            * Partition tolerance (分区容错)
                > 区间通信可能失败。

            > 这三个基本需求，最多只能同时满足其中的两项，一致性和可用性不可能同时成立，因为可能通信失败（即出现分区容错）。
        
        * 拜占庭问题
            > 11位拜占庭将军去打仗, 他们各自有权力观测敌情并作出判断, 进攻或撤退, 那么怎么让他们只用传令兵达成一致呢?一种很符合直觉的方法就是投票,每位将军作出决定后都将结果"广播"给其余所有将军, 这样所有将军都能获得同样的11份(包括自己)结果, 取多数, 即可得到全军都同意的行为.但如果这11位将军中有间谍呢? 假设有9位忠诚的将军, 5位判断进攻, 4位判断撤退, 还有2个间谍恶意判断撤退, 虽然结果是错误的撤退, 但这种情况完全是允许的. 因为这11位将军依然保持着状态一致性。

        * 一致性解决方案
            * 2PC和3PC
                * 2PC
                    > 第一阶段：准备阶段(投票阶段)和第二阶段：提交阶段（执行阶段）。
                * 3PC
                    > 在第一阶段和第二阶段中插入一个准备阶段。保证了在最后提交阶段之前各参与节点的状态是一致的。引入超时机制，同时在协调者和参与者中都引入超时机制。

                * 区别
                    > 相对于2PC，3PC主要解决的单点故障问题，并减少阻塞，因为一旦参与者无法及时收到来自协调者的信息之后，他会默认执行commit。而不会一直持有事务资源并处于阻塞状态。但是这种机制也会导致数据一致性问题，因为，由于网络原因，协调者发送的abort响应没有及时被参与者接收到，那么参与者在等待超时之后执行了commit操作。这样就和其他接到abort命令并执行回滚的参与者之间存在数据不一致的情况。
                
                * 总结
                    > 无论是二阶段提交还是三阶段提交都无法彻底解决分布式的一致性问题。那么世上只有一种一致性算法，那就是Paxos，所有其他一致性算法都是Paxos算法的不完整版。

            * Paxos

                ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/Paxos-1.jpg?raw=true)
                

    
    * ZAB原子广播（数据一致原理）
        >  paxos理论到实际是个艰难的过程。比如怎样在分布式环境下维持一个全局唯一递增的序列，如果是靠数据库的自增主键，那么整个系统的稳定和性能的瓶颈全都集中于这个单点。paxos算法也没有限制Proposer的个数，Proposer个数越多，那么达成一致所造成的碰撞将越多，甚至产生活锁，如果限制Proposer的个数为一个，那么就要考虑唯一的Proposer崩溃要怎么处理。

    * 工作步骤

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zab-1.png?raw=true)

        1. leader从客户端收到一个写请求
        2. leader生成一个新的事务并为这个事务生成一个唯一的ZXID，
        3. leader将这个事务发送给所有的follows节点
        4. follower节点将收到的事务请求加入到历史队列(history queue)中,并发送ack给ack给leader
        5. 当leader收到大多数follower（超过法定数量）的ack消息，leader会发送commit请求
        6. 当follower收到commit请求时，会判断该事务的ZXID是不是比历史队列中的任何事务的ZXID都小，如果是则提交，如果不是则等待比它更小的事务的commit.

        ![](https://github.com/moxingwang/resource/blob/master/image/zookeeper/zab-2.png?raw=true)
    

* 扩展
    * Client-java
    * Curator
        > Curator是Netflix公司开源的一套Zookeeper客户端框架。了解过Zookeeper原生API都会清楚其复杂度。Curator帮助我们在其基础上进行封装、实现一些开发细节，包括接连重连、反复注册Watcher和NodeExistsException等。


* 整体回顾

    ![](https://raw.githubusercontent.com/moxingwang/resource/master/image/zookeeper/zkservice-1.jpg)

* 思考问题
    * 一个客户端修改了某个节点的数据，其它客户端能够马上获取到这个最新数据吗(跨客户端视图的并发一致性)
    * 集群中clientPort不一致，可以等了解了读写机制理解
    * observer是怎么设置的
    * zxid溢出变成负数了怎么办
    * 水平扩容
    * zookeeper 有哪些缺点
        * 数据量大，同步慢，超时



#### reference
* [ZooKeeper基本原理](https://www.cnblogs.com/luxiaoxun/p/4887452.html)
* [源码Zookeeper 集群版建立连接过程](https://my.oschina.net/xianggao/blog/538839)
* [ZooKeeper解惑](http://jm.taobao.org/2011/05/30/947/)
* [ZooKeeper FAQ](http://jm.taobao.org/2013/10/07/zookeeper-faq/)
* [Zookeeper会话](http://www.cnblogs.com/leesf456/p/6103870.html)
* [zookeeper curator处理会话过期session expired](https://www.cnblogs.com/kangoroo/p/7538314.html)
* [zookeeper之数据模型](https://blog.csdn.net/usagoole/article/details/82944230)
* [ZooKeeper session管理](https://blog.csdn.net/tomato__/article/details/78560727)
* [ZooKeeper的Znode剖析](https://blog.csdn.net/lihao21/article/details/51810395)
* [ZooKeeper数据一致性](https://blog.csdn.net/tomato__/article/details/78673365)
* [一直对zookeeper的应用和原理比较迷糊，今天看一篇文章，讲得很通透](https://blog.csdn.net/gs80140/article/details/51496925)
* [Zookeeper - CLI](https://www.tutorialspoint.com/zookeeper/zookeeper_cli.htm)
* [分布式一致性原理、Paxos算法与Zookeeper的ZAB协议、Zookeeper使用场景与在电商系统中的应用](https://blog.csdn.net/zhengzhihust/article/details/53456371)
* [关于若干选举算法的解释与实现](http://blog.jobbole.com/104832/)
* [Zookeeper的FastLeaderElection算法分析](https://www.jianshu.com/p/ccaecde36dd3)
* [深入浅出Zookeeper（一） Zookeeper架构及FastLeaderElection机制](http://www.jasongj.com/zookeeper/fastleaderelection/)
* [聊聊zookeeper的ZAB算法](https://juejin.im/entry/5b84d589e51d453885032159)
* [ZAB协议的那些事？](https://juejin.im/post/5b0633f96fb9a07ab45903ed)
* [ZooKeeper典型应用场景一览](http://jm.taobao.org/2011/10/08/1232/)
* [Zookeeper的sync操作是什么？](https://www.jianshu.com/p/44a1b28b1c98)
* [ZAB协议详解](https://blog.csdn.net/xiaocai9999/article/details/80641404)
* [Zookeeper请求处理](http://www.cnblogs.com/leesf456/p/6140503.html)
* [zookeeper leader和learner的数据同步](https://blog.csdn.net/weixin_36145588/article/details/75043611)
* [品味ZooKeeper之Watcher机制](https://www.jianshu.com/p/4c071e963f18)
* [关于分布式事务、两阶段提交协议、三阶提交协议](https://www.hollischuang.com/archives/681)
* [分布式系统的一致性协议之 2PC 和 3PC](https://matt33.com/2018/07/08/distribute-system-consistency-protocol/)
* [2PC/3PC、paxos与ZAB协议](https://my.oschina.net/chener/blog/1504093)
* [Zookeeper数据与存储](https://www.cnblogs.com/leesf456/p/6179118.html)
