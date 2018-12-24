````
packagecom.khoubyari.example.service;

importjava.util.Vector;

/**
*Createdbymon23/11/2016.
*JDK已经明确说明唤醒哪个thread是随意决定的，取决于厂商的具体实现
*Thechoiceisarbitraryandoccursatthediscretionoftheimplementation
*/
publicclassThreadWaitNotifyTest{
publicstaticvoidmain(Stringargs[]){
Vectorobj=newVector();
Threadconsumer=newThread(newConsumer(obj));
Threadconsumer2=newThread(newConsumerTwo(obj));
Threadproducter=newThread(newProducter(obj));
consumer.start();
consumer2.start();
producter.start();
}
}

/*消费者*/
classConsumerimplementsRunnable{
privateVectorobj;

publicConsumer(Vectorv){
this.obj=v;
}

publicvoidrun(){
synchronized(obj){
while(true){
try{
if(obj.size()==0){
System.out.println("Consumer开始wait"+Thread.currentThread().getName());
obj.wait();
System.out.println("Consumer等待中");
}
System.out.println("Consumer:goodshavebeentaken");
System.out.println("objsize:"+obj.size());

if(!obj.isEmpty()){
obj.remove(0);
}
obj.notify();
Thread.sleep(200);

}catch(Exceptione){
e.printStackTrace();
}
}
}
}
}


/*消费者*/
classConsumerTwoimplementsRunnable{
privateVectorobj;

publicConsumerTwo(Vectorv){
this.obj=v;
}

publicvoidrun(){
synchronized(obj){
while(true){
try{
if(obj.size()==0){
System.out.println("ConsumerTwo开始wait"+Thread.currentThread().getName());
obj.wait();
System.out.println("ConsumerTwo等待中");
}
System.out.println("ConsumerTwo:goodshavebeentaken");
System.out.println("objsize:"+obj.size());

if(!obj.isEmpty()){
obj.remove(0);
}
obj.notify();
Thread.sleep(200);

}catch(Exceptione){
e.printStackTrace();
}
}
}
}
}


/*生产者*/
classProducterimplementsRunnable{
privateVectorobj;

publicProducter(Vectorv){
this.obj=v;
}

publicvoidrun(){
synchronized(obj){
while(true){
try{
if(obj.size()!=0){
System.out.println("Producter开始wait"+Thread.currentThread().getName());
obj.wait();
System.out.println("Producter等待中");
}

obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.add(newString("apples"));
obj.notify();
System.out.println("Producter:objareready");
//Thread.sleep(500);
}catch(Exceptione){
e.printStackTrace();
}
}
}
}
}
````