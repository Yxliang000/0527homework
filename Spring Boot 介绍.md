## Spring Boot 介绍

​	spring boot 我理解就是把 spring 、spring mvc 、spring data jpa 等等的一些常用的常用的基础框架组合起来，提供默认的配置，然后提供可插拔的设计，就是各种 starter ，来方便开发者使用这一系列的技术，spring 家族发展到今天，已经很庞大了，作为一个开发者，如果想要使用 spring 家族一系列的技术，需要一个一个的搞配置，然后还有个版本兼容性问题，其实挺麻烦的，偶尔也会有小坑出现，其实蛮影响开发进度， spring boot 就是来解决这个问题，提供了一个解决方案吧，可以先不关心如何配置，可以快速的启动开发，进行业务逻辑编写，各种需要的技术，加入 starter 就配置好了，直接使用，可以说追求开箱即用的效果吧. 	

​	Spring MVC是基于Servlet 的一个 MVC 框架主要解决 WEB 开发的问题，因为 Spring 的配置非常复杂，各种XML、 JavaConfig、hin处理起来比较繁琐。于是为了简化开发者的使用，从而创造性地推出了Spring boot，约定优于配置，简化了spring的配置流程。 

​	Spring 最初利用“工厂模式”（DI）和“代理模式”（AOP）解耦应用组件。大家觉得挺好用，于是按照这种模式搞了一个 **MVC框架（一些用Spring 解耦的组件），用开发 web 应用（ SpringMVC ）**。然后发现每次开发都写很多样板代码，为了简化工作流程，于是开发出了一些“懒人整合包”（starter），这套就是 Spring Boot。 



Spring Boot可以基于Spring轻松创建可以“运行”的、独立的、生产级的应用程序。 大多数Spring Boot应用程序需要很少的Spring配置。 

​	可以使用Spring Boot创建可以使用java -jar或传统 war 包部署启动的Java应用程序。 还提供一个运行“spring脚本”的命令行工具。 不需要XML配置 。

源代码

```
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class Example {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Example.class, args);
    }

}
```

#### @RestController和@RequestMapping 注解

​	Example类的第一个注解是@RestController。 这被称为 stereotype annotation。它为人们阅读代码提供了一些提示，对于Spring来说，这个类具有特定的作用。在这里，我们的类是一个web @Controller，所以Spring在处理传入的Web请求时会考虑这个类。

@RequestMapping注解提供“路由”信息。 告诉Spring，任何具有路径“/”的HTTP请求都应映射到home方法。 @RestController注解告诉Spring将生成的字符串直接返回给调用者。

> @RestController和@RequestMapping注解是Spring MVC 的注解（它们不是Spring Boot特有的）。

#### @EnableAutoConfiguration注解

第二个类级别的注释是@EnableAutoConfiguration。 这个注解告诉 Spring Boot 根据您添加的jar依赖关系来“猜(guess)”你将如何配置Spring。由于spring-boot-starter-web添加了Tomcat和Spring MVC，自动配置将假定您正在开发Web应用程序并相应地配置Spring。

### 13.5 启动器

启动器是一组方便的依赖关系描述符，可以包含在应用程序中。 您可以获得所需的所有Spring和相关技术的一站式服务，无需通过示例代码搜索和复制粘贴依赖配置。 例如，如果要开始使用Spring和JPA进行数据库访问，那么只需在项目中包含spring-boot-starter-data-jpa依赖关系即可。

启动器包含许多依赖关系，包括您需要使项目快速启动并运行，并具有一致的受支持的依赖传递关系。

### 14.1 不要使用“default”包

当类不包括包声明时，它被认为是在“默认包”中。 通常不鼓励使用“默认包”，并应该避免使用。 对于使用@ComponentScan，@EntityScan或@SpringBootApplication注解的Spring Boot应用程序，可能会有一些特殊的问题，因为每个jar的每个类都将被读取。

> 建议遵循Java推荐的软件包命名约定，并使用反向域名（例如，com.example.project）。

### 14.2 查找主应用程序类

通常建议将应用程序主类放到其他类之上的根包(root package)中。 @EnableAutoConfiguration注解通常放置在主类上，它隐式定义了某些项目的基本“搜索包”。 例如，如果您正在编写JPA应用程序，则@EnableAutoConfiguration注解类的包将用于搜索@Entity项。

使用根包(root package)还可以使用@ComponentScan注释，而不需要指定basePackage属性。 如果您的主类在根包中，也可以使用@SpringBootApplication注释。

## 15. 配置类

Spring Boot支持基于Java的配置。虽然可以使用XML配置用SpringApplication.run()，但我们通常建议您的主source是@Configuration类。 通常，定义main方法的类也是作为主要的@Configuration一个很好的选择。

### 15.1 导入其他配置类

您不需要将所有的@Configuration放在一个类中。 @Import注解可用于导入其他配置类。 或者，您可以使用@ComponentScan自动扫描所有Spring组件，包括@Configuration类。

### 15.2 导入XML配置

如果您必须使用基于XML的配置，我们建议您仍然从@Configuration类开始。 然后，您可以使用的@ImportResource注释来加载XML配置文件。

## 16. 自动配置

Spring Boot 会根据您添加的jar依赖关系自动配置您的Spring应用程序。

## 17. Spring Beans 和 依赖注入

您可以自由使用任何标准的Spring Framework技术来定义您的bean及其依赖注入关系。 为了简单起见，我们发现使用@ComponentScan搜索bean，结合@Autowired构造函数(constructor)注入效果很好。

如果您按照上述建议（将应用程序类放在根包(root package)中）构建代码，则可以使用  @ComponentScan而不使用任何参数。 所有应用程序组件（@Component，@Service，@Repository，@Controller等）将自动注册为Spring Bean。 

如果一个bean 只有一个构造函数，则可以省略@Autowired。 

## 18. 使用@SpringBootApplication注解

许多Spring Boot开发人员总是使用@Configuration，@EnableAutoConfiguration和@ComponentScan来标注它们的主类。 由于这些注解经常一起使用，Spring Boot提供了一个方便的@SpringBootApplication注解作为这三个的替代方法。

@SpringBootApplication注解相当于使用@Configuration，@EnableAutoConfiguration和@ComponentScan和他们的默认属性：

### 19.1 从IDE运行

如果您无法将项目直接导入到IDE中，则可以使用构建插件生成IDE元数据。 Maven包括Eclipse和IDEA的插件; Gradle为各种IDE提供插件。

> 如果您不小心运行了两次Web应用程序，您将看到“Port already in use”中的错误。 使用STS用户可以使用重新启动按钮relaunch而不是运行以确保任何现有实例已关闭。

## 20. 开发工具

Spring Boot包括一组额外的工具，可以使应用程序开发体验更加愉快。 spring-boot-devtools模块可以包含在任何项目中，以提供额外的[开发时](https://blog.csdn.net/qq_36348557/article/details/development-time)功能。 要包含devtools支持，只需将模块依赖关系添加到您的构建中：

**Maven：**

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 20.1 属性默认值

Spring Boots支持的几个库使用缓存来提高性能。 例如，[模板引擎](http://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-template-engines)将缓存编译的模板，以避免重复解析模板文件。 此外，Spring MVC可以在返回静态资源时向响应中添加HTTP缓存头。

虽然缓存在生产中非常有益，但它在开发过程中可能会产生反效果，从而阻止您看到刚刚在应用程序中进行的更改。 因此，spring-boot-devtools将默认禁用这些缓存选项。

缓存选项通常由您的application.properties文件中的设置配置。 例如，Thymeleaf提供了spring.thymeleaf.cache属性。 spring-boot-devtools模块不需要手动设置这些属性，而是自动应用更加合理的开发时(development-time)配置。

### 20.2 自动重启

使用spring-boot-devtools的应用程序将在类路径上的文件发生更改时自动重新启动。 这在IDE中开发时可能是一个有用的功能，因为它为代码更改提供了非常快的反馈循环。 默认情况下，将监视指向文件夹的类路径上的任何条目。 请注意，某些资源（如静态资源和视图模板）不需要重新启动应用程序。

**触发重启**

当DevTools监视类路径资源时，触发重新启动的唯一方法是更新类路径中的文件时。 导致类路径更新的方式取决于您正在使用的IDE。 在Eclipse中，保存修改的文件将导致类路径被更新并触发重新启动。 在IntelliJ IDEA中，构建项目（Build→Make Project）将具有相同的效果。

> 只要 forking 被启用，您也可以通过支持的构建插件（即Maven和Gradle）启动应用程序，因为DevTools需要一个单独的应用程序类加载器才能正常运行。Gradle和Maven默认情况下在类路径上检DevTools。
>
> 自动重启当与LiveReload一起使用时工作非常好。 详见[下文](http://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/htmlsingle/#using-boot-devtools-livereload)。 如果您使用JRebel，自动重启将被禁用，有利于动态类重新加载。 其他devtools功能仍然可以使用（如LiveReload和属性覆盖）。
>
> DevTools依赖于应用程序上下文的关闭钩子，以在重新启动期间关闭它。 如果禁用了关闭挂钩（SpringApplication.setRegisterShutdownHook（false）），DevTools将无法正常工作。
>
> 当判断类路径中的项目是否会在更改时触发重新启动时，DevTools会自动忽略名为spring-boot，spring-boot-devtools，spring-boot-autoconfigure，spring-boot-actuator和spring-boot-start的项目。

**重新启动(Restart) vs 重新加载(Reload)**

Spring Boot提供的重新启动技术使用两个类加载器。 不会改的类（例如，来自第三方的jar）被加载到基类加载器中。 您正在开发的类被加载到重新启动(restart)类加载器中。 当应用程序重新启动时，重新启动类加载器将被丢弃，并创建一个新的类加载器。 这种方法意味着应用程序重新启动通常比“冷启动”快得多，因为基类加载器已经可以使用。

如果发现重新启动对应用程序不够快，或遇到类加载问题，您可以考虑来自ZeroTurnaround的JRebel等重新加载技术。 这些工作通过在加载类时重写(rewriting)类，使其更适合重新加载。 Spring Loaded提供了另一个选项，但是它在很多框架上不支持，并且不支持商用。

## 23. SpringApplication

SpringApplication类提供了一种方便的方法来引导将从main()方法启动的Spring应用程序。 在许多情况下，您只需委派静态SpringApplication.run()方法：

```
public static void main(String[] args) {
    SpringApplication.run(MySpringConfiguration.class, args);
}
```

### 23.2 自定义Banner

可以通过在您的类路径中添加一个 banner.txt 文件，或者将banner.location设置到banner文件的位置来更改启动时打印的banner。 如果文件有一些不常用的编码，你可以设置banner.charset（默认为UTF-8）。除了文本文件，您还可以将banner.gif，banner.jpg或banner.png图像文件添加到您的类路径中，或者设置一个banner.image.location属性。 图像将被转换成ASCII艺术表现，并打印在任何文字banner上方。

您可以在banner.txt文件中使用以下占位符：

#### 表23.1. banner变量

| 变量名                                                       | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ${application.version}                                       | 在MANIFEST.MF中声明的应用程序的版本号。例如， Implementation-Version: 1.0 被打印为 1.0. |
| ${application.formatted-version}                             | 在MANIFEST.MF中声明的应用程序版本号的格式化显示（用括号括起来，以v为前缀）。 例如 (v1.0)。 |
| ${spring-boot.version}                                       | 您正在使用的Spring Boot版本。 例如1.5.2.RELEASE。            |
| ${spring-boot.formatted-version}                             | 您正在使用格式化显示的Spring Boot版本（用括号括起来，以v为前缀）。 例如（v1.5.2.RELEASE）。 |
| Ansi.NAME(orAnsi.NAME(or{AnsiColor.NAME}, AnsiBackground.NAME,AnsiBackground.NAME,{AnsiStyle.NAME}) | 其中NAME是ANSI转义码的名称。 有关详细信息，请参阅 [AnsiPropertySource](https://github.com/spring-projects/spring-boot/tree/v1.5.2.RELEASE/spring-boot/src/main/java/org/springframework/boot/ansi/AnsiPropertySource.java)。 |
| ${application.title}                                         | 您的应用程序的标题在MANIFEST.MF中声明。 例如Implementation-Title：MyApp打印为MyApp。 |

> 如果要以编程方式生成banner，则可以使用SpringApplication.setBanner()方法。 使用org.springframework.boot.Banner 如接口，并实现自己的printBanner() 方法。

您还可以使用spring.main.banner-mode属性来决定是否必须在System.out（控制台）上打印banner，使用配置的logger（log）或不打印（off）。

### 23.8 使用ApplicationRunner或CommandLineRunner

SpringApplication启动时如果您需要运行一些特定的代码，就可以实现ApplicationRunner或CommandLineRunner接口。 两个接口都以相同的方式工作，并提供一个单独的运行方法，这将在SpringApplication.run（…）完成之前调用。

CommandLineRunner接口提供对应用程序参数的访问（简单的字符串数组），而ApplicationRunner使用上述的ApplicationArguments接口。

```
@Component
public class MyBean implements CommandLineRunner {

    public void run(String... args) {
        // Do something...
    }

}12345678
```

如果定义了若干CommandLineRunner或ApplicationRunner bean，这些bean必须按特定顺序调用，您可以实现org.springframework.core.Ordered接口，也可以使用org.springframework.core.annotation.Order注解。

### 23.9 Application exit

每个SpringApplication将注册一个JVM关闭钩子，以确保ApplicationContext在退出时正常关闭。 可以使用所有标准的Spring生命周期回调（例如DisposableBean接口或@PreDestroy注释）。

另外，如果希望在应用程序结束时返回特定的退出代码，那么bean可以实现org.springframework.boot.ExitCodeGenerator接口。

### 23.10 管理功能

可以通过指定spring.application.admin.enabled属性来为应用程序启用与管理相关的功能。 这会在平台MBeanServer上暴露SpringApplicationAdminMXBean。 您可以使用此功能来远程管理您的Spring Boot应用程序。 这对于任何服务包装器(service wrapper)实现也是有用的。

> 如果您想知道应用程序在哪个HTTP端口上运行，请使用local.server.port键获取该属性。
>
> 启用此功能时请小心，因为MBean公开了关闭应用程序的方法。