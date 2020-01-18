实现单个字符分词(ngram)

- `注`
> `-u elastic:uates12345` 为权限验证,没有设置权限验证的直接去掉即可.

### 初始化
> 开始操作之前先确定数据库是否已经创建测试template和index,如有直接删除.

- 删除template
```
curl -XDELETE  -u elastic:uates12345  'http://localhost:9200/_template/trade_test_0'
```

- 删除index
```
curl -XDELETE  -u elastic:uates12345    'http://localhost:9200/trade-test_1'
```

### 创建template和index
- 创建template
```
curl -u elastic:uates12345  -H "Content-Type:application/json" -XPUT http://localhost:9200/_template/trade_test_0 -d ' {
  "template": "trade-test_*",
  "order": 0,
  "settings": {
    "analysis": {
      "filter": {
        "sp_no_ngram_filter": {
          "type": "ngram",
          "min_gram": 1,
          "max_gram": 30
        }
      },
      "analyzer": {
        "sp_str_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "sp_no_ngram_filter"
          ]
        }
      }
    }
  },
  "mappings": {
    "type": {
      "_source": {
        "enabled": true
      },
      "properties": {
        "orderType": {
          "type": "integer"
        },
        "smallRegionId": {
          "type": "keyword"
        },
        "serialNumber": {
          "analyzer": "sp_str_analyzer",
          "type": "text"
        }
      }
    }
  }
}'
```

- 创建索引
```
curl -XPUT   -u elastic:uates12345   'http://localhost:9200/trade-test_1'
```

### 添加数据
- 添加数据
```
curl -u elastic:uates12345  -H "Content-Type:application/json" -XPOST http://localhost:9200/trade_test_0/type -d' {
    "orderType": "1",
    "smallRegionId": "1213",
    "serialNumber": "我爱我的祖国"
}'
```


### 分词测试
```
curl -u elastic:uates12345  -H "Content-Type:application/json" -XPOST http://localhost:9200/trade-test_1/_analyze -d' {
    "analyzer": "sp_str_analyzer",
    "text": "我爱我的祖国"
}'
```

### 查询测试
- match测试
```
curl -u elastic:uates12345  -H "Content-Type:application/json" -XPOST http://localhost:9200/trade_test_0/_search -d' {
    "query": {
        "bool": {
          "must": [
            {
              "match": {
                "serialNumber": "我的"
              }
            }
          ],
          "must_not": [],
          "should": []
        }
      },
      "from": 0,
      "size": 10,
      "sort": [],
      "aggs": {}
}'
```

- match_phrase测试
```
curl -u elastic:uates12345  -H "Content-Type:application/json" -XPOST http://localhost:9200/trade_test_0/_search -d' {
    "query": {
    "bool": {
      "must": [
        {
          "match_phrase": {
            "serialNumber": {
              "query": "爱国",
              "slop":  3
            }
          }
        }
      ],
      "must_not": [],
      "should": []
    }
  },
  "from": 0,
  "size": 10,
  "sort": [],
  "aggs": {}
}'
```



# reference
- [Elasticsearch通过ngram分词机制实现搜索推荐](https://www.jianshu.com/p/939e047af5d1)
- [深入理解 Match Phrase Query](https://www.felayman.com/articles/2017/12/11/1512989203372.html)