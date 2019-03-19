> 做开发或者测试，天天可能需要调用REST接口联调或者测试，查看数据返回是否正确、查看返回的JSON格式、格式化JSON,看似简单的事情，做起来很费事，而且烦人！看见很多同事使用最多的就是Postman和curl操作了,先看看这两种操作方式。

### postman发请求
* 下载postman应用
* 配置接口，设置参数
* 多个电脑使用可能还需要使用同一个账号同步

### curl发请求
* 写先好curl命令
* 复制到终端执行
* 对response JSON格式化

上面两种方式给我的感觉是都很麻烦，接口请求参数多了配置很麻烦、总是在复制来复制去，耗时、跨平台不方便、不方便保存、不方便分享等等。

期望有一种简单方便的请求参数配置，跨平台，方便保存，方便查看请求结果，能够够集成到多种IDE，方便开发和测试使用。

接下来介绍两大神奇，分别是 VSCode[REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)和jetbrains家族[HTTP Client in IntelliJ IDEA Code Editor](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#creating-an-http-request-file?utm_source=hacpai.com),毫不夸张的说，如果你正在使用VSCode或者jetbrains家族的IDE,有了他们让你的工作效率大大提高，并且你会深深的爱上他。

# 在VSCode中使用REST Client

REST Client支持`cURL` 和 `RFC 2616` 两种标准来调用REST API, 使用起来非常简单，只需要写一个以`.http `或者 `.rest` 结尾命令的的文件即可实现调用。

首先来看个简单例子，这里有一个GET接口(https://httpbin.org/ip)看看如何调用。

> `注意：`  `httpbin.org`是一个开源的接口测试网站，它能测试 HTTP 请求和响应的各种信息，比如 cookie、ip、headers 和登录验证等，且支持 GET、POST 等多种方法，对 web 开发和测试很有帮助。[https://httpbin.org/](https://httpbin.org/)


* 首先在VSCode中创建一个名叫`test.http`(下载地址：[test.http](https://github.com/moxingwang/resource/blob/master/image/web/test.http))的文件，然后加入以下代码
```
### Get request with a header
GET https://httpbin.org/ip
Accept: application/json

### Get request with a header
curl -H "Content-Type:application/json" -XGET 'https://httpbin.org/ip'
```

![](https://github.com/moxingwang/resource/blob/master/image/web/rest%20client%20http%201.png?raw=true)

* 发送请求
发送请求非常简单，只需要点击上图中的`Send Request`即可执行，最终得到结果如下

![](https://github.com/moxingwang/resource/blob/master/image/web/rest%20client%20http%202.png?raw=true)

* 查看结果
执行返回后，HTTP的状态信息和header都在右侧，并且对body已经格式化好了，是不是非常方便，真的是太方便了。

可以看到上面对同一个接口调用有两种调用方式，其执行结构都是一样，即使你写的再复杂，参数再多的curl请求，拿过来保存在这里直接执行即可，比如下面这样一个例子（直接copy我的一个本地测试）,直接copy到test.http这个文件中执行即可，并且还能够美观的查看执行结果。

```
curl -H "Content-Type:application/json" -XPUT 'http://localhost:8083/connectors/test-connector/config' -d '
{
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "localhost",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "password",
    "database.server.id": "19991",
    "database.server.name": "trade_order",
    "database.whitelist": "db_order",
    "include.schema.changes": "false",
    "snapshot.mode": "schema_only",
    "snapshot.locking.mode": "none",
    "database.history.kafka.bootstrap.servers": "localhost:9092",
    "database.history.kafka.topic": "dbhistory.trade_order",
    "decimal.handling.mode": "string",
     "table.whitelist": "db_order.t_order_item",
    "database.history.store.only.monitored.tables.ddl":"true",
    "database.history.skip.unparseable.ddl":"true"
}'
```

### 常用写法
REST Client的写法非常简单，你只需要知道HTTP请求的构成就行，分别是`Query Strings`、`Request Headers`、`Request Body`，只需要看一个例子就会写所有的，更复杂的写法查看[REST Client Overview](https://marketplace.visualstudio.com/items?itemName=humao.rest-client),常见构成结构如下。

```
请求方法 地址
header

request body
```

例如：

```
### Send POST request with json body
POST https://httpbin.org/post?client=ios&name=哈哈哈
Content-Type: application/json
myHeader: myheader
auth-token: mytoken

{
  "id": 999,
  "value": "content"
}
```

掌握这一个例子适用于大部分场景就够了,当然了这里只是一个介绍，REST Client还支持好多功能，非常优秀，非常好用，简直是爱不释手，官网文档都有[REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)。


# 在IntelliJ中使用HTTP Client in IntelliJ IDEA Code Editor
> 在中使用HTTP Client in IntelliJ IDEA Code Editor和在VSCode中使用REST Client一样，唯一的区别就是IntelliJ IDE暂时不支持curl的方式。

# 附：常见例子
### Send POST request with json body
```
POST https://httpbin.org/post
Content-Type: application/json

{
  "id": 999,
  "value": "content"
}
```

### Send POST request with body as parameters
```
POST https://httpbin.org/post
Content-Type: application/x-www-form-urlencoded

id=999&value=content
```

### Send a form with the text and file fields
```
POST https://httpbin.org/post
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="element-name"
Content-Type: text/plain

Name
--WebAppBoundary
Content-Disposition: form-data; name="data"; filename="data.json"
Content-Type: application/json

< ./request-form-data.json
--WebAppBoundary--
```

### Basic authorization.
```
GET https://httpbin.org/basic-auth/user/passwd
Authorization: Basic user passwd
```

### Basic authorization with variables.
```
GET https://httpbin.org/basic-auth/user/passwd
Authorization: Basic {{username}} {{password}}
```

### Digest authorization.
```
GET https://httpbin.org/digest-auth/realm/user/passwd
Authorization: Digest user passwd
```

### Digest authorization with variables.
```
GET https://httpbin.org/digest-auth/realm/user/passwd
Authorization: Digest {{username}} {{password}}
```

### Authorization by token, part 1. Retrieve and save token.
```
POST https://httpbin.org/post
Content-Type: application/json

{
  "token": "my-secret-token"
}

> {% client.global.set("auth_token", response.body.json.token); %}
```

### Authorization by token, part 2. Use token to authorize.
```
GET https://httpbin.org/headers
Authorization: Bearer {{auth_token}}
```

# 总结
如果你正在使用VSCode或者IntelliJ IDE一定要使用这两款优秀的插件，让你的工作方便省事，方便发送请求，方便查看执行结构，方便保存，方便分享。