# 测试框架Mockito

## Service层测试

1. 引用jar 
````
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-all</artifactId>
</dependency>
````

2. TestSuite
````
/**
 * Created by m on 18/11/2016.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MockServiceTest.class,
        PayCallbackMockTest.class
})
public class SuiteMock extends AbstractTransactionalJUnit4SpringContextTests {

}
````

3. BaseMock
````
@RunWith(MockitoJUnitRunner.class)
public class BaseMock {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

}

````

4. Test
````
/**
 * Created by m on 17/11/2016.
 * mock api:        https://static.javadoc.io/org.mockito/mockito-core/2.2.17/org/mockito/Mockito.html#21
 * mock tutorials:  https://www.tutorialspoint.com/mockito/mockito_callbacks.htm
 */
public class MockServiceTest extends BaseMock {

    // injects mock or spy fields into tested object automatically.
    @InjectMocks private IQueryCanRefoundOrderService queryCanRefoundOrderService = new QueryCanRefoundOrderService();

    //mock掉IQueryCanRefoundOrderService所依赖的QueryCanRefoundOrderMapper
    @Mock private QueryCanRefoundOrderMapper queryCanRefoundOrderMapper;

    @Test()
    public void getUserName() {
        List<Order> orders = new LinkedList<Order>();
        Order order = new Order();
        order.setId(100L);
        orders.add(order);

        PageInfo pageInfo = new PageInfo();

        when(queryCanRefoundOrderMapper.findCanRefoundOrder(pageInfo)).thenReturn(orders);

        List<Order> aa = queryCanRefoundOrderService.findCanRefoundOrderService(pageInfo);

        //验证结果
        assertEquals(orders, aa);
    }

}

````

## Controller层代码
````
/**
 * Created by m on 17/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
//@WebAppConfiguration
//@ContextConfiguration(locations = { "classpath:spring/applicationContext-*.xml" })
public class MockitoControllerTest {

    private MockMvc mockMvc;

    @Mock private IQueryOrderService queryOrderService;
    @InjectMocks private BAppQueryOrderListController bAppQueryOrderListController  = new BAppQueryOrderListController();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bAppQueryOrderListController).build();
    }

    @Test
    public void testCategoryManage() throws Exception {
        // 定义方法行为
        when(queryOrderService.searchOrdersForBApp((BAppQueryOrderParamDto)anyObject(),(PageInfo) anyObject())).thenReturn(new PageVo());

        mockMvc.perform(get("/orderApi/bApp/queryOrder/list/all/").param("keyWords","RS2016111500000894").param("merchantId","8176"))
                .andExpect(status().is2xxSuccessful())//期望返回status
                .andDo(print());//输出请求和相应

    }
}
````
