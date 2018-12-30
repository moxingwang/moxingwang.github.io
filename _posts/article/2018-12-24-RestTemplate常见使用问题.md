### POST请求url里面携带uriVariables参数该如何传

可能你在使用restTemplate的时候遇到错误'Using RestTemplate in Spring. Exception- Not enough variables available to expand',解决办法[https://stackoverflow.com/questions/21819210/using-resttemplate-in-spring-exception-not-enough-variables-available-to-expan](https://stackoverflow.com/questions/21819210/using-resttemplate-in-spring-exception-not-enough-variables-available-to-expan)具体代码如下。

```aidl
Map<String, Object> reqParamMap = new HashMap<>();
reqParamMap.put("company", "yuantong");
reqParamMap.put("number", "1242354354");

Map<String, String> parameters = new HashMap<>();
parameters.put("resultv2", "1");
parameters.put("name", "1");

reqParamMap.put("parameters", parameters);

String reqParam = JSON.toJSONString(reqParamMap);


HttpHeaders headers = new HttpHeaders();
MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
headers.setContentType(type);
headers.add("Accept", MediaType.APPLICATION_JSON.toString());

ResponseEntity<Map> resp = restTemplate.postForEntity("https://www.xxxx.com/test?schema=json&param={reqParam}", new HttpEntity<String>(headers), Map.class, reqParam);
```

### GET接口如何使用Header
```aidl
HttpHeaders headers = new HttpHeaders();
headers.add("Cookie", "AccessToken="+ accessToken);
HttpEntity entity = new HttpEntity(headers);

String loginUrl = "https://www.xxxx.com/test";

ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.GET, entity, String.class);
```