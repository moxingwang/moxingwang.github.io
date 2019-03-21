# 使用geo_point类型

### 创建索引
```
curl -XPOST 'http://localhost:9200/weixin_discovery_market_location' -d '
{
    "aliases" : {
            "alias_weixin_discovery_market_location" : {}
        },
    "mappings": {
        "type": {
            "properties": {
               "marketName":{
                    "type": "string"
                },
              "location": {
                "type": "geo_point"
              }
            }
        }
  }
}'
```


### 添加数据
```
curl -XPUT 'http://localhost:9200/weixin_discovery_market_location/type/1009' -d '
{
  "marketName": "真北商场",
     "location": { 
        "lat": 41.12,
        "lon": -71.34
      }
}'
```

### 添加数据
```
curl -XPUT 'http://localhost:9200/weixin_discovery_market_location/type/1019' -d '
{
  "marketName": "吴中商场",
     "location": { 
        "lat": 31.12,
        "lon": -51.34
      }
}'
```


### 查询指定geo 周边最近地址
```
curl -XPOST 'http://localhost:9200/weixin_discovery_market_location/type/_search' -d '
{
  "query": {
    "filtered": {
      "filter": {
        "geo_distance": {
          "distance": "91km", 
          "location": { 
            "lat": 31.12,
            "lon": -51.34
          }
        }
      }
    }
  },
  "sort": [
    {
      "_geo_distance": {
        "location": { 
          "lat": 31.12,
            "lon": -51.34
        },
        "order":         "asc",
        "unit":          "km", 
        "distance_type": "plane" 
      }
    }
  ]

}'
```


# 使用geo_shape类型

### 创建索引
```
curl -XPOST 'http://localhost:9200/weixin_discovery' -d '
{
    "aliases" : {
            "alias_weixin_discovery" : {}
        },
    "mappings": {
        "type": {
            "properties": {
                "marketName":{
                    "type": "string"
                },
                "location": {
                        "type": "geo_shape",
                        "points_only": true
                    }
            }
        }
  }
}'
```


### 添加数据
```
curl -XPUT 'http://localhost:9200/weixin_discovery/type/1009' -d '
{
  "marketName": "真北商场",
    "location": {
        "type": "point",
        "coordinates": [121.392496,31.245827]
    }
}'
```

### 查询指定geo 1KM范围内的geo数据信息
```
curl -XPOST 'http://localhost:9200/weixin_discovery/type/_search' -d '
{
  "query": {
    "geo_shape": {
      "location": { 
        "shape": { 
          "type":   "circle", 
          "radius": "1km",
          "coordinates": [ 
            121.391337,31.244654
          ]
        }
      }
    }
  }
}'
```



# reference
* [Sorting by Distance](https://www.elastic.co/guide/en/elasticsearch/guide/current/sorting-by-distance.html)
* [百度获取经纬度](http://api.map.baidu.com/lbsapi/getpoint/index.html)
* [github elastic/elasticsearch](https://github.com/elastic/elasticsearch/issues/28744)