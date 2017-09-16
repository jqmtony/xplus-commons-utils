Java 中 Reference用法详解
=========================

这篇文章主要介绍了Java 中 Reference用法详解的相关资料,需要的朋友可以参考下
---------------------------------------------------------

#Java  Reference详解

在 jdk 1.2 及其以后，引入了强引用、软引用、弱引用、虚引用这四个概念。网上很多关于这四个概念的解释，但大多是概念性的泛泛而谈，今天我结合着代码分析了一下，首先我们先来看定义与大概解释（引用类型在包 Java.lang.ref 里）。

##1、强引用（StrongReference）

强引用不会被GC回收，并且在java.lang.ref里也没有实际的对应类型。举个例子来说：　　　
<pre>
<code>
	Object obj = new Object();
</code>
</pre>

这里的obj引用便是一个强引用，不会被GC回收。

##2、软引用（SoftReference）
软引用在JVM报告内存不足的时候才会被GC回收，否则不会回收，正是由于这种特性软引用在caching和pooling中用处广泛。软引用的用法：
<pre>
<code>
Object obj = new Object();
SoftReference<Object> softRef = new SoftReference(obj);
// 使用 softRef.get() 获取软引用所引用的对象`
Object objg = softRef.get();
</code>
</pre>


##3、弱引用（WeakReference）
当GC一但发现了弱引用对象，将会释放WeakReference所引用的对象。弱引用使用方法与软引用类似，但回收策略不同。

##4、虚引用（PhantomReference）

当GC一但发现了虚引用对象，将会将PhantomReference对象插入ReferenceQueue队列，而此时PhantomReference所指向的对象并没有被GC回收，而是要等到ReferenceQueue被你真正的处理后才会被回收。虚引用的用法：
<pre>
<code>
Object obj = new Object();
ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
PhantomReference<Object> phanRef = new PhantomReference<Object>(obj, refQueue);
// 调用phanRef.get()不管在什么情况下会一直返回null
Object objg = phanRef.get();
// 如果obj被置为null，当GC发现了虚引用，GC会将phanRef插入进我们之前创建时传入的refQueue队列
// 注意，此时phanRef所引用的obj对象，并没有被GC回收，在我们显式地调用refQueue.poll返回phanRef之后
// 当GC第二次发现虚引用，而此时JVM将phanRef插入到refQueue会插入失败，此时GC才会对obj进行回收
Reference<? extends Object> phanRefP = refQueue.poll();
</code>
</pre>


看了简单的定义之后，我们结合着代码来测试一下，强引用就不用说了，软引用的描述也很清楚，关键是 “弱引用” 与 “虚引用”。

弱引用：
	
public static void main(String[] args) throws InterruptedException {
  Object obj = new Object();
  ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
  WeakReference<Object> weakRef = new WeakReference<Object>(obj, refQueue);
  System.out.println(weakRef.get());
  System.out.println(refQueue.poll());
  obj = null;
  System.gc();
  System.out.println(weakRef.get());
  System.out.println(refQueue.poll());
}

由于System.gc()是告诉JVM这是一个执行GC的好时机，但具体执不执行由JVM决定，因此当JVM决定执行GC，得到的结果便是（事实上这段代码一般都会执行GC）：

java.lang.Object@de6ced
null
null
java.lang.ref.WeakReference@1fb8ee3

从执行结果得知，通过调用weakRef.get()我们得到了obj对象，由于没有执行GC,因此refQueue.poll()返回的null，当我们把obj = null;此时没有引用指向堆中的obj对象了，这里JVM执行了一次GC，我们通过weakRef.get()发现返回了null，而refQueue.poll()返回了WeakReference对象，因此JVM在对obj进行了回收之后，才将weakRef插入到refQueue队列中。

虚引用：
	
public static void main(String[] args) throws InterruptedException {
  Object obj = new Object();
  ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
  PhantomReference<Object> phanRef = new PhantomReference<Object>(obj, refQueue);
  System.out.println(phanRef.get());
  System.out.println(refQueue.poll());
  obj = null;
  System.gc();
  System.out.println(phanRef.get());
  System.out.println(refQueue.poll());
}

同样，当JVM执行了GC，得到的结果便是：
null
null
null
java.lang.ref.PhantomReference@1fb8ee3

从执行结果得知，我们先前说的没有错，phanRef.get()不管在什么情况下，都会返回null，而当JVM执行GC发现虚引用之后，JVM并没有回收obj，而是将PhantomReference对象插入到对应的虚引用队列refQueue中，当调用refQueue.poll()返回PhantomReference对象时，poll方法会先把PhantomReference的持有队列queue（ReferenceQueue<? super T>）置为NULL，NULL对象继承自ReferenceQueue，将enqueue(Reference paramReference)方法覆盖为return false，而此时obj再次被GC发现时，JVM再将PhantomReference插入到NULL队列中便会插入失败返回false，此时GC便会回收obj。事实上通过这段代码我们也的却看不出来obj是否被回收，但通过 PhantomReference 的javadoc注释中有一句是这样写的：

Once the garbage collector decides that an object obj is phantom-reachable, it is being enqueued on the corresponding queue, but its referent is not cleared. That is, the reference queue of the phantom reference must explicitly be processed by some application code.

翻译一下（这句话很简单，我相信很多人应该也看得懂）：

一旦GC决定一个“obj”是虚可达的，它（指PhantomReference）将会被入队到对应的队列，但是它的指代并没有被清除。也就是说，虚引用的引用队列一定要明确地被一些应用程序代码所处理。

弱引用与虚引用的用处

软引用很明显可以用来制作caching和pooling，而弱引用与虚引用呢？其实用处也很大，首先我们来看看弱引用，举个例子：
class Registry {
  private Set registeredObjects = new HashSet();
  
  public void register(Object object) {
    registeredObjects.add( object );
  }
}

所有我添加进 registeredObjects 中的object永远不会被GC回收，因为这里有个强引用保存在registeredObjects里，另一方面如果我把代码改为如下：
class Registry {
   private Set registeredObjects = new HashSet();
  
   public void register(Object object) {
     registeredObjects.add( new WeakReference(object) );
   }
 }

  现在如果GC想要回收registeredObjects中的object，便能够实现了，同样在使用HashMap如果想实现如上的效果，一种更好的实现是使用WeakHashMap。

而虚引用呢？我们先来看看javadoc的部分说明：

Phantom references are useful for implementing cleanup operations that are necessary before an object gets garbage-collected. They are sometimes more flexible than the finalize() method.

翻译一下：

虚引用在实现一个对象被回收之前必须做清理操作是很有用的。有时候，他们比finalize()方法更灵活。

很明显的，虚引用可以用来做对象被回收之前的清理工作。

 感谢阅读，希望能帮助到大家，谢谢大家对本站的支持！

原文链接：http://www.cnblogs.com/newcj/archive/2011/05/15/2046882.html
