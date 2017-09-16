#Spring Boot 补充内容

#0. Maven简介 

##0.1. Maven是什么?

<img src="images/maven-logo.png" width="200" height="82"/>

Maven 是一个顶级的 Apache Software Foundation 开源项目，创建它最初是为了管理 Jakarta Turbine 项目复杂的构建过程。从那以后，不论是开源开发项目还是私有开发项目都选择 Maven 作为项目构建系统。Maven 快速地发展着，如今已是第二版，Maven 已经从针对单个复杂项目的定制构建工具成长为广泛使用的构建管理系统，其丰富的功能可以应用于大多数的软件开发场景。

##0.2. Maven配置

下载页地址:
http://maven.apache.org/download.cgi

- 安装配置:
    - 1、解压包;
    - 2、添加新的系统环境变量 **MAVEN_HOME**， 并设置其值为你安装的目录 MAVEN_HOME= D:\xxxxxx\apache-maven-3.2.2
    - 3、更新系统PATH 变量， 添加;%MAVEN_HOME%\bin;到尾部
    - 4、测试一下 mvn -v


- 修改Maven仓库位置
    - 目标:修改Maven仓库为Maven安装目录的repository目录
    - 操作:找到，Maven安装目录\conf\settings.xml这个文件。打开,添加

	<localRepository>
		D:/xxxxxx/apache-maven-3.5.0/repository
	</localRepository>

- Eclipse IDE配置
    - 1、Windows-->Prefrences,点击Maven的右边的三角符号，以展开Maven的配置界面
    - 2、之后，点击Maven下面的Installations，选择Maven的安装目录，并点击确定。
    - 3、然后在User Settings里面的Global选择刚才修改过的settings.xml文件，看看Local Repository项是否变成了刚才修改过的路径

#1. Gradle简介

<img src="images/gradle-logo.png" width="200" height="82"/>

##1.1 Gradle能做什么?

- **强大的依赖管理**
- 可以结构化构建，易于维护和理解。
- 强大的多工程构建支持
- 是第一个构建集成工具。集成了Ant, Maven的功能。
- 具有广泛的领域模型支持你的构建
- 。。。。。。

**其实在最常用到的是“依赖管理”。**

##1.2 Maven与Gradle的对比

- **Maven的优点**
    - 产品存在已久，成熟度高；
    - 企业级开发集成应用普及；
    - 依赖管理功能可精细化；
    - 包结构固定;
    - 适合各种规模软件开发中应用;
    - SCM源码管理;


- **Gradle的优点**
    - 沿用了Maven的依赖管理体系;
    - 智能化依赖管理;
    - 包结构管理灵活;
    - 适合各种规模软件开发中应用;



##1.3 Gradle配置

- 系统配置
- 项目配置

###1.3.1 系统配置

init.gradle文件

Sample项目的init.gradle配置

	allprojects{
	    repositories {
	    
	        def REPOSITORY_URL = 'http://maven.aliyun.com/nexus/content/groups/public/'
	        all { ArtifactRepository repo ->
	            if(repo instanceof MavenArtifactRepository){
	                def url = repo.url.toString()
	                if (url.startsWith('https://repo1.maven.org/maven2') || url.startsWith('https://jcenter.bintray.com/')) {
	                    project.logger.lifecycle "Repository ${repo.url} replaced by $REPOSITORY_URL."
	                    remove repo
	                }
	            }
	        }
	        
	        maven {
	            url REPOSITORY_URL
	        }
	    }
	}

建议修改为:

	allprojects{
		
		buildscript {
			//gradle脚本自身需要使用的资源仓库
			repositories {
				mavenLocal()//Maven本地仓
	    		maven {url "http://172.16.96.71:8081/nexus/content/groups/public/"}//指定仓库
				mavenCentral()//Maven中央仓
			}
		}
		//
	    repositories {
	    	mavenLocal()//Maven本地仓
	    	maven {url "http://172.16.96.71:8081/nexus/content/groups/public/"}//指定仓库
			mavenCentral()//Maven中央仓
	    }
	}

###1.3.2 项目配置

build.gradle文件的内容

	buildscript {
		ext {
			springBootVersion = '1.5.3.RELEASE'
		}
		//gradle脚本自身需要使用的资源
		repositories {
			mavenLocal()
			maven{url "http://192.168.0.250:8081/nexus/content/groups/public/"}
			mavenCentral()
		}
		//依赖
		dependencies {
			//classpath声明只能在buildscript代码块中的dependencies里使用
			classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
		}
	}
	
	apply plugin: 'java'
	apply plugin: 'eclipse-wtp'
	apply plugin: 'org.springframework.boot'
	apply plugin: 'war'
	
	version = '1.0.0'
	sourceCompatibility = 1.8
	
	//项目自身需要的资源
	repositories {
		mavenLocal()
		maven{url "http://192.168.0.250:8081/nexus/content/groups/public/"}
		mavenCentral()
	}
	
	configurations {
		providedRuntime
	}
	
	dependencies {
		//Base Dependencies
		compile('org.springframework.boot:spring-boot-starter-web')
		compile('org.springframework.boot:spring-boot-starter-data-jpa')
		compile('org.springframework.boot:spring-boot-starter-security')
		compile('org.springframework.boot:spring-boot-starter-thymeleaf')
		compile('org.springframework.boot:spring-boot-starter-log4j')
		compile('org.springframework.boot:spring-boot-starter-logging')
		
		//Hibernate ORM
		compile group: 'org.hibernate', name: 'hibernate-core', version: '5.2.10.Final'
		compile group: 'org.hibernate', name: 'hibernate-entitymanager', version: '5.2.10.Final'
		
		//Web Jar
		compile group: 'org.webjars', name: 'webjars-locator', version: '0.32-1'	
		compile group: 'org.webjars', name: 'jquery', version: '1.12.4'
		compile group: 'org.webjars', name: 'bootstrap', version: '3.3.7-1'
		compile group: 'org.webjars', name: 'font-awesome', version: '4.7.0'
		
		//DataBase Driver
		compile group: 'com.microsoft.sqlserver', name: 'sqljdbc4', version: '4.0'
		compile group: 'com.oracle', name: 'ojdbc14', version: '10.2.0.1.0'
		compile group: 'mysql', name: 'mysql-connector-java', version: '5.1.21'
		
		//Connection Pooling
		compile group: 'com.alibaba', name: 'druid', version: '1.0.18'
		compile group: 'com.alibaba.druid', name: 'druid-wrapper', version: '0.2.9'
		
		//Log4j
		//compile group: 'org.slf4j', name: 'log4j-over-slf4j', version: '1.7.25'
		//compile group: 'log4j', name: 'log4j', version: '1.2.17'
		
		//Run
		providedRuntime('org.springframework.boot:spring-boot-starter-tomcat')
		testCompile('org.springframework.boot:spring-boot-starter-test')
	}


###1.3.3 常见问题和处理技巧:

- 第一次 **创建** 或者 **导入** 项目，为什么会那么慢呢？

> **答:** 第一次调用到Eclipse Buildship插件时，必须去下载Gradle程序包(大概67MB)。<br/>
然后再下载依赖包，最后build项目。<br />

- 下载依赖包总是好慢。

> **答:** 下载慢的主要原因是 **资源** 没找好，建议选择 **国内资源** 或 **响应快的资源** 。
>> **阿里仓库:** [http://maven.aliyun.com/nexus/content/groups/public/](http://maven.aliyun.com/nexus/content/groups/public/)<br/>
>> **MVNrepository:** [http://central.maven.org/maven2/](http://central.maven.org/maven2/)<br/>
>> **。。。。。。**

###1.3.4 开发环境部署

- 先下载好[gradle-3.4.1-bin.zip](https://gradle.org/releases)
- 添加 **GRADLE_USER_HOME**=D:/xxx/xxx/.gradle 系统变量
- 安装 Buildship插件
- 创建一个Gradle项目,
- .gradle目录出现后，将Gradle工具解压到特定目录中。
- 在特定目录中加入init.gradle配置文件。
- 再测试新建一个Gradle项目。

##1.4 私仓加速

####1.4.1 为什么要使用私仓?

- 需要外网的支持如果公司 **不能上外网** 的话则不能从中央仓库下载所需jar包;
- **公司网速慢** 的时候也会影响项目构建的速度;
- 公司自开发的jar包不想放到中央仓库管理，可以上传到私有仓库中，供公司内部人员使用;
- 方便 **查找** jar包和 **下载** jar包;

####1.4.2 Nexus Repository Manager

在这里主要介绍的是Sonatype.org 出品的开源私服仓库 Nexus Repository Manager ，该软件可支持Maven、Gradle等主流构建管理软件的构建上传和下载管理。

<img src="images/repository-nexus.jpg" width="400" height="300"/>

Nexus OSS 下载地址:[https://www.sonatype.com/download-oss-sonatype](https://www.sonatype.com/download-oss-sonatype)

>Nexus Repository Manager OSS 3.x

<img src="images/nexus-oss3.png" width="630" height="450"/>



>Nexus Repository Manager OSS 2.x

<img src="images/nexus-oss2.png" width="630" height="450"/>


####1.4.3 搭建私仓

公司内容服务器上已部署的私仓:

管理界面地址:[http://172.16.96.54:8081/](http://172.16.96.54:8081/)
公用仓库地址:[http://172.16.96.54:8081/repository/maven-public/](http://172.16.96.54:8081/repository/maven-public/)

管理员:admin/admin123

####1.4.4 增加资源

**国内加速资源**

新增 http://maven.aliyun.com/nexus 资源,[http://maven.aliyun.com/nexus/content/groups/public](http://maven.aliyun.com/nexus/content/groups/public)

**特殊包资源**

新增 http://nexus.bsdn.org 资源,[http://nexus.bsdn.org/content/groups/public/](http://nexus.bsdn.org/content/groups/public/)


#2. 再谈Spring Boot

##2.1 Spring Boot 四大神器

- Auto Configuration(自动配置)
- Starters(启动器)
- CLI(命令行工具，可用于快速搭建基于spring的原型)
- Actuator(应用实时监控)

###2.1.1 Auto Configuration

<img src="images/autoconfigure-how-to-work.png" width="900" height="1200"/>

Spring Boot此框架的神奇之处在于 **@EnableAutoConfiguration**

###2.1.2 Starters



###2.1.3 CLI

###2.1.4 Actuator

##2.2 @SpringBootApplication

App Run

	@SpringBootApplication
	public class SampleApplication {
	
		public static void main(String[] args) {
			SpringApplication.run(SampleApplication.class, args);
		}
	}

Context Run

	public class ServletInitializer extends SpringBootServletInitializer {
	
		@Override
		protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
			return application.sources(SampleApplication.class);
		}
	
	}


Both Run

	@SpringBootApplication
	public class Application extends SpringBootServletInitializer {
	
		public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
		}
	}

也就是说可以将 **应用启动** 和 **容器启动** 写在统一个类作为通用启动类。
	
	

##2.3 application.properties

###2.3.1 官方配置文件属性参考
[Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

###2.3.2 常用配置属性

	#DATASOURCE (数据源配置)
	spring.datasource.name= # name of the data source
	spring.datasource.initialize=true # populate using data.sql
	spring.datasource.schema= # a schema (DDL) script resource reference
	spring.datasource.data= # a data (DML) script resource reference
	spring.datasource.platform= # the platform to use in the schema resource (schema-${platform}.sql)
	spring.datasource.continueOnError=false # continue even if can't be initialized
	spring.datasource.separator=; # statement separator in SQL initialization scripts
	spring.datasource.driverClassName= # JDBC Settings...
	spring.datasource.url=
	spring.datasource.username=
	spring.datasource.password=
	spring.datasource.max-active=100 # Advanced configuration...
	spring.datasource.max-idle=8
	spring.datasource.min-idle=8
	spring.datasource.initial-size=10
	spring.datasource.validation-query=
	spring.datasource.test-on-borrow=false
	spring.datasource.test-on-return=false
	spring.datasource.test-while-idle=
	spring.datasource.time-between-eviction-runs-millis=
	spring.datasource.min-evictable-idle-time-millis=
	spring.datasource.max-wait-millis=

	#SPRING MVC (HttpMapperProperties)
	http.mappers.json-pretty-print=false # pretty print JSON
	http.mappers.json-sort-keys=false # sort keys
	spring.mvc.locale= # set fixed locale, e.g. enUK
	spring.mvc.date-format= # set fixed date format, e.g. dd/MM/yyyy
	spring.mvc.message-codes-resolver-format= # PREFIXERRORCODE / POSTFIXERROR_CODE
	spring.view.prefix= # MVC view prefix
	spring.view.suffix= # ... and suffix
	spring.resources.cache-period= # cache timeouts in headers sent to browser
	spring.resources.add-mappings=true # if default mappings should be added

	#JPA (JpaBaseConfiguration, HibernateJpaAutoConfiguration)
	spring.jpa.properties.*= # properties to set on the JPA connection
	spring.jpa.openInView=true
	spring.jpa.show-sql=true
	spring.jpa.database-platform=
	spring.jpa.database=
	spring.jpa.generate-ddl=false # ignored by Hibernate, might be useful for other vendors
	spring.jpa.hibernate.naming-strategy= # naming classname
	spring.jpa.hibernate.ddl-auto= # defaults to create-drop for embedded dbs
	spring.data.jpa.repositories.enabled=true # if spring data repository support is enabled

	#THYMELEAF (ThymeleafAutoConfiguration)
	spring.thymeleaf.prefix=classpath:/templates/
	spring.thymeleaf.suffix=.html
	spring.thymeleaf.mode=HTML5
	spring.thymeleaf.encoding=UTF-8
	spring.thymeleaf.content-type=text/html # ;charset=<encoding> is added
	spring.thymeleaf.cache=true # set to false for hot refresh

	#LOGGING
	logging.path=/var/logs
	logging.file=myapp.log
	logging.config=

	#EMBEDDED SERVER CONFIGURATION (ServerProperties)
	server.port=8080
	server.address= # bind to a specific NIC
	server.session-timeout= # session timeout in seconds
	server.context-path= # the context path, defaults to '/'
	server.servlet-path= # the servlet path, defaults to '/'
	server.tomcat.access-log-pattern= # log pattern of the access log
	server.tomcat.access-log-enabled=false # is access logging enabled
	server.tomcat.protocol-header=x-forwarded-proto # ssl forward headers
	server.tomcat.remote-ip-header=x-forwarded-for
	server.tomcat.basedir=/tmp # base dir (usually not needed, defaults to tmp)
	server.tomcat.background-processor-delay=30; # in seconds
	server.tomcat.max-threads = 0 # number of threads in protocol handler
	server.tomcat.uri-encoding = UTF-8 # character encoding to use for URL decoding

	#AOP
	spring.aop.auto=
	spring.aop.proxy-target-class=


###2.3.3 多环境配置

在Spring Boot 配置选项中有一个特殊参数#spring.profiles.active ,通过这个参数可以指定有效的配置文件，利用该参数可以选用不同配置文件。

实用案例:
当我们项目有多个环境时，包括:开发环境(dev)、测试环境(test)、生产环境(pro)。

命名各个环境的配置文件为:
- 开发环境配置文件 : application-dev.properties 
- 测试环境配置文件 : application-test.properties 
- 生产环境配置文件 : application-pro.properties 

在主配置文件件 application.properties 中，先配置好可公用部分的配置属性，然后加入spring.profiles.active 属性指定再加载的配置文件。

注意：
在指定了spring.profiles.active属性后，指定的配置文件的属性会覆盖默认配置文件的属性(Spring遵循“约定大于配置”原则)。



##2.4 Repository 和 Service 层

###2.4.1 Repository基类接口

	@NoRepositoryBean
	public interface BaseRepository<T, PK extends Serializable> extends JpaRepository<T, PK> {
	
	}

###2.4.2 BaseService基类接口

	public interface BaseService<T, PK extends Serializable> {
	
		Page<T> findAll(Pageable pageable);
		T save(T entity);
		T findOne(PK id);
		boolean exists(PK id);
		long count();
		void delete(PK id);
		void delete(T entity);
		void delete(Iterable<T> entities);
		void deleteAll();
		List<T> findAll();
		List<T> findAll(Sort sort);
		List<T> findAll(Iterable<PK> ids);
		List<T> save(Iterable<T> entities);
		void flush();
		T saveAndFlush(T entity);
		void deleteInBatch(Iterable<T> entities);
		void deleteAllInBatch();
		T getOne(PK id);
	
	}

###2.4.3 BaseServiceImpl基类接口方法实现类

	public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {
	
		@Autowired
		protected BaseRepository<T, PK> repository;
	
		@Override
		public Page<T> findAll(Pageable pageable) {
			return repository.findAll(pageable);
		}
	
		@Override
		public T save(T entity) {
			return repository.save(entity);
		}
	
		@Override
		public T findOne(PK id) {
			return repository.findOne(id);
		}
	
		@Override
		public boolean exists(PK id) {
			return repository.exists(id);
		}
	
		@Override
		public long count() {
			return repository.count();
		}
	
		@Override
		public void delete(PK id) {
	
			repository.delete(id);
		}
	
		@Override
		public void delete(T entity) {
			repository.delete(entity);
	
		}
	
		@Override
		public void delete(Iterable<T> entities) {
			repository.delete(entities);
		}
	
		@Override
		public void deleteAll() {
			repository.deleteAll();
	
		}
	
		@Override
		public List<T> findAll() {
			return repository.findAll();
		}
	
		@Override
		public List<T> findAll(Sort sort) {
	
			return repository.findAll(sort);
		}
	
		@Override
		public List<T> findAll(Iterable<PK> ids) {
	
			return repository.findAll(ids);
		}
	
		@Override
		public List<T> save(Iterable<T> entities) {
	
			return repository.save(entities);
		}
	
		@Override
		public void flush() {
			repository.flush();
		}
	
		@Override
		public T saveAndFlush(T entity) {
	
			return repository.saveAndFlush(entity);
		}
	
		@Override
		public void deleteInBatch(Iterable<T> entities) {
	
			repository.deleteInBatch(entities);
		}
	
		@Override
		public void deleteAllInBatch() {
	
			repository.deleteAllInBatch();
		}
	
		@Override
		public T getOne(PK id) {
	
			return repository.getOne(id);
		}
	
	}

#3. 参考资料

- [Gradle Maven 依赖管理](http://www.cnblogs.com/duwei/p/4704798.html)
