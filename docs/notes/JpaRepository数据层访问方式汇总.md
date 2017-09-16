

AppleFramework在数据访问控制层采用了Spring Data作为这一层的解决方案，下面就对Spring Data相关知识作一个较为详细的描述。 

1.Spring Data所解决的问题 
Spring Data :提供了一整套数据访问层(DAO)的解决方案，致力于减少数据访问层(DAO)的开发量。它使用一个叫作Repository的接口类为基础，它被定义为访问底层数据模型的超级接口。而对于某种具体的数据访问操作，则在其子接口中定义。 
public interface Repository<T, ID extends Serializable> { 
} 
所有继承这个接口的interface都被spring所管理，此接口作为标识接口，功能就是用来控制domain模型的。 
Spring Data可以让我们只定义接口，只要遵循spring data的规范，就无需写实现类。 

2.什么是Repository？ 
2.1 Repository（资源库）：通过用来访问领域对象的一个类似集合的接口，在领域与数据映射层之间进行协调。这个叫法就类似于我们通常所说的DAO，在这里，我们就按照这一习惯把数据访问层叫Repository 
Spring Data给我们提供几个Repository，基础的Repository提供了最基本的数据访问功能，其几个子接口则扩展了一些功能。它们的继承关系如下： 
Repository： 仅仅是一个标识，表明任何继承它的均为仓库接口类，方便Spring自动扫描识别 
CrudRepository： 继承Repository，实现了一组CRUD相关的方法 
PagingAndSortingRepository： 继承CrudRepository，实现了一组分页排序相关的方法 
JpaRepository： 继承PagingAndSortingRepository，实现一组JPA规范相关的方法 
JpaSpecificationExecutor： 比较特殊，不属于Repository体系，实现一组JPA Criteria查询相关的方法 
我们自己定义的XxxxRepository需要继承JpaRepository，这样我们的XxxxRepository接口就具备了通用的数据访问控制层的能力。 
2.2 JpaRepository 所提供的基本功能 
2.2.1 CrudRepository<T, ID extends Serializable>： 
这个接口提供了最基本的对实体类的添删改查操作 
T save(T entity);//保存单个实体 
Iterable<T> save(Iterable<? extends T> entities);//保存集合 
T findOne(ID id);//根据id查找实体 
boolean exists(ID id);//根据id判断实体是否存在 
Iterable<T> findAll();//查询所有实体,不用或慎用! 
long count();//查询实体数量 
void delete(ID id);//根据Id删除实体 
void delete(T entity);//删除一个实体 
void delete(Iterable<? extends T> entities);//删除一个实体的集合 
void deleteAll();//删除所有实体,不用或慎用! 
2.2.2 PagingAndSortingRepository<T, ID extends Serializable> 
这个接口提供了分页与排序功能 
Iterable<T> findAll(Sort sort);//排序 
Page<T> findAll(Pageable pageable);//分页查询（含排序功能） 
2.2.3 JpaRepository<T, ID extends Serializable> 
这个接口提供了JPA的相关功能 
List<T> findAll();//查找所有实体 
List<T> findAll(Sort sort);//排序 查找所有实体 
List<T> save(Iterable<? extends T> entities);//保存集合 
void flush();//执行缓存与数据库同步 
T saveAndFlush(T entity);//强制执行持久化 
void deleteInBatch(Iterable<T> entities);//删除一个实体集合 
3.Spring data 查询 
3.1 简单条件查询:查询某一个实体类或者集合 
按照Spring data 定义的规则，查询方法以find|read|get开头 
涉及条件查询时，条件的属性用条件关键字连接，要注意的是：条件属性以首字母大写其余字母小写为规定。 
例如：定义一个Entity实体类 
class User｛ 
private String firstname; 
private String lastname; 
｝ 
使用And条件连接时，应这样写： 
findByLastnameAndFirstname(String lastname,String firstname); 
条件的属性名称与个数要与参数的位置与个数一一对应 

3.2 使用JPA NamedQueries （标准规范实现） 
这种查询是标准的JPA规范所定义的，直接声明在Entity实体类上，调用时采用在接口中定义与命名查询对应的method，由Spring Data根据方法名自动完成命名查询的寻找。 
（1）在Entity实体类上使用@NamedQuery注解直接声明命名查询。 
@Entity 
@NamedQuery(name = "User.findByEmailAddress", 
  query = "select u from User u where u.emailAddress = ?1") 
public class User { 

} 
注：定义多个时使用下面的注解 
@NamedQueries(value = { 
@NamedQuery(name = User.QUERY_FIND_BY_LOGIN, 
query = "select u from User u where u." + User.PROP_LOGIN 
+ " = :username"), 
@NamedQuery(name = "getUsernamePasswordToken", 
query = "select new com.aceona.weibo.vo.TokenBO(u.username,u.password) from User u where u." + User.PROP_LOGIN 
    + " = :username")}) 
（2）在interface中定义与(1)对应的方法 
public interface UserRepository extends JpaRepository<User, Long> { 

  List<User> findByLastname(String lastname); 

  User findByEmailAddress(String emailAddress); 
} 
3.3 使用@Query自定义查询（Spring Data提供的） 
这种查询可以声明在Repository方法中，摆脱像命名查询那样的约束，将查询直接在相应的接口方法中声明，结构更为清晰，这是Spring data的特有实现。 
例如： 
public interface UserRepository extends JpaRepository<User, Long> { 

  @Query("select u from User u where u.emailAddress = ?1") 
  User findByEmailAddress(String emailAddress); 
} 
3.4 @Query与 @Modifying 执行更新操作 
这两个annotation一起声明，可定义个性化更新操作，例如只涉及某些字段更新时最为常用，示例如下： 
@Modifying 
@Query("update User u set u.firstname = ?1 where u.lastname = ?2") 
int setFixedFirstnameFor(String firstname, String lastname); 

3.5 索引参数与命名参数 
（1）索引参数如下所示，索引值从1开始，查询中 ”?X” 个数需要与方法定义的参数个数相一致，并且顺序也要一致 
@Modifying 
@Query("update User u set u.firstname = ?1 where u.lastname = ?2") 
int setFixedFirstnameFor(String firstname, String lastname); 

（2）命名参数（推荐使用这种方式） 
可以定义好参数名，赋值时采用@Param("参数名")，而不用管顺序。如下所示： 
public interface UserRepository extends JpaRepository<User, Long> { 

  @Query("select u from User u where u.firstname = :firstname or u.lastname = :lastname") 
  User findByLastnameOrFirstname(@Param("lastname") String lastname, 
 @Param("firstname") String firstname); 
} 

4. Transactionality（事务） 
4.1 操作单个对象的事务 
Spring Data提供了默认的事务处理方式，即所有的查询均声明为只读事务，对于持久化，更新与删除对象声明为有事务。 
参见org.springframework.data.jpa.repository.support.SimpleJpaRepository<T, ID> 
@org.springframework.stereotype.Repository 
@Transactional(readOnly = true) 
public class SimpleJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, 
JpaSpecificationExecutor<T> { 
…… 
@Transactional 
public void delete(ID id) { 

delete(findOne(id)); 
} 
…… 
} 
对于自定义的方法，如需改变spring data提供的事务默认方式，可以在方法上注解@Transactional声明 

4.2 涉及多个Repository的事务处理 
进行多个Repository操作时，也应该使它们在同一个事务中处理，按照分层架构的思想，这部分属于业务逻辑层，因此，需要在Service层实现对多个Repository的调用，并在相应的方法上声明事务。 
例如： 
@Service(“userManagement”) 
class UserManagementImpl implements UserManagement { 

  private final UserRepository userRepository; 
  private final RoleRepository roleRepository; 

  @Autowired 
  public UserManagementImpl(UserRepository userRepository, 
    RoleRepository roleRepository) { 
    this.userRepository = userRepository; 
    this.roleRepository = roleRepository; 
  } 

  @Transactional 
  public void addRoleToAllUsers(String roleName) { 

    Role role = roleRepository.findByName(roleName); 

    for (User user : userRepository.readAll()) { 
      user.addRole(role); 
      userRepository.save(user); 
    } 
} 

5.关于DAO层的规范 
5.1对于不需要写实现类的情况：定义XxxxRepository 接口并继承JpaRepository接口，如果Spring data所提供的默认接口方法不够用，可以使用@Query在其中定义个性化的接口方法。 
5.2对于需要写实现类的情况：定义XxxxDao 接口并继承com.aceona.appleframework.persistent.data.GenericDao 
书写XxxxDaoImpl实现类并继承com.aceona.appleframework.persistent.data.GenericJpaDao，同时实现XxxxDao接口中的方法 

在Service层调用XxxxRepository接口与XxxxDao接口完成相应的业务逻辑 

关于配置：

spring.xml
Java代码  收藏代码

    <?xml version="1.0" encoding="UTF-8"?>  
    <beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"  
xmlns:jdbc="http://www.springframework.org/schema/jdbc" xmlns:jee="http://www.springframework.org/schema/jee"  
xmlns:tx="http://www.springframework.org/schema/tx" xmlns:jpa="http://www.springframework.org/schema/data/jpa"  
xsi:schemaLocation="  
    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd  
    http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.2.xsd  
    http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.2.xsd  
    http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-3.2.xsd  
    http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.2.xsd  
    http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa-1.3.xsd"  
default-lazy-init="true">  
      
<description>Spring公共配置</description>  
      
<context:component-scan base-package="com.scu.book.shop">  
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />  
    <context:exclude-filter type="annotation" expression="org.springframework.web.bind.annotation.ControllerAdvice" />  
</context:component-scan>  
      
<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">  
    <property name="dataSource" ref="dataSource" />  
    <property name="jpaVendorAdapter" ref="hibernateJpaVendorAdapter" />  
    <property name="packagesToScan" value="com.scu.book.shop" />  
    <property name="jpaProperties">  
<props>  
    <prop key="hibernate.dialect">${hibernate.dialect}</prop>  
    <prop key="hibernate.show_sql">${hibernate.show_sql}</prop>  
    <prop key="hibernate.format_sql">${hibernate.format_sql}</prop>  
    <prop key="hibernate.hbm2ddl.auto">${hibernate.hbm2ddl.auto}</prop>  
    <prop key="hibernate.ejb.naming_strategy">org.hibernate.cfg.ImprovedNamingStrategy</prop>  
</props>  
    </property>  
</bean>  
      
<bean id="hibernateJpaVendorAdapter" class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter" />  
      
<jpa:repositories base-package="com.scu.book.shop" transaction-manager-ref="transactionManager" entity-manager-factory-ref="entityManagerFactory" />  
      
<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">  
    <property name="entityManagerFactory" ref="entityManagerFactory" />  
</bean>  
      
<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true" />  
      
    <!--   
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean" />  
     -->  
       
<context:property-placeholder ignore-unresolvable="true"  
    location="classpath*:/property/jdbc.properties,  
      classpath*:/property/hibernate.properties,  
      classpath*:/property/image.properties" />  
      
<bean id="dataSource" class="org.apache.tomcat.jdbc.pool.DataSource"  
    destroy-method="close">  
      
    <property name="driverClassName" value="${jdbc.driver}" />  
    <property name="url" value="${jdbc.url}" />  
    <property name="username" value="${jdbc.username}" />  
    <property name="password" value="${jdbc.password}" />  
      
    <property name="maxActive" value="${jdbc.pool.maxActive}" />  
    <property name="maxIdle" value="${jdbc.pool.maxIdle}" />  
    <property name="testWhileIdle" value="true" />  
    <property name="testOnBorrow" value="true" />  
    <property name="testOnReturn" value="false" />  
    <property name="validationQuery" value="select 1" />  
    <property name="removeAbandoned" value="true" />  
      
    <property name="timeBetweenEvictionRunsMillis" value="900000" />  
    <property name="minEvictableIdleTimeMillis" value="1800000" />  
</bean>  
      
    </beans>  

 web.xml:
Java代码  收藏代码

    <?xml version="1.0" encoding="UTF-8"?>  
    <web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee   
http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">  
      
<display-name>book shop</display-name>  
      
<listener>  
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>  
</listener>  
<context-param>  
    <param-name>contextConfigLocation</param-name>  
    <param-value>classpath:spring/*.xml</param-value>  
</context-param>  
      
<filter>  
    <filter-name>encodingFilter</filter-name>  
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>  
    <init-param>  
<param-name>encoding</param-name>  
<param-value>UTF-8</param-value>  
    </init-param>  
</filter>  
<filter-mapping>  
    <filter-name>encodingFilter</filter-name>  
    <url-pattern>/*</url-pattern>  
</filter-mapping>  
      
<servlet>  
    <servlet-name>springServlet</servlet-name>  
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>  
    <init-param>  
<param-name>contextConfigLocation</param-name>  
<param-value>classpath:mvc/*.xml</param-value>  
    </init-param>  
    <load-on-startup>1</load-on-startup>  
</servlet>  
<servlet-mapping>  
    <servlet-name>springServlet</servlet-name>  
    <url-pattern>/</url-pattern>  
</servlet-mapping>  
      
<filter>  
    <filter-name>openEntityManagerInViewFilter</filter-name>  
    <filter-class>org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter</filter-class>  
</filter>  
<filter-mapping>  
    <filter-name>openEntityManagerInViewFilter</filter-name>  
    <url-pattern>/*</url-pattern>  
</filter-mapping>  
      
    </web-app>  

 

mvc.xml
Java代码  收藏代码

    <?xml version="1.0" encoding="UTF-8"?>  
    <beans xmlns="http://www.springframework.org/schema/beans"  
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
xmlns:context="http://www.springframework.org/schema/context"  
xmlns:mvc="http://www.springframework.org/schema/mvc"  
xsi:schemaLocation="http://www.springframework.org/schema/mvc   
    http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd  
    http://www.springframework.org/schema/beans   
    http://www.springframework.org/schema/beans/spring-beans-3.2.xsd  
    http://www.springframework.org/schema/context   
    http://www.springframework.org/schema/context/spring-context-3.2.xsd"  
    default-lazy-init="true">  
      
<context:component-scan base-package="com.scu.book.shop" use-default-filters="false">  
    <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>  
    <context:include-filter type="annotation" expression="org.springframework.web.bind.annotation.ControllerAdvice"/>  
</context:component-scan>   
  
<mvc:annotation-driven>  
    <mvc:message-converters register-defaults="true">  
<bean class="org.springframework.http.converter.StringHttpMessageConverter">  
    <constructor-arg value="UTF-8" />  
</bean>  
<bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">    
    <property name="objectMapper">  
<bean class="com.fasterxml.jackson.databind.ObjectMapper">    
    <property name="dateFormat">    
<bean class="java.text.SimpleDateFormat">    
    <constructor-arg value="yyyy-MM-dd HH:mm:ss" />     
</bean>    
    </property>    
</bean>   
    </property>    
</bean>  
    </mvc:message-converters>  
</mvc:annotation-driven>  
  
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">  
    <property name="prefix" value="/WEB-INF/views/"/>  
    <property name="suffix" value=".jsp"/>  
</bean>  
  
<mvc:default-servlet-handler/>  
<mvc:view-controller path="/" view-name="redirect:/task/list"/>  
  
    </beans>  

 这里只粘配置文件,其他地方参考源码。
 
# 参考资料:
 
 * [Spring Data JPA - JpaRepository数据层 访问方式汇总](http://perfy315.iteye.com/blog/1460226)
