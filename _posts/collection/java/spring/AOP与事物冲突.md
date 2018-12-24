1. 冲突
````
AOP 与Transactional 冲突后使用@Order（value =2）解决
<aop:aspectj-autoproxy/>
````
2. demo
````
/**
*@title:
*CreatedbyMoXingwangon2016-09-0321:24.
*/
@Aspect
@Order(value=2)
@Component
publicclassPayErrorLogAop{

privatestaticLoggerlogger=Logger.getLogger(PayErrorLogAop.class);

privatestaticStringgetHostInfo(){
try{
returnPlantformUtils.getLocalIp()+":"+PlantformUtils.getPort();
}catch(Exceptione){
return"localhostdefault";
}
}

@Autowired
privatePayErrorLogMapperpayErrorLogMapper;

@Pointcut("execution(*com.chinaredstar.ordercenter.dubboservice.pay.PayCallbackService.payCallback(..))")
//@Pointcut("execution(*com.chinaredstar.ordercenter.serviceinterface.pay.IPayCallbackService.payCallback(..))")
privatevoidaspectjMethod(){};

@AfterThrowing(value="aspectjMethod()",throwing="ex")
publicvoidafterThrowingAdvice(JoinPointjoinPoint,Exceptionex){
try{
Stringremark=ex.getMessage();
if(remark.length()>500){
remark=remark.substring(0,500);
}
intresult=payErrorLogMapper.insert(getHostInfo(),remark);
logger.debug(result);
}catch(Exceptione){
if(logger.isDebugEnabled()){
logger.debug(e.getMessage(),e);
}
}
}


}
````