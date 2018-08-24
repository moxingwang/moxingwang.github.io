# 一、Webservice实现

## 实现方式分类
### 1.  spring实现
* bean配置

````
<bean class="org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter">
        <property name="baseAddress" value="http://localhost:8088/"/>
    </bean>
````

* service

````
@Component
@WebService(serviceName="sapPushExpenseWebservice")
public class SapPushExpenseWebservice  {
    private static Logger logger = LoggerFactory.getLogger(SapPushExpenseWebservice.class);
    @WebMethod
    public Result<Object> pushExpense(@WebParam(name="expenseDTOSet") Set<String> expenseDTOSet) {
        logger.info(JSON.toJSONString(expenseDTOSet));
        return null;
    }
}
````

### 2.  spring boot实现
* Cxf boot配置

````
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-frontend-jaxws</artifactId>
    <version>3.1.6</version>
</dependency>
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http</artifactId>
    <version>3.1.6</version>
</dependency>
````

````
@Configuration
public class SapCxfConfig {

    @Bean
    public ServletRegistrationBean dispatcherServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/sap/soap/*");
    }
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public ISapPushExpenseWebservice sapPushExpenseWebservice() {
        return new SapPushExpenseWebservice();
    }
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), sapPushExpenseWebservice());
        endpoint.publish("/push");
        return endpoint;
    }

}
````

* service实现

````
@WebService
public interface ISapPushExpenseWebservice {
    @WebMethod
    Result<Object> pushExpense(@WebParam(name="expenseDTOSet")Set<SapExpenseDTO> expenseDTOSet);

}

public class SapPushExpenseWebservice implements ISapPushExpenseWebservice {

    private static Logger logger = LoggerFactory.getLogger(SapPushExpenseWebservice.class);

    @Autowired
    private ISapExpenseService sapExpenseService;

    @Override
    public Result<Object> pushExpense(Set<SapExpenseDTO> expenseDTOSet) {
        logger.info(JSON.toJSONString(expenseDTOSet));
        return sapExpenseService.pushExpense(expenseDTOSet);
    }

}

````

### 3.  dubbo实现
    * http://blog.csdn.net/u012129031/article/details/53259877

# 二、Webservice调用

## cxf客户端生产代码

* 下载地址

````
http://cxf.apache.org/
````

* 生成代码

````
远程生成
./wsdl2java -d /Users/moxingwang/Desktop/soap -client http://localhost:8080/sap/soap/user?wsdl
本地生成
./wsdl2java -p com.chinaredstar.bill.integration.demo.test -d /Users/moxingwang/Desktop/soap /Users/moxingwang/Desktop/ss.xml


带密码的
11.txt文件内容
    http://3RDSOAP2PI:1234567@172.16.3.21:50000/dir/wsdl?p=sa/a3102884ecae325ca312de6d9720e2fb
生产指令
    wsimport -d /Users/moxingwang/Desktop/soap1 -keep -verbose -Xauthfile /Users/moxingwang/Desktop/sap/11.txt ‘http://172.16.3.21:50000/dir/wsdl?p=sa/a3102884ecae325ca312de6d9720e2fb’
````

## 使用代理调用
* 依赖jar

````
 <!-- https://mvnrepository.com/artifact/org.apache.cxf/cxf-rt-frontend-jaxws -->
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-jaxws</artifactId>
            <version>3.1.9</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-transports-http</artifactId>
            <version>3.1.9</version>
        </dependency>
````

* 调用代码

````
1.第一种方式调用
public class Test {

    public static void main(String[] args) {

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://192.168.224.182:8088/sapPushExpenseWebservice?wsdl");
        try {
            List<String> list = new ArrayList<>();
            list.add("测试");

            Object[] objects = client.invoke("pushExpense", list);

            System.out.println(JSON.toJSONString(objects[0]));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
2.第二种方式调用
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(SIZFIFMECSRECEIVEOS.class);
        factory.setUsername("ZWS_ECS");
        factory.setPassword("abcd1234");
        factory.setAddress("http://172.16.3.21:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=BC_NREM&receiverParty=&receiverService=&interface=SI_ZREI_BAPI_RE_CN_OVER_OS&interfaceNamespace=http%3A%2F%2Fchinaredstar.com%2Fecc2oms");
        SIZFIFMECSRECEIVEOS port = (SIZFIFMECSRECEIVEOS) factory.create();

        ZFIFMECSRECEIVE zfifmecsreceive = new ZFIFMECSRECEIVE();
        zfifmecsreceive.setICBUTYNUM("121221");
        TABLEOFZMMFIFMCOMONOUTNEW tableofzmmfifmcomonoutnew = new TABLEOFZMMFIFMCOMONOUTNEW();

        zfifmecsreceive.setETBELNR(tableofzmmfifmcomonoutnew);

        ZFIFMECSRECEIVEResponse a = port.siZFIFMECSRECEIVEOS(zfifmecsreceive);
        
  第三种方式调用
        ZFMRECPBAPIRECNCREATE zfmrecpbapirecncreate = new ZFMRECPBAPIRECNCREATE();
  
          java.net.Authenticator myAuth = new java.net.Authenticator() {
              @Override
              protected java.net.PasswordAuthentication getPasswordAuthentication() {
                  return new java.net.PasswordAuthentication("dengwj", "1qaz2wsx".toCharArray());
              }
          };
          java.net.Authenticator.setDefault(myAuth);
          URL url = null;
          try {
              url = new URL("http://172.16.3.33:50000/dir/wsdl?p=sa/a862bcbfb95838b382e23a7a49252f1c");
          } catch (MalformedURLException e) {
              System.out.println(e.getMessage());
              e.printStackTrace();
          }
          SIZFMRECPBAPIRECNCREATEOutService service = new SIZFMRECPBAPIRECNCREATEOutService(url);
````