使用`edge ngram`将每个单词都进行进一步的分词和切分，用切分后的`ngram`来实现前缀搜索,比如'OD5046240000014238'这样一个订单号会被分解成'O','OD','OD','OD5','OD50'...'OD5046240000014238'这样子,就可以实现前缀搜索或者搜索推荐.

不过我的业务系统中订单号`OD5046240000014238`(后四位为userid的后四位)用户常常需要使用后面几位去模糊匹配订单列表,需要的分词效果如下.

```
4238
14238
014238
0014238
...
46240000014238
046240000014238
5046240000014238
D5046240000014238
OD5046240000014238
```

## 自定义分析器
* 创建索引指定分析器
```
curl  -XPUT -H  "Content-Type:application/json"  'http://localhost:9200/myindex' -d '{
  "settings": {
    "analysis": {
      "filter": {
        "order_no_edge_ngram_filter" : {
          "type" : "edge_ngram",
          "min_gram" : 4,
          "max_gram" : 25
        }
      },
      "analyzer": {
        "order_no_analyzer" : {
          "type" : "custom",
          "tokenizer" : "standard",
          "filter" : [
            "reverse",
            "order_no_edge_ngram_filter",
            "reverse"
          ]
        }
      }
    }
  }
}
'
```

* 测试分词器

```
curl  -XPOST -H  "Content-Type:application/json"  'http://localhost:9200/myindex/_analyze' -d '{
  "text":"OD5046240000014238",
  "analyzer":"order_no_analyzer"
}
'

```

返回结果

```
{
  "tokens": [
    {
      "token": "4238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "14238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "0014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "00014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "0000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "40000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "6240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "46240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "046240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "5046240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "D5046240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "OD5046240000014238",
      "start_offset": 0,
      "end_offset": 18,
      "type": "<ALPHANUM>",
      "position": 0
    }
  ]
}

```

## reference
* [ElasticSearch 解析机制常见用法库 之 Tokenizer常用用法](https://blog.csdn.net/i6448038/article/details/51614220)
* [Elasticsearch - 指定分析器](https://blog.csdn.net/WSYW126/article/details/71080285)
* [二十四、Elasticsearch通过ngram分词机制实现搜索推荐](https://www.jianshu.com/p/939e047af5d1)