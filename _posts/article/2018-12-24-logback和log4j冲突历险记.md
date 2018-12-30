# 问题现象
1. 项目默认走logback，log4j不起作用。
2. 工程里面找不到logback的jar包。

![](https://raw.githubusercontent.com/m65536/resource/master/image/log4jlogback.png)

command+F未发现logback的jar包。
3. 测试环境打包后发现lib目录下面有logback的jar包。


# 解决思路
> jar相互依赖导致的。

1. 搜索logback

![](https://raw.githubusercontent.com/m65536/resource/master/image/logback_search.png)

发现项目中使用到了spring-boot-starter，spring-boot-starter依赖spring-boot-starter-logger，spring-boot-starter-logger依赖了logback-classic。

# 总结
类似这种log打印问题，如果想要的日志被覆盖问题，大部分都是jar包作怪，仔细排查搜索就能找到问题。

# 附

## log4j.xml
````$xslt
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

    <appender class="org.apache.log4j.ConsoleAppender" name="stdout">
        <layout class="org.apache.log4j.PatternLayout">
            <param value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%X{traceId}]-[%l] %m%n"
                   name="ConversionPattern"/>
        </layout>
    </appender>
    <appender name="infoLog" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="/logs/p-longyi-gis-service/info.log"/>
        <param name="DatePattern" value="'.'yyyy-MM-dd'.log'"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%X{traceId}]-[%l] %m%n"/>
        </layout>
        <!--过滤器设置输出的级别-->
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="levelMin" value="info"/>
            <param name="levelMax" value="info"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>
    <appender name="warnLog" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="/logs/p-longyi-gis-service/warn.log"/>
        <param name="DatePattern" value="'.'yyyy-MM-dd'.log'"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%X{traceId}]-[%l] %m%n"/>
        </layout>
        <!--过滤器设置输出的级别-->
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="levelMin" value="warn"/>
            <param name="levelMax" value="warn"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>
    <appender name="errorLog" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="/logs/p-longyi-gis-service/error.log"/>
        <param name="DatePattern" value="'.'yyyy-MM-dd'.log'"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%X{traceId}]-[%l] %m%n"/>
        </layout>
        <!--过滤器设置输出的级别-->
        <filter class="org.apache.log4j.varia.LevelRangeFilter">
            <param name="levelMin" value="error"/>
            <param name="levelMax" value="error"/>
            <param name="AcceptOnMatch" value="true"/>
        </filter>
    </appender>

    <logger name="com.chinaredstar" additivity="true">
        <level value="debug" />
    </logger>

    <root>
        <level value="info"/>
        <appender-ref ref="stdout"/>
        <appender-ref ref="infoLog"/>
        <appender-ref ref="warnLog"/>
        <appender-ref ref="errorLog"/>
    </root>
</log4j:configuration>
````

## logback.xml
````$xslt
<?xml version="1.0" encoding="UTF-8"?>
<!--
scan：当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
scanPeriod：设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒当scan为true时，此属性生效。默认的时间间隔为1分钟。
debug：当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。
-->
<configuration scan="false" debug="false">
    <!-- 定义日志的根目录 -->
    <property name="LOG_HOME" value="/logs/p-longyi-gis-service" />
    <!-- ch.qos.logback.core.ConsoleAppender 表示控制台输出 -->
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <Encoding>UTF-8</Encoding>
        <!--
        日志输出格式：%d表示日期时间，%thread表示线程名，%-5level：级别从左显示5个字符宽度
        %logger{50} 表示logger名字最长50个字符，否则按照句点分割。 %msg：日志消息，%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SS} [%-5level]-[%thread]-[%caller{1,evaluator-1}]-%msg%n</pattern>

            <!--<pattern>%d{yyyy-MM-dd HH:mm:ss} [%p][%c][%M][%l][%L] %L-> %m%n</pattern>-->
            <!--<pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>-->
            <!--<pattern>>%d{yyyy-MM-dd HH:mm:ss} | %-5p | [%thread] %logger{5}:(%F:%L) %msg%n</pattern>-->

            <!--<pattern>>%d{yyyy-MM-dd HH:mm:ss}[%-5p][%thread] %logger{5}(%F:%L) %msg%n</pattern>-->

        </layout>
    </appender>



    <!-- INFO级别日志 appender -->
    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 过滤器，只记录INFO级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <Encoding>UTF-8</Encoding>
        <!-- 指定日志文件的名称 -->
        <file>${LOG_HOME}/info.log</file>
        <!--
        当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名
        TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--
            滚动时产生的文件的存放位置及文件名称 %d{yyyy-MM-dd}：按天进行日志滚动
            %i：当文件大小超过maxFileSize时，按照i进行文件滚动
            -->
            <fileNamePattern>${LOG_HOME}/info.log.%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <!--
            可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。假设设置每天滚动，
            且maxHistory是365，则只保存最近365天的文件，删除之前的旧文件。注意，删除旧文件是，
            那些为了归档而创建的目录也会被删除。
            -->
            <MaxHistory>365</MaxHistory>
            <!--
            当日志文件超过maxFileSize指定的大小是，根据上面提到的%i进行日志文件滚动 注意此处配置SizeBasedTriggeringPolicy是无法实现按文件大小进行滚动的，必须配置timeBasedFileNamingAndTriggeringPolicy
            -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <!--
        日志输出格式：%d表示日期时间，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %logger{50} 表示logger名字最长50个字符，否则按照句点分割。 %msg：日志消息，%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </layout>
    </appender>

    <appender name="ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 过滤器，只记录INFO级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <Encoding>UTF-8</Encoding>
        <!-- 指定日志文件的名称 -->
        <file>${LOG_HOME}/error.log</file>
        <!--
        当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名
        TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--
            滚动时产生的文件的存放位置及文件名称 %d{yyyy-MM-dd}：按天进行日志滚动
            %i：当文件大小超过maxFileSize时，按照i进行文件滚动
            -->
            <fileNamePattern>${LOG_HOME}/error.log.%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <!--
            可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。假设设置每天滚动，
            且maxHistory是365，则只保存最近365天的文件，删除之前的旧文件。注意，删除旧文件是，
            那些为了归档而创建的目录也会被删除。
            -->
            <MaxHistory>365</MaxHistory>
            <!--
            当日志文件超过maxFileSize指定的大小是，根据上面提到的%i进行日志文件滚动 注意此处配置SizeBasedTriggeringPolicy是无法实现按文件大小进行滚动的，必须配置timeBasedFileNamingAndTriggeringPolicy
            -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <!--
        日志输出格式：%d表示日期时间，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %logger{50} 表示logger名字最长50个字符，否则按照句点分割。 %msg：日志消息，%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </layout>
    </appender>


    <appender name="WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 过滤器，只记录INFO级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <Encoding>UTF-8</Encoding>
        <!-- 指定日志文件的名称 -->
        <file>${LOG_HOME}/warn.log</file>
        <!--
        当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名
        TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--
            滚动时产生的文件的存放位置及文件名称 %d{yyyy-MM-dd}：按天进行日志滚动
            %i：当文件大小超过maxFileSize时，按照i进行文件滚动
            -->
            <fileNamePattern>${LOG_HOME}/warn.log.%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <!--
            可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。假设设置每天滚动，
            且maxHistory是365，则只保存最近365天的文件，删除之前的旧文件。注意，删除旧文件是，
            那些为了归档而创建的目录也会被删除。
            -->
            <MaxHistory>365</MaxHistory>
            <!--
            当日志文件超过maxFileSize指定的大小是，根据上面提到的%i进行日志文件滚动 注意此处配置SizeBasedTriggeringPolicy是无法实现按文件大小进行滚动的，必须配置timeBasedFileNamingAndTriggeringPolicy
            -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <!--
        日志输出格式：%d表示日期时间，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %logger{50} 表示logger名字最长50个字符，否则按照句点分割。 %msg：日志消息，%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </layout>
    </appender>



    <!--
    logger主要用于存放日志对象，也可以定义日志类型、级别
    name：表示匹配的logger类型前缀，也就是包的前半部分
    level：要记录的日志级别，包括 TRACE < DEBUG < INFO < WARN < ERROR
    additivity：作用在于children-logger是否使用 rootLogger配置的appender进行输出，false：表示只用当前logger的appender-ref，true：表示当前logger的appender-ref和rootLogger的appender-ref都有效
    -->

    <logger name="com.chinaredstar" level="debug" additivity="true">
        <!--<appender-ref ref="stdout" />-->
    </logger>

    <!--
    root与logger是父子关系，没有特别定义则默认为root，任何一个类只会和一个logger对应，
    要么是定义的logger，要么是root，判断的关键在于找到这个logger，然后判断这个logger的appender和level。
    -->
    <root level="info">
        <appender-ref ref="stdout" />
        <appender-ref ref="INFO" />
        <appender-ref ref="ERROR" />
        <appender-ref ref="WARN" />
    </root>
</configuration>
````