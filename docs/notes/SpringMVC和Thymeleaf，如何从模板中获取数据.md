#Thymeleaf+SpringMVC，如何从模板中获取数据
*Spring MVC and Thymeleaf: how to access data from templates*

在一个典型的SpringMVC应用中，带@Controller注解的类负责准备数据模型Map的数据和选择一个视图进行渲染。这个模型Map对视图进行完全的抽象，在使用Thymeleaf的情况下，它将是一个VariablesMap对象（即Thymeleaf模板执行上下文的属性）,使其可以用于模板重点表达式。

#1.Spring中Model的attributes属性(Spring model attributes)

SpringMVC调用可以在视图模型的执行过程中访问的数据，在Thymeleaf中相当于上下文变量。

在SpringMVC中添加一个attributes有几种不同的方法，下面有一些常见的情况：

给Model的addAttribut方法新增一个attribute

	@RequestMapping(value = "message", method = RequestMethod.GET)
	public String messages(Model model) {
	    model.addAttribute("messages", messageRepository.findAll());
	    return "message/list";
	}

在ModelAndView的返回值中添加：

	@RequestMapping(value = "message", method = RequestMethod.GET)
	public ModelAndView messages() {
	    ModelAndView mav = new ModelAndView("message/list");
	    mav.addObject("messages", messageRepository.findAll());
	    return mav;
	}

通过@ModelAttribute注解暴露出公告方法：

	@ModelAttribute("messages")
	public List<Message> messages() {
	    return messageRepository.findAll();
	}

你可能已经注意到了，在上述的将messages属性添加到model的方法中，Thymeleaf视图均可用。

在Thymeleaf中，这些model的attributes属性值均可以使用${attributeName}来访问，这个attributeName对于Thymeleaf来说就是一个messages，这是一个SpringEL表达式，总之，SpringEL表达式是一种支持在运行时查询和操作对象图的语言。

在Thymeleaf中访问model的Attributes方式如下：

	<tr th:each="message : ${messages}">
	    <td th:text="${message.id}">1</td>
	    <td><a href="#" th:text="${message.title}">Title ...</a></td>
	    <td th:text="${message.text}">Text ...</td>
	</tr>

#2.Request参数(Request parameters)

Request参数在Thymeleaf视图中可以很容易的使用，Request参数一般为从客户端到服务器传送参数，如：

 https://example.com/query?q=Thymeleaf+Is+Great!

现在假设有一个@Controller控制器，控制器中重定向的方式发送一个request参数：

	@Controller
	public class SomeController {
	    @RequestMapping("/")
	    public String redirect() {
	        return "redirect:/query?q=Thymeleaf Is Great!";
	    }
	}

访问参数q可以使用param前缀

	<p th:text="${param.q[0]}" th:unless="${param.q == null}">Test</p>

例子中有两点需要注意的地方：

    ${param.q!=null}检查set中是否有参数q
    参数是一个数组，因为它可以多值比如?q=a&r=b

还有一种访问方式是使用#httpServletRequest对象，可以直接进入javax.servlet.http.HttpServletRequest对象：

	<p th:text="${#httpServletRequest.getParameter('q')}" th:unless="${#httpServletRequest.getParameter('q') == null}">Test</p>

#3.Session属性(Session attributes)

比如为session添加了一个mySessionAttribute属性：

	@RequestMapping({"/"})
	String index(HttpSession session) {
	    session.setAttribute("mySessionAttribute", "someValue");
	    return "index";
	}

和Request参数访问方式类似，这里使用session前缀：

	<div th:text="${session.mySessionAttribute}">[...]</div>

同样的，还可以使用#httpSession方式访问，它直接进入javax.servlet.http.HttpSession对象。


#4.ServletContext属性(ServletContext attributes)

ServletContext属性可以再request和session中共享，未来访问ServletContext属性，可以使用application前缀：

	<table>
        <tr>
            <td>context中的attribute</td>
            <!-- 检索ServletContext的属性'myContextAttribute' -->
            <td th:text="${application.myContextAttribute}">42</td>
        </tr>
        <tr>
            <td>attributes数量：</td>
            <!-- 返回attributes的数量 -->
            <td th:text="${application.size()}">42</td>
        </tr>
        <tr th:each="attr : ${application.keySet()}">
            <td th:text="${attr}">javax.servlet.context.tempdir</td>
            <td th:text="${application.get(attr)}">/tmp</td>
        </tr>
    </table>

#5.Spring beans

Thymeleaf可以通过@beanName访问Spring应用上下午中注册的bean，如

	<div th:text="${@urlService.getApplicationUrl()}">...</div>

在这个例子中，@urlService就是在上下文中注册的Spring Bean:

	@Configuration
	public class MyConfiguration {
	    @Bean(name = "urlService")
	    public UrlService urlService() {
	        return new FixedUrlService("somedomain.com/myapp"); // 一个实现
	    }
	}
	
	public interface UrlService {
	    String getApplicationUrl();
	}


#参考资料:

- [英文原文 - Spring MVC and Thymeleaf: how to access data from templates](http://www.thymeleaf.org/doc/articles/springmvcaccessdata.html)
- [简体译文 - Thymeleaf+SpringMVC，如何从模板中获取数据](http://www.cnblogs.com/jiangchao226/p/5938152.html)
