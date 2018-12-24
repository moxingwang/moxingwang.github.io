java开发模拟测试框架。

## pom
````
		<dependency>
			<groupId>org.mockito</groupId>
			<artifactId>mockito-all</artifactId>
		</dependency>
````

## test
````
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MockServiceTest.class,
        PayCallbackMockTest.class
})
public class SuiteMock extends AbstractTransactionalJUnit4SpringContextTests {

}
````

````
@RunWith(MockitoJUnitRunner.class)
public class BaseMock {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

}
````

````
public class PayCallbackServiceTest extends BaseMock {

    @InjectMocks
    IPayCallbackService payCallbackService = new PayCallbackService();

    @Mock
    private IQueryOrderService queryOrderService ;
    @Mock
    private PayCallBackMapper payCallBackMapper;
    @Mock
    private IPromotionService promotionService;
    @Mock
    private IQueryPaymentService queryPaymentService;
    @Mock OrderMapper orderMapper;
    @Mock
    IOrderService orderService;
    @Mock
    ProductShopGoodsInventoryService productShopGoodsInventoryService;
    @Mock
    OrderItemMapper orderItemMapper;

    Order order = new Order();
    Order originalOrder = new Order();
    //准备参数
    Set<String> mockSerialNumber = new HashSet<>();
    String orderSerialNumber = "SO2016111700003196";
    List<PaymentLine> paymentLinesQuery = new LinkedList<>();
    PaymentLine paymentLine = new PaymentLine();
    List<Promotion> promotions = new LinkedList<>();
    List<OrderItem> orderItems = new LinkedList<>();

    @Before
    public void init(){

        order.setId(11L);
        order.setOrderType(OrderType.SALE);
        order.setPayableAmount(new BigDecimal(100));
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setChannel(new Long(1));
        order.setPayableAmount(new BigDecimal(1));

        originalOrder.setMerchantId("10001");
        originalOrder.setPurchaserId("10001");

        mockSerialNumber.add(orderSerialNumber);

        paymentLine.setId(11L);
        paymentLinesQuery.add(paymentLine);
        paymentLine.setPaymentLineAmount(new BigDecimal(100));

        Promotion promotion = new Promotion();
        promotion.setPromotionType(PromotionType.PROMOTION_SALE_JKFQ);
        promotions.add(promotion);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(new BigDecimal(1));
        orderItems.add(orderItem);
    }


    @Test()
    public void testUnpaidAmount() {

        when(queryOrderService.queryOrderWithItemsBySerialNumber(anyString())).thenReturn(order);
        when(queryPaymentService.queryPaymentLines(anyLong())).thenReturn(paymentLinesQuery);
        when(payCallBackMapper.queryPromotions(order.getId())).thenReturn(promotions);
        when(promotionService.isPromotionAvailableForOrder(anySet())).thenReturn(true);
        when(promotionService.isPromotionAvailableForOrder(anySet())).thenReturn(false);
        when(orderMapper.updateByPrimaryKey((Order) anyObject())).thenReturn(1);
        when(orderItemMapper.getItemsByOrderId(anyLong())).thenReturn(orderItems);


        try{
            List<UnPaidVo> unPaidVos = payCallbackService.queryUnpaidAmounts(mockSerialNumber);
        }catch (Exception e){
            //test 195
        }


        {
            order.setId(2L);
            order.setOrderStatus(OrderStatus.UNPAID);
            when(payCallBackMapper.queryPromotions(order.getId())).thenReturn(null);
            when(productShopGoodsInventoryService.addInventories(anyString(),anyInt(),anyString(),anyString())).thenReturn(new ServiceResult<Object>(true,""));
            List<UnPaidVo> unPaidVos = payCallbackService.queryUnpaidAmounts(mockSerialNumber);
        }

        {
            try{
                paymentLine.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setId(2L);
                order.setOrderStatus(OrderStatus.UNPAID);
                when(payCallBackMapper.queryPromotions(order.getId())).thenReturn(null);
                when(productShopGoodsInventoryService.addInventories(anyString(),anyInt(),anyString(),anyString())).thenReturn(new ServiceResult<Object>(true,""));
                List<UnPaidVo> unPaidVos = payCallbackService.queryUnpaidAmounts(mockSerialNumber);
            }catch (Exception e){

            }
        }

        {
            try{
                paymentLine.setPaymentLineAmount(new BigDecimal(100));

                paymentLine.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setId(2L);
                order.setOrderStatus(OrderStatus.UNPAID);
                when(payCallBackMapper.queryPromotions(order.getId())).thenReturn(null);
                when(productShopGoodsInventoryService.addInventories(anyString(),anyInt(),anyString(),anyString())).thenReturn(new ServiceResult<Object>(true,""));
                List<UnPaidVo> unPaidVos = payCallbackService.queryUnpaidAmounts(mockSerialNumber);
            }catch (Exception e){}
        }


    }

}
````