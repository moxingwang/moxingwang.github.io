## additivity
> 它是 子Logger 是否继承 父Logger 的 输出源（appender） 的标志位。具体说，默认情况下子Logger会继承父Logger的appender，也就是说子Logger会在父Logger的appender里输出。若是additivity设为false，则子Logger只会在自己的appender里输出，而不会在父Logger的appender里输出。

* [默认日志logback配置解析](http://tengj.top/2017/04/05/springboot7/?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io)

## eg
````
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

	<appender name="infoLog" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="/logs/order-center-dubbo-service/info.log" />
		<param name="DatePattern" value="'.'yyyy-MM-dd'.log'" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%l] %m%n" />
		</layout>
		<!--过滤器设置输出的级别 -->
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="levelMin" value="info" />
			<param name="levelMax" value="info" />
			<param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>
	<appender name="warnLog" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="/logs/order-center-dubbo-service/warn.log" />
		<param name="DatePattern" value="'.'yyyy-MM-dd'.log'" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%l] %m%n" />
		</layout>
		<!--过滤器设置输出的级别 -->
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="levelMin" value="warn" />
			<param name="levelMax" value="warn" />
			<param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>
	<appender name="errorLog" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="File" value="/logs/order-center-dubbo-service/error.log" />
		<param name="DatePattern" value="'.'yyyy-MM-dd'.log'" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%l] %m%n" />
		</layout>
		<!--过滤器设置输出的级别 -->
		<filter class="org.apache.log4j.varia.LevelRangeFilter">
			<param name="levelMin" value="error" />
			<param name="levelMax" value="error" />
			<param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>



	<appender class="org.apache.log4j.ConsoleAppender" name="stdout">
		<layout class="org.apache.log4j.PatternLayout">
			<param value="%d{yyyy-MM-dd HH:mm:ss} [%p]-[%t]-[%l] %m%n" name="ConversionPattern" />
		</layout>
	</appender>

	<appender class="org.apache.log4j.DailyRollingFileAppender" name="orderDailyRollingPattern">
		<param name="File" value="/data/logs/order.log" />
		<param name="DatePattern" value="'.'yyyy-MM-dd'.log'" />
		<layout class="org.apache.log4j.PatternLayout">
			<param value="%m%n" name="ConversionPattern" />
		</layout>
	</appender>

	<logger name="orderDailyRollingPattern" additivity="false">
		<level value="info" />
		<appender-ref ref="orderDailyRollingPattern" />
	</logger>

	<logger name="com.chinaredstar" additivity="true">
		<level value="debug" />
	</logger>

	<root>
		<level value="info" />
		<appender-ref ref="stdout" />
		<appender-ref ref="infoLog" />
		<appender-ref ref="warnLog" />
		<appender-ref ref="errorLog" />
	</root>
</log4j:configuration>
````

## 参考文章
[log4j配置文件中的additivity属性](http://blog.csdn.net/junshao90/article/details/8364812)