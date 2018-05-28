## Spring基础知识

​	Spring是一个从实际开发中抽取出来的框架，因此它完成了大量开发中的通用步骤，留给开发者的仅仅是与特定应用相关的部分，从而大大提高了企业应用的开发效率。 

# Spring核心技术

### 7.1 Spring的IoC容器和bean的简介

​	传统Java SE程序设计，我们直接在对象内部通过new进行创建对象，是程序主动去创建依赖对象；而IoC是有专门一个容器来创建这些对象，即由Ioc容器来控制对 象的创建。

​        控制反转（IoC）又被称作依赖注入（DI）。它是一个对象定义其依赖的过程，它的依赖也就是与它一起合作的其它对象，这个过程只能通过构造方法参数、工厂方法参数、或者被构造或从工厂方法返回后通过settter方法设置其属性来实现。然后容器在创建bean时注入这些依赖关系。这个过程本质上是反过来的，由bean本身控制实例化，或者直接通过类的结构或Service定位器模式定位它自己的依赖，因此得其名曰控制反转。

​	在Spring中，形成应用主干且被Spring IoC容器管理的对象称为*beans*。一个bean就是被Spring IoC容器实例化、装配和管理的对象。简单来说，一个bean就是你的应用中众多对象中一个。Beans和它们之间的依赖被容器中配置的元数据反射。 

### 7.3 Bean概述

Spring IoC容器管理了至少一个bean，这些bean通过提供给容器的配置元数据来创建。

在容器本身内部，这些bean定义表示为**BeanDefinition**对象，它包含以下元数据：

- 包限定的类名：被定义bean的实际实现类。
- bean行为的配置元素，在容器中bean以哪种状态表现（scope，lifecycle，callback，等等）。
- 需要一起工作的其它对象的引用，这些引用又被称作合作者（collaborator）或依赖。
- 设置到新创建对象中的其它配置，例如，在bean中使用的连接数，用于管理连接池或池的大小。

元数据转化成了一系列的属性，组成了每个bean的定义。

#### 7.3.1 命名bean

每一个bean都有一个或多个标识符。这些标识符在托管bean的容器中必须唯一。一个bean通常只有一个标识符，但如果需要更多标识符，可以通过别名来实现。

- **bean命名的约定** 
  命名bean时采用标准Java对字段的命名约定。bean名字以小写字母开头，然后是驼峰式。例如，（不带引号）**‘accountManager’**, **‘accountService’**, **‘userDao’**, **‘loginController’**, 等等。 
  按统一的规则命名bean更易于阅读和理解，而且，如果使用Spring AOP，当通过名字应用advice到一系列bean上将会非常有帮助。

#### 7.3.2 实例化bean

​	一个bean的定义本质上就是创建一个或多个对象的食谱。容器查看这个食谱并使用被bean定义封装的配置元数据来创建（或获取）实际的对象。

​	如果使用XML配置，在<bean/>元素的class属性中指定被实例化的对象的类型（或类）即可。class属性通常是必需的，它实际是BeanDefinition实例的Class属性（例外，参考使用实例的工厂方法实例化和7.7 Bean定义继承）。有两种使用Class属性的方式：

典型地，容器直接通过反射调用bean的构造方法来创建一个bean，这个Java代码中使用new操作符是等价的。
	容器通过调用工厂类中的static工厂方法来创建一个bean。通过调用static工厂方法返回的对象类型可能是同一个类，也可能是完全不同的另一个类。 
	内部类名字。如果为一个static嵌套类配置bean定义，必须使用嵌套类的二进制名字。 
例如，如果在com.example包中有一个类叫Foo，并且Foo类中有一个static嵌套类叫Bar，那么bean定义中的class属性的值应该是com.example.Foo$Bar。 
注意使用$字符分割嵌套类和外部类。
使用构造方法实例化
	用构造方法创建bean的方式，在Spring中所有正常的类都可以使用并兼容。也就是说，被开发的类不需要实现任何特定的接口或以特定的形式编码。仅仅指定bean类就足够了。但是，依靠什么样的类型来指定bean，你可能需要默认的空构造方法。

​	Spring的IoC窗口几乎可以管理所有的类，并不仅限于真的JavaBean。大部分用户更喜欢带有默认（无参）构造方法和适当setter/getter方法的JavaBean。你也可以拥有不同的非bean风格的类。例如，如果你需要使用不是JavaBean格式的连接池，Spring一样可以管理它。

​	在XML中，可以像下面这样指定bean类：

<bean id="exampleBean" class="examples.ExampleBean"/> <bean name="anotherExample" class="examples.ExampleBeanTwo"/> 

##### 使用静态工厂方法实例化

当使用静态工厂方法创建一个bean时，需要使用**class**属性指定那个包含静态工厂方法的类，并使用**factory-method**属性指定工厂方法的名字。应该可以调用这个方法（带参数的稍后讨论）并返回一个有效的对象，之后它就像用构造方法创建的对象一样对待。一种这样的bean定义的使用方法是在代码调用静态工厂。

下面的bean定义指定了通过调用工厂方法创建这个bean。这个定义没有指定返回类型，仅仅指定了包含工厂方法的类。在这个例子中，**createInstance()**方法必须是静态的。

```
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>123
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```

### 7.4 依赖

一个典型的企业级应用包含了不止一个对象（或者用Spring的说法叫bean）。即使是最简单的应用也需要几个对象一起工作以展现给最终用户看到的连贯应用。下一节介绍怎么样定义一系列独立的bean使它们相互合作实现一个共同的目标。

#### 7.4.1 依赖注入

​	依赖注入是一个对象定义其依赖的过程，它的依赖也就是与它一起合作的其它对象，这个过程只能通过构造方法参数、工厂方法参数、或者被构造或从工厂方法返回后通过setter方法设置其属性来实现。然后容器在创建这个bean时注入这些依赖。这个过程本质上是反过来的，由bean本身控制实例化，或者直接通过类的结构或Service定位器模式定位它自己的依赖，因此得其名曰控制反转。

​	使用依赖注入原则代码更干净，并且当对象提供了它们的依赖时解耦更有效。对象不查找它的依赖，且不知道依赖的位置或类。比如，类变得更容易测试，尤其是当依赖是基于接口或抽象基类时，这允许在单元测试时模拟实现。

​	依赖注入有两种主要的方式，基于构造方法的依赖注入和基于setter方法的依赖注入。

##### 基于构造方法的依赖注入

基于构造方法的依赖注入，由容器调用带有参数的构造方法来完成，<u>每个参数代表一个依赖</u>。调用带有特定参数的静态工厂方法创建bean几乎是一样的，这里把构造方法的参数与静态工厂方法的参数同等对待。下面的例子展示了一个只能通过构造方法注入依赖的类。注意，这个类并没有什么特殊的地方，它仅仅只是一个没有依赖容器的特定接口、基类或注解的POJO。

```
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on a MovieFinder
    private MovieFinder movieFinder;

    // a constructor so that the Spring container can inject a MovieFinder
    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...

}
```

##### 基于setter方法的依赖注入

基于setter方法的依赖注入，由容器在调用无参构造方法或无参静态工厂方法之后调用setter方法来实例化bean。

下面的例子展示了一个只能通过纯净的setter方法注入依赖的类。这个类符合Java的约定，它是一个没有依赖容器的特定接口、基类或注解的POJO。

```
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on the MovieFinder
    private MovieFinder movieFinder;

    // a setter method so that the Spring container can inject a MovieFinder
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...

}12345678910111213
```

**ApplicationContext**对它管理的bean支持基于构造方法和基于setter方法的依赖注入，也支持在使用构造方法注入依赖之后再使用setter方法注入依赖。以**BeanDefinition**的形式配置依赖，可以与**PropertyEditor**实例一起把属性从一种形式转化为另一种形式。但是，大多数Spring用户不直接（编程式地）使用这些类，而是使用XML **bean**定义、注解的组件（例如，以**@Component**、**@Controller**注解的类）或基于Java的**@Configuration**类的**@Bean**方法。这些资源然后都被内部转化为了**BeanDefinition**的实例，并用于加载完整的Spring IoC容器的实例。

- **基于构造方法或基于setter方法的依赖注入？** 
  因为可以混合使用基于构造方法和基于setter方法的依赖注入，所以使用构造方法注入强制依赖并使用setter方法或配置方法注入可选依赖是一个不错的原则。注意，在setter方法上使用[@Required](https://blog.csdn.net/tangtong1/article/details/51960382#beans-required-annotation)注解可以使属性变成必需的依赖。 
  Spring团队一般倡导使用构造方法注入，因为它会使应用程序的组件实现为不可变的对象，并保证必需的依赖不为**null**。另外，构造方法注入的组件总是以完全初始化的状态返回给客户端(调用)代码。注意，大量的构造方法参数是代码的坏味道，这意味着类可能有很多责任，应该被重构为合适的部分以实现关注点的分离。 
  setter方法应该仅仅只用于可选依赖，这些可选依赖应该在类中被赋值合理的默认值。否则，在使用这项依赖的任何地方都要做非null检查。setter方法注入的好处之一是可以使那个类的对象稍后重新配置或重新注入。使用[JMX MBeans](https://blog.csdn.net/tangtong1/article/details/51960382#jmx)的管理是使用setter注入很好的案例。 
  特定的类选择最合适的依赖注入方式。有时你并没有第三方类的源码，你就需要选择使用哪种方式。例如，如果一个第三方类没有暴露任何setter方法，那么只能选择构造方法注入了。

##### 依赖注入的过程

容器按如下方式处理依赖注入：

- **ApplicationContext**被创建并初始化描述所有bean的配置元数据。配置元数据可以是XML、Java代码或注解。
- 对于每一个bean，它的依赖以属性、构造方法参数或静态工厂方法参数的形式表示。这些依赖在bean实际创建时被提供给它。
- 每一个属性或构造方法参数都是将被设置的值的实际定义，或容器中另一个bean的引用。
- 每一个值类型的属性或构造方法参数都会从特定的形式转化为它的实际类型。默认地，Spring可以把字符串形式的值转化为所有的内置类型，比如**int**, **long**, **String**, **boolean**，等等。

Spring容器在创建时会验证每个bean的配置。但是，bean的属性本身直到bean实际被创建时才会设置。单例作用域的或被设置为预先实例化（默认）的bean会在容器创建时被创建。作用域的定义请参考[7.5 bean的作用域](https://blog.csdn.net/tangtong1/article/details/51960382#bean-scopes)。否则，bean只在它被请求的时候才会被创建。创建一个bean会潜在地引起一系列的bean被创建，因为bean的依赖及其依赖的依赖（等等）会被创建并赋值。注意，那些不匹配的依赖可能稍后创建，比如，受影响的bean的首次创建（译者注：这句可能翻译的不到位，有兴趣的自己翻译下，原文为 Note that resolution mismatches among those dependencies may show up late, i.e. on first creation of the affected bean）。

- **循环依赖** 
  如果你主要使用构造方法注入，很有可能创建一个无法解决的循环依赖场景。 
  例如，类A使用构造方法注入时需要类B的一个实例，类B使用构造方法注入时需要类A的一个实例。如果为类A和B配置bean互相注入，Spring IoC容器会在运行时检测出循环引用，并抛出异常**BeanCurrentlyInCreationException**。 
  一种解决方法是把一些类配置为使用setter方法注入而不是构造方法注入。作为替代方案，避免构造方法注入，而只使用setter方法注入。换句话说，尽管不推荐，但是可以通过setter方法注入配置循环依赖。 
  不像典型的案例（没有循环依赖），A和B之间的循环依赖使得一个bean在它本身完全初始化之前被注入了另一个bean（经典的先有鸡/先有蛋问题）。

#### 7.4.5 自动装配合作者

Spring容器可以相互合作的bean间自动装配其关系。你可让让Spring通过检查ApplicationContext的内容自动为你解决bean之间的依赖。自动装配有以下优点：

- 自动装配将极大地减少指定属性或构造方法参数的需要（在这点上，其它机制比如本章其它小节讲解的[bean模板](https://blog.csdn.net/tangtong1/article/details/51960382#beans-child-bean-definitions)也是有价值的）。
- 自动装配可以更新配置当你的对象进化时。例如，如果你需要为一个类添加一个依赖，那么不需要修改配置就可以自动满足。因此，自动装配在开发期间非常有用，但不否定在代码库变得更稳定的时候切换到显式的装配。

使用XML配置时，可以为带有autowire属性的bean定义指定自动装配的模式。自动装配的功能有四种模式。可以为每个自动装配的bean指定一种模式。 
**表7.2 自动装配的模式**

| 模式        | 解释                                                         |
| ----------- | ------------------------------------------------------------ |
| no          | 默认地没有自动自动装配。bean的引用必须通过ref元素定义。对于大型部署，不推荐更改默认设置，因为显式地指定合作者能够更好地控制且更清晰。在一定程度上，这也记录了系统的结构。 |
| byName      | 按属性名称自动装配。Spring为待装配的属性寻找同名的bean。例如，如果一个bean被设置为按属性名称自动装配，且它包含一个属性叫master（亦即，它有setMaster(…)方法），Spring会找到一个名叫master的bean并把它设置到这个属性中。 |
| byType      | 按属性的类型自动装配，如果这个属性的类型在容器中只存在一个bean。如果多于一个，则会抛出异常，这意味着不能为那个bean使用按类型装配。如果一个都没有，则什么事都不会发生，这个属性不会被装配。 |
| constructor | 与按类型装配类似，只不过用于构造方法的参数。如果这个构造方法的参数类型在容器中不存在明确的一个bean，将会抛出异常。 |

使用按类型装配或构造方法自动装配模式，可以装配数组和集合类型。在这个情况下，容器中所有匹配期望类型的候选者都将被提供用来满足此依赖。你可以自动装配强类型的Map如果其键的类型是String。自动装配Map的值将包含所有匹配期望类型的bean实例，并且Map的键将包含对应的bean的名字。

可以联合使用自动装配行为和依赖检查，其中依赖检查会在自动装配完成后执行。



### 7.9 基于注解的容器配置

```
注解形式比XML形式更好吗？

注解形式的引入引起了一个话题，它是否比XML形式更好。简单的回答是视情况而定。详细的回答是每一种方式都有它的优缺点，通常由开发者决定哪种方式更适合他们。由于他们定义的方式，注解提供了更多的上下文声明，导致了更短更简明的配置。然而，XML形式装配组件不会涉及到它们的源码或者重新编译它们。一些开发者更喜欢亲近源码，但另一些则认为注解类不是POJO，且配置很分散，难以控制。

不管做出什么选择，Spring都支持两种风格且可以混用它们。另外，通过Java配置的方式，Spring可以让注解变得非侵入式，不会触碰到目标组件的源码。而且，所有的配置方式Spring Tool Suite都支持。12345
```

一种XML形式的替代方案是使用基于注解的配置，它依赖于字节码元数据，用于装配组件并可取代尖括号式的声明。不同于使用XML描述一个bean，开发者需要把配置移动到组件类本身，并给相关的类、方法及字段声明加上注解。

#### 7.9.1 @Required

可以在构造方法上使用**@Autowired**：

```
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...

}123456789101112
```

- 从Spring 4.3开始，如果目标bean只有一个构造方法，则**@Autowired**的构造方法不再是必要的。如果有多个构造方法，那么至少一个必须被注解以便告诉容器使用哪个。

也可以在setter方法上使用**@Autowired**：

```
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...

}123456789101112
```

也可以应用在具有任意名字和多个参数的方法上：

```
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...

}12345678910111213141516
```

也可以应用在字段上，甚至可以与构造方法上混用：

```
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    private MovieCatalog movieCatalog;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...

}123456789101112131415
```

也可以从**ApplicationContext**中提供特定类型的所有bean，只要添加这个注解在一个那种类型的数组字段或方法上即可：

```
public class MovieRecommender {

    @Autowired
    private MovieCatalog[] movieCatalogs;

    // ...

}12345678
```

同样适用于集合类型：

```
public class MovieRecommender {

    private Set<MovieCatalog> movieCatalogs;

    @Autowired
    public void setMovieCatalogs(Set<MovieCatalog> movieCatalogs) {
        this.movieCatalogs = movieCatalogs;
    }

    // ...

}123456789101112
```

- 如果需要数组或list中的元素按顺序排列的话，可以让这些bean实现**org.springframework.core.Ordered**接口或使用**@Order**注解或标准的**@Priority**注解。

甚至Map也可以被自动装配，只要key的类型是**String**就可以。Map的value将包含所有的特定类型的bean，并且key会包含这些bean的名字。

```
public class MovieRecommender {

    private Map<String, MovieCatalog> movieCatalogs;

    @Autowired
    public void setMovieCatalogs(Map<String, MovieCatalog> movieCatalogs) {
        this.movieCatalogs = movieCatalogs;
    }

    // ...

}123456789101112
```

默认地，如果没有候选的bean则自动装配会失败。这种默认的行为表示被注解的方法、构造方法及字段必须（required）有相应的依赖。也可按下面的方法改变这种行为。

```
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired(required=false)
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...

}123456789101112
```

- 每个类只能有一个构造方法被标记为required，但可以有多个非必须的构造方法被注解。这种情况下，每个构造方法都会考虑候选者，且Spring使用最贪婪的那个构造方法，它的依赖都能被满足，并且有最多个的参数。 
  **@Autowired**的**required**属性比**@Required**注解更推荐使用。这个*required*属性表示如果不能被自动装配那么这个属性是非必须的、会被忽略的。另一方面，**@Required**则更强势，它要求这个属性必须被设置不管容器以什么样的方式支持它。如果没有值被注入，就会抛出异常。

也可以把**@Autowired**用在那些著名的可解析的依赖的接口上：**BeanFactory, ApplicationContext, Environment, ResourceLoader, ApplicationEventPublisher**, 以及**MessageSource**。这些接口和它们扩展的接口，比如**ConfigurableApplicationContext**或**ResourcePatternResolver**，会自动解析，不需要特殊设置。

```
public class MovieRecommender {

    @Autowired
    private ApplicationContext context;

    public MovieRecommender() {
    }

    // ...

}1234567891011
```

**@Autowired， @Inject， @Resource**和**@Value**注解都是被Spring的**BeanPostProcessor**处理的，这反过来意味着我们不能使用自己的**BeanPostProcessor**或**BeanFactoryPostProcessor**类型来处理这些注解。这些类型必须通过XML或使用Spring的**@Bean**方法显式地装配。

### 7.10 类路径扫描及管理的组件

本章的大部分例子都将采用XML的形式配置元数据。上一节7.9 基于注解的容器配置描述了怎么在源码级别提供配置。即便如此，基本的配置还是得通过XML来配置，注解仅仅用于驱动依赖注入。本节提供了一种隐式地通过扫描类路径检测候选组件的方式。候选组件是那些符合相应过滤规则并与容器通信的类。这种方式可以让我们不再通过XML的形式执行bean的注册，而是采用注解（比如**@Component**）、AspectJ表达式或自定义的过滤规则来选择哪些类将被注册到容器中。

- 从Spring 3.0开始，许多由Spring的Java配置项目提供的功能都成为了Spring的核心部分。这使得我们可以通过Java而不是XML形式定义bean。参考**@Configuration， @Bean， @Import**和**@DependsOn**注解，来看一看怎么使用这些新功能。

#### 7.10.1 @Component及其扩展注解

**@Repository**注解是一种用于标识存储类（也被称为数据访问对象或者DAO）的标记。异常的自动翻译是这个标记的用法之一，参考[20.2.2 异常翻译](https://blog.csdn.net/tangtong1/article/details/51960382#exception-translation)。

Spring提供了一些扩展注解：**@Component， @Service**和**@Controller**。**@Component**可用于管理任何Spring的组件。**@Repository， @Service**和**@Controller**是**@Component**用于指定用例的特殊形式，比如，在持久层、服务层和表现层。使用**@Service**或**@Controller**能够让你的类更易于被合适的工具处理或与切面（aspect）关联。比如，这些注解可以使目标组件成为切入点。当然，**@Repository， @Service**和**@Controller**也能携带更多的语义。因此，如果你还在考虑使用**@Component**还是**@Service**用于注解service层，那么就选**@Service**吧，它更清晰。同样地，如前面所述，**@Repository**还能够用于在持久层标记自动异常翻译。

#### 7.10.2 元注解

Spring提供了很多注解可用于元注解。元注解即一种可用于别的注解之上的注解。例如，**@Service**就是一种被元注解**@Component**注解的注解：

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component // Spring will see this and treat @Service in the same way as @Component
public @interface Service {

    // ....
}12345678
```

元注解也可以组合起来形成组合注解。例如，**@RestController**注解是一种**@Controller**与**@ResponseBody**组合的注解。

另外，组合注解也可以重新定义来自元注解的属性。这在只想暴露元注解的部分属性值的时候非常有用。例如，Spring的**@SessionScope**注解把它的作用域硬编码为**session**，但是仍然允许自定义**proxyMode**。

```
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(WebApplicationContext.SCOPE_SESSION)
public @interface SessionScope {

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}1234567891011121314
```

**@SessionScope**然后就可以使用了，而且不需要提供**proxyMode**，如下：

```
@Service
@SessionScope
public class SessionScopedService {
    // ...
}12345
```

或者重写**proxyMode**的值，如下：

```
@Service
@SessionScope(proxyMode = ScopedProxyMode.INTERFACES)
public class SessionScopedUserService implements UserService {
    // ...
}12345
```

更多信息请参考[Spring注解编程模型](https://blog.csdn.net/tangtong1/article/details/51960382#spring-annotation-programming-model)。

#### 7.10.3 自动检测类并注册bean定义

Spring能够自动检测被注解的类，并把它们注册到**ApplicationContext**中。例如，下面的两个会被自动检测到：

```
@Service
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired
    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

}1234567891011
```

```
@Repository
public class JpaMovieFinder implements MovieFinder {
    // implementation elided for clarity
}1234
```

为了能够自动检测到这些类并注册它们，需要为**@Configuration**类添加**@ComponentScan**注解，并设置它的**basePackage**属性为这两个类所在的父包（替代方案，也可以使用逗号、分号、空格分割这两个类所在的包）。

```
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}12345
```

- 上面的配置也可以简单地使用这个注解的**value**属性，例如：**ComponentScan(“org.example”)**

也可以使用XML形式的配置：

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.example"/>

</beans>123456789101112
```

- 使用**<context:component-scan>**隐式地允许**<context:annotation-config>**的功能。因此，使用**<context:component-scan>**时一般就不需要再包含**<context:annotation-config>**元素了。
- 类路径扫描的包必须保证这些包出现在classpath中。当使用Ant构建JAR包时，请确定不要激活仅仅使用文件的开关。同样地，类路径目录可能在某些环境下基于安全考虑不允许暴露，基于JDK 1.7.0_45及更高版本的app（需要在清单中设置信任库，参考<http://stackoverflow.com/questions/19394570/java-jre-7u45-breaks-classloader-getresources>）。

另外，使用**component-scan**元素时默认也启用了**AutowiredAnnotationBeanPostProcessor**和**CommonAnnotationBeanPostProcessor**。这意味着这两个组件被自动检测到了且不需要在XML中配置任何元数据。

- 可以使用**annotation-config**元素并设置其属性为false来禁用**AutowiredAnnotationBeanPostProcessor**和**CommonAnnotationBeanPostProcessor**。

#### 7.10.4 使用过滤器自定义扫描

默认地，只有使用注解**@Component, @Repository, @Service, @Controller**或自定义注解注解的类才能被检测为候选组件。然而，我们可以使用自定义的过滤器修改并扩展这种行为。添加这些过滤器到**@ComponentScan**注解的*includeFilters*或*excludeFilters*参数即可（或**component-scan**元素的子元素*include-filter*或*exclude-filter*）。每个过滤器元素都需要**type**和**expression**属性。下表描述了相关的选项： 
**表7.5. 过滤器类型**

| 过滤器类型       | 表达式例子                 | 描述                                                     |
| ---------------- | -------------------------- | -------------------------------------------------------- |
| annotation(默认) | org.example.SomeAnnotation | 目标组件类级别的注解                                     |
| assignable       | org.example.SomeClass      | 目标组件继承或实现的类或接口                             |
| aspectj          | org.example..*Service+     | 用于匹配目标组件的AspecJ类型表达式                       |
| regex            | org.example.Default.*      | 用于匹配目标组件类名的正则表达式                         |
| custom           | org.example.MyTypeFilter   | org.springframework.core.type.TypeFilter接口的自定义实现 |

下面的例子展示了如何忽略掉所有的**@Repository**注解，并使用带有“stub”的Repository代替：

```
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    ...
}1234567
```

或者使用XML形式配置：

```
<beans>
    <context:component-scan base-package="org.example">
        <context:include-filter type="regex"
                expression=".*Stub.*Repository"/>
        <context:exclude-filter type="annotation"
                expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
</beans>12345678
```

- 也可以设置这个注解的**useDefaultFilters=false**或为**<component-scan/>**元素提供属性**use-default-filters=”false”**忽略掉默认的过滤器。这将不会自动检测带有**@Component, @Repository, @Service, @Controller**或**@Configuration**注解的类。

#### 7.10.5 在组件内部定义bean元数据

Spring的组件也可以为容器贡献bean的定义元数据，只要在**@Component**注解的类内部使用**@Bean**注解即可。下面是一个简单的例子：

```
@Component
public class FactoryMethodComponent {

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    public void doWork() {
        // Component method implementation omitted
    }

}1234567891011121314
```

这个类是Spring的一个组件，它包含一个应用相关的方法**doWork()**。但是，它也通过方法**publicInstance()**贡献了一个bean定义。**@Bean**注解标识了这个工厂方法和其它的bean属性，比如**@Qualifier**注解的限定符。其它可用于此处的方法级别注解还有**@Scope， @Lazy**及自定义注解等。

- 除了扮演组件初始化的角色，**@Lazy**注解还可以放置在被**@Autowired**或**@Inject**标记的注入点。在这种情况下，它会使得注入使用延迟代理。

自动装配的字段和方法也可以像前面讨论的一样被支持，也可以支持**@Bean**方法的自动装配：

```
@Component
public class FactoryMethodComponent {

    private static int i;

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    // use of a custom qualifier and autowiring of method parameters

    @Bean
    protected TestBean protectedInstance(
            @Qualifier("public") TestBean spouse,
            @Value("#{privateInstance.age}") String country) {
        TestBean tb = new TestBean("protectedInstance", 1);
        tb.setSpouse(spouse);
        tb.setCountry(country);
        return tb;
    }

    @Bean
    private TestBean privateInstance() {
        return new TestBean("privateInstance", i++);
    }

    @Bean
    @RequestScope
    public TestBean requestScopedInstance() {
        return new TestBean("requestScopedInstance", 3);
    }

}1234567891011121314151617181920212223242526272829303132333435
```

上面的例子使用了另一个叫作**privateInstance**的bean的**Age**属性自动装配了**String**类型的参数**country**。Spring的表达式语言使用**#{}**的记法定义了这个属性的值。对于**@Value**注解，提前配置的表达式解析器会在需要解析表达式文本的时候寻找bean的名字。

在Spring组件内部的**@Bean**方法的处理不同于使用**@Configuration**注解的类内部的**@Bean**方法。不同之处是**@Component**类不会使用CGLIB拦截调用的方法和字段从而进行增强。CGLIB代理在调用**@Configuration**类中的**@Bean**方法时会创建对合作对象的引用，这种方法的调用不会通过正常的Java语法调用，而是通过容器以便提供生命周期管理，甚至在通过编程地方式调用**@Bean**方法时也会形成对其它bean的引用。相反，调用普通的**@Component**类中的**@Bean**方法只会形成标准的Java语法调用，不会有特殊的CGLIB处理过程及其它的限制条件。

- 你可能会定义**@Bean**方法为静态的，这样就不用创建包含它的类的实例了。这在定义后置处理器bean时会形成特殊的情况，比如**BeanFactoryPostProcessor**或**BeanPostProcessor**，因为这类bean会在容器的生命周期前期被初始化，而不会触发其它部分的配置。 
  注意，对静态**@Bean**方法的调用永远不会被容器拦截，即使在**@Configuration**类内部。这是由于技术上的瓶颈：CGLIB的子类代理只会重写非静态方法。因此，对另一个**@Bean**方法的直接调用只有标准的Java语法，只会从工厂方法本身直接返回一个独立的实例。 
  由于Java语言的可见性，**@Bean**方法并不一定会对容器中的bean有效。你可能很随意的在非**@Configuration**类中定义或定义为静态方法。然而，在**@Configuration**类中的正常的**@Bean**方法都需要被重写的，因此，它们不应该定义为**private**或**final**。 
  **@Bean**方法也可以在父类中被发现，同样适用于Java 8中接口的默认方法。这使得组建复杂的配置时能具有更好的灵活性，甚至可能通过Java 8的默认方法实现多重继承，这在Spring 4.2开始支持。 
  最后，注意一个类中可能会存在相同bean的多个**@Bean**方法，这会在运行时选择合适的工厂方法。使用的算法时选择“最贪婪”的构造方法，一些场景可能会按如下方法选择相应的工厂方法：满足最多依赖的会被选择，这与使用**@Autowired**时选择多个构造方法时类似。

#### 7.10.6 命名自动检测的组件

当一个组件被扫描过程自动检测到时，它的名字由**BeanNameGenerator**定义的策略生成。默认地，Spring的扩展注解（**@Component, @Repository, @Service**和**@Controller**）都包含一个**value**属性，这个**value**值会提供一个名字以便通信。

如果这样的注解没有明确地提供**value**值，或者另外一些检测到的组件（比如自定义过滤器扫描到的组件），那么默认生成器会返回一个首字母小写的短路径的类名。比如，下面两个组件，它们的名字分别为**myMovieLister**和**movieFinderImpl**：

```
@Service("myMovieLister")
public class SimpleMovieLister {
    // ...
}1234
```

```
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}1234
```

- 如果不想遵循默认的名字生成策略，也可以提供自定义的策略。首先，需要实现**BeanNameGenerator**接口，并且要包含一个无参构造方法。然后，配置扫描器时为其指定这个自定义生成器的全路径：

  ```
  @Configuration
  @ComponentScan(basePackages = "org.example", nameGenerator = MyNameGenerator.class)
  public class AppConfig {
      ...
  }12345
  ```

  ```
  <beans>
      <context:component-scan base-package="org.example"
          name-generator="org.example.MyNameGenerator" />
  </beans>1234
  ```

一般地，当其它的组件可能会明确地引用这个组件时为其注解提供一个名字是个很好地方式。另外，当容器装配时自动生成的名字足够用了。

#### 7.10.7 为自动检测的组件提供作用域

一般Spring管理的组件的作用域默认为**singleton**。但是，有时可能会需要不同的作用域，这时可以通过**@Scope**注解来声明：

```
@Scope("prototype")
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}12345
```

更详细的信息请参考[7.5.4 Request, session, global session, application和WebSocket作用域](https://blog.csdn.net/tangtong1/article/details/51960382#request-session-global-session-application-and-websocket-scopes)。

- 也可以自定义策略处理作用域而不是依靠这种注解的方法，实现**ScopeMetadataResolver**接口，并包含一个默认的无参构造方法，然后在配置扫描器的时候提供其全路径即可。 
  `@Configuration @ComponentScan(basePackages = "org.example", scopeResolver = MyScopeResolver.class) public class AppConfig { ... } `
  `<beans> <context:component-scan base-package="org.example" scope-resolver="org.example.MyScopeResolver" /> </beans> `

当使用非单例作用域时，有必要为作用域内的对象生成代理。原因如[有作用域的bean作为依赖项](https://blog.csdn.net/tangtong1/article/details/51960382#scoped-beans-as-dependencies)中描述。因此，**component-scan**元素需要指明**scoped-proxy**属性。有三种可选值：无，接口和目标类。例如，下面的配置将使用标准的JDK动态代理：

```
@Configuration
@ComponentScan(basePackages = "org.example", scopedProxy = ScopedProxyMode.INTERFACES)
public class AppConfig {
    ...
}12345
```

```
<beans>
    <context:component-scan base-package="org.example"
        scoped-proxy="interfaces" />
</beans>1234
```

#### 7.10.8 使用注解提供限定符

**@Qualifier**注解在[7.9.4 使用限定符微调基于注解的自动装配](https://blog.csdn.net/tangtong1/article/details/51960382#fine-tuning-annotation-based-autowiring-with-qualifiers)中被讨论过。那节的例子中展示了如何使用**@Qualifier**注解，并展示了如何使用自定义的限定符注解提供更细粒度的控制。那些例子都是基于XML形式的，使用**qualifier**或**meta**子元素为bean提供限定符。同样地，也可以在类级别提供注解达到同样的效果。下面的三个例子展示了用法：

```
@Component
@Qualifier("Action")
public class ActionMovieCatalog implements MovieCatalog {
    // ...
}12345
```

```
@Component
@Genre("Action")
public class ActionMovieCatalog implements MovieCatalog {
    // ...
}12345
@Component
@Offline
public class CachingMovieCatalog implements MovieCatalog {
    // ...
}12345
```

- 与大部分注解一样，请记住注解元数据是绑定到类定义本身的，然而XML形式允许为相同类型提供多个bean并绑定不同的限定符，因为XML的元数据是绑定到每个实例的而不是每个类。

### 7.11 使用JSR 330标准注解

从Spring 3.0开始，Spring开始支持JSR-330的标准注解用于依赖注入。这些注解与Spring自带的注解一样被扫描。仅仅只需要引入相关的jar包即可。

- 如果使用Maven，**javax.inject**也可以在标准Maven仓库中找到，添加如下配置到pom.xml即可。

  ```
  <dependency>
      <groupId>javax.inject</groupId>
      <artifactId>javax.inject</artifactId>
      <version>1</version>
  </dependency>12345
  ```

#### 7.11.1 使用@Inject和@Named依赖注入

可以像下面这样使用**@javax.inject.Inject**代替**@Autowired**：

```
import javax.inject.Inject;

public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    public void listMovies() {
        this.movieFinder.findMovies(...);
        ...
    }
}12345678910111213141516
```

与**@Autowired**一样，可以在字段级别、方法级别或构造参数级别使用**@Inject**。另外，也可以定义注入点为**Provider**，以便按需访问短作用域的bean或通过调用**Provider.get()**延迟访问其它的bean。上面例子的一种变体如下：

```
import javax.inject.Inject;
import javax.inject.Provider;

public class SimpleMovieLister {

    private Provider<MovieFinder> movieFinder;

    public void listMovies() {
        this.movieFinder.get().findMovies(...);
        ...
    }
}123456789101112
```

如果你喜欢为依赖添加一个限定符，也可以像下面这样使用**@Named**注解：

```
import javax.inject.Inject;
import javax.inject.Named;

public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(@Named("main") MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}1234567891011121314
```

#### 7.11.2 @Named：与@Component注解等价

可以像下面这样使用**@javax.inject.Named**代替**@Component**：

```
import javax.inject.Inject;
import javax.inject.Named;

@Named("movieListener")
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}123456789101112131415
```

通常使用**@Component**都不指定名字，同样地**@Named**也可以这么用：

```
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}123456789101112131415
```

使用**@Named**时，也可以像使用Spring注解一样使用组件扫描：

```
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}12345
```

- 与**@Component**不同的是，JSR-330的**@Named**注解不能组合成其它的注解，因此，如果需要构建自定义的注解，请使用Spring的注解。

#### 7.11.3 JSR-330标准注解的局限性

使用标准注解时，应该要了解以下不支持的特性：

**表 7.6. Spring组件模型与JSR-330变种的对比**

| Spring              | javax.inject.*    | javax.inject的局限性                                         |
| ------------------- | ----------------- | ------------------------------------------------------------ |
| @Autowired          | @Inject           | @Inject没有require属性，可以使用Java 8的Optional代替。       |
| @Component          | @Named            | JSR-330没有提供组合模型，仅仅只是一种标识组件的方式          |
| @Scope(“singleton”) | @Singleton        | JSR-330默认的作用域类似于Spring的prototype。然而，为了与Spring一般的配置的默认值保持一致，JSR-330配置的bean在Spring中默认为singleton。为了使用singleton以外的作用域，必须使用Spring的@Scope注解。javax.inject也提供了一个@Scope注解，不过这仅仅被用于创建自己的注解。 |
| @Qualifier          | @Qualifier/@Named | javax.inject.Qualifier仅使用创建自定义的限定符。可以通过javax.inject.Named创建与Spring中@Qualifier一样的限定符 |
| @Value              | -                 | 无                                                           |
| @Required           | -                 | 无                                                           |
| @Lazy               | -                 | 无                                                           |
| ObjectFactory       | Provider          | javax.inject.Provider是对Spring的ObjectFactory的直接替代，仅仅使用简短的get()方法即可。它也可以与Spring的@Autowired或无注解的构造方法和setter方法一起使用。 |

### 7.12 基于Java的容器配置

#### 7.12.1 基本概念：@Bean和@Configuration

Spring中基于Java的配置的核心内容是**@Configuration**注解的类和**@Bean**注解的方法。

**@Bean**注解表示一个方法将会实例化、配置并初始化一个对象，且这个对象会被Spring容器管理。这就像在XML中**<beans/>**元素中**<bean/>**元素一样。**@Bean**注解可用于任何Spring的**@Component**注解的类中，但大部分都只用于**@Configuration**注解的类中。

注解了**@Configuration**的类表示这个类的目的就是作为bean定义的地方。另外，**@Configuration**类内部的bean可以调用本类中定义的其它bean作为依赖。最简单的配置大致如下：

```
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

}123456789
```

上面的**AppConfig**类与下面的XML形式是等价的：

```
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>123
```

```
全量的@Configuration和简化的@Bean模式？
当@Bean方法不定义在@Configuration的类中时，它们会被一种简化的模式处理。例如，定义在@Component类或普通类中的@Bean方法。

不同于全量的@Configuration模式，简化的@Bean方法不能轻易地使用别的依赖。通常在简化械下一个@Bean方法不会调用另一个@Bean方法。

推荐在@Configuration类中使用@Bean方法，从而保证全量模式总是起作用。这样可以防止同一个@Bean方法被无意中调用多次，并减少一些狡猾的bug。123456
```

**@Bean**和**@Configuration**注解将会在下面的章节中详细讨论。首先，我们来看看基于Java配置以不同的方式创建Spring的容器。

#### 7.12.2 使用AnnotationConfigApplicationContext实例化Spring容器

下面的章节介绍Spring 3.0中引入的**AnnotationConfigApplicationContext**。这个**ApplicationContext**的实现不仅可以把**@Configuration**类作为输入，同样普通的**@Component**类和使用JSR-330注解的类也可以作为输入。

当使用**@Configuration**类作为输入时，这个类本身及其下面的所有**@Bean**方法都会被注册为bean。

当**@Component**和JSR-330类作为输入时，它们会被注册为bean，并且假设在必要的时候使用了**@Autowired**或**@Inject**。

##### 简单的构造方法

与使用**ClassPathXmlApplicationContext**注入XML文件一样，可以使用**AnnotationConfigApplicationContext**注入**@Configuration**类。这样就完全不用在Spring容器中使用XML了：

```
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}12345
```

如前面所述，**AnnotationConfigApplicationContext**不限于只注入**@Configuration**类，任何**@Component**或JSR-330注解的类都能被提供给这个构造方法。例如：

```
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}12345
```

上面假设了**MyServiceImpl， Dependency1， Dependency2**使用了Spring的依赖注入注解比如**@Autowired**。

##### 使用register(Class

```
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}12345678
```

##### 使用scan(String…)扫描组件

为了扫描组件，只要像下面这样配置**@Configuration**类即可：

```
@Configuration
@ComponentScan(basePackages = "com.acme")
public class AppConfig  {
    ...
}12345
```

- 有经验的用户可能更熟悉使用等价的XML形式配置： 
  `<beans> <context:component-scan base-package="com.acme"/> </beans> `

上面的例子中，**com.acme**包会被扫描，只要是使用了**@Component**注解的类，都会被注册进容器中。同样地，**AnnotationConfigApplicationContext**也暴露了**scan(String…)**方法用于扫描组件：

```
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}123456
```

- 请记住**@Configuration**类是被**@Component**元注解注解的类，所以它们也会被扫描到。上面的例子中，假设**AppConfig**定义在**com.acme**包中（或更深的包中），调用**scan()**时它也会被扫描到，并且它里面配置的所有**@Bean**方法会在**refresh()**的时候被注册到容器中。

##### 使用AnnotationConfigWebApplicationContext支持web应用

一个**WebApplicationContext**与**AnnotationConfigApplicationContext**的变种是**AnnotationConfigWebApplicationContext**。这个实现可以用于配置Spring的**ContextLoaderListener**的servlet监听器、Spring MVC的**DispatcherServlet**等。下面是一个典型的配置Spring MVC web应用的片段。注意包含**contextClass**的context-param和init-param的用法：

```
<web-app>
    <!-- Configure ContextLoaderListener to use AnnotationConfigWebApplicationContext
        instead of the default XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration locations must consist of one or more comma- or space-delimited
        fully-qualified @Configuration classes. Fully-qualified packages may also be
        specified for component-scanning -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- Bootstrap the root application context as usual using ContextLoaderListener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- Declare a Spring MVC DispatcherServlet as usual -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- Configure DispatcherServlet to use AnnotationConfigWebApplicationContext
            instead of the default XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- Again, config locations must consist of one or more comma- or space-delimited
            and fully-qualified @Configuration classes -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- map all requests for /app/* to the dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>12345678910111213141516171819202122232425262728293031323334353637383940414243444546474849
```

#### 7.12.3 使用@Bean注解

**@Bean**是方法级别的注解，它与XML中的**<bean/>**类似，同样地，也支持**<bean/>**的一些属性，比如，init-method, destroy-method, autowring和name。

可以在**@Configuration**或**@Component**注解的类中使用**@Bean**注解。

##### 声明bean

只要在方法上简单的加上**@Bean**注解就可以定义一个bean了，这样就在**ApplicationContext**中注册了一个类型为方法返回值的bean。默认地，bean的名字为方法的名称，如下所示：

```
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl();
    }

}123456789
```

上面的方式与下面的XML形式等价：

```
<beans>
    <bean id="transferService" class="com.acme.TransferServiceImpl"/>
</beans>123
```

两种方式都定义了一个名字为**transferService**的bean，且绑定了**TransferServiceImpl**的实例：

```
transferService -> com.acme.TransferServiceImpl1
```

##### Bean之间的依赖

**@Bean**注解的方法可以有任意个参数用于描述这个bean的依赖关系。比如，如果**TransferService**需要一个**AccountRepository**，我们可以通过方法参数实现这种依赖注入。

```
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }

}123456789
```

这种机制与基于构造方法的依赖注入是完全相同的，更多详细内容可以查看[相关章节](https://blog.csdn.net/tangtong1/article/details/51960382#constructor-based-dependency-injection)。

##### 接收生命周期回调

任何使用**@Bean**定义的类都有正常的生命周期回调，并且可以使用**@PostConstruct**和**@PreDestroy**注解，更多详细信息请参考[JSR-250注解](https://blog.csdn.net/tangtong1/article/details/51960382#postconstruct-and-predestroy)。

正常的生命周期回调被完美支持， 如果一个bean实现了**InitializingBean, DisposableBean**或者**Lifecycle**，它们的方法将被容器依次调用。

同样地，也支持***Aware**系列的接口，比如BeanFactoryAware, BeanNameAware, MessageSourceAware, ApplicationContextAware等等。

**@Bean**注解也支持任意的初始化及销毁的回调方法，这与XML的init-method和destroy-method是非常类似的。

```
public class Foo {
    public void init() {
        // initialization logic
    }
}

public class Bar {
    public void cleanup() {
        // destruction logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public Foo foo() {
        return new Foo();
    }

    @Bean(destroyMethod = "cleanup")
    public Bar bar() {
        return new Bar();
    }

}1234567891011121314151617181920212223242526
```

- 默认地，使用Java配置的方式如果一个bean包含了公共的close或shutdown方法，它们将会被自动地包含在销毁的回调中。如果有公共的close或shutdown方法，但是我们并不想使用它们，那么只要加上**@Bean(destroyMethod=”“)**就可以屏蔽掉默认的推测了。 
  我们可以使用这种特性在通过JNDI获得的资源上，并且这些资源是外部应用管理的。特别地，保证一定要在DataSource上使用它，因为DataSource在Java EE的应用服务器上是有问题的。 
  `@Bean(destroyMethod="") public DataSource dataSource() throws NamingException { return (DataSource) jndiTemplate.lookup("MyDS"); } `
  同样地，使用**@Bean**方法，可以很容易地选择编程式地JNDI查找：使用Spring的JndiTemplate/JndiLocatorDelegate帮助类或直接使用JNDI的InitialContext，但是不要使用JndiObjectFactoryBean变种，因为它会强制你去声明一个返回类型作为FactoryBean的类型代替实际的目标类型，这会使得交叉引用变得很困难。

当然，上面例子中的Foo，也可以直接在构造期间直接调用init()方法：

```
@Configuration
public class AppConfig {
    @Bean
    public Foo foo() {
        Foo foo = new Foo();
        foo.init();
        return foo;
    }

    // ...

}123456789101112
```

- 在Java中可以用你喜欢的方式直接操作对象，而不需要总是依赖容器的生命周期！

##### 指定bean的作用域

###### 使用@Scope注解

可以使用任何标准的方式为@Bean注解的bean指定一个作用域，这些方式请参考[Bean作用域](https://blog.csdn.net/tangtong1/article/details/51960382#bean-scopes)章节。

默认地作用域为singleton，但是可以使用@Scope注解重写：

```
@Configuration
public class MyConfiguration {

    @Bean
    @Scope("prototype")
    public Encryptor encryptor() {
        // ...
    }

}12345678910
```

###### @Scope和scoped-proxy

Spring提供了一种简便地方式声明bean的作用域，它被称为scoped proxy。最简单地方式是创建那么一个代理，使用XML配置的形式则使用**<aop:scoped-proxy/>**元素。在Java中使用@Scope注解配置bean的方式提供了与XML代理模式属性同样的功能。默认是没有代理的(ScopedProxyMode.No)，但是可以指定ScopedProxyMode.TARGET_CLASS或ScopedProxyMode.INTERFACES。

如果把xml形式改写为Java形式，看起来如下：

```
// an HTTP Session-scoped bean exposed as a proxy
@Bean
@SessionScope
public UserPreferences userPreferences() {
    return new UserPreferences();
}

@Bean
public Service userService() {
    UserService service = new SimpleUserService();
    // a reference to the proxied userPreferences bean
    service.setUserPreferences(userPreferences());
    return service;
}1234567891011121314
```

##### 自定义bean的名称

默认地，使用@Bean默认的方法名为其bean的名字，然而这项功能可以使用name属性重写：

```
@Configuration
public class AppConfig {

    @Bean(name = "myFoo")
    public Foo foo() {
        return new Foo();
    }

}123456789
```

##### bean的别名

正如[7.3.1 命名bean](https://blog.csdn.net/tangtong1/article/details/51960382#naming-beans)中所讨论地一样，有时候可以想要给同一个bean多个名字，亦即别名，@Bean注解的name属性就可以达到这样的目的， 你可以为其提供一个String的数组。

```
@Configuration
public class AppConfig {

    @Bean(name = { "dataSource", "subsystemA-dataSource", "subsystemB-dataSource" })
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }

}123456789
```

##### Bean描述

有时可能需要为一个bean提供更详细的描述。这对于监控bean很有用。

可以使用@Description注解为其添加一段描述：

```
@Configuration
public class AppConfig {

    @Bean
    @Description("Provides a basic example of a bean")
    public Foo foo() {
        return new Foo();
    }

}12345678910
```

#### 使用@Configuration注解

@Configuration是类级别的注解，这表示一个对象是bean定义的来源。@Configuration注解的类里面使用@Bean注解的方法声明bean。对其中的@Bean方法的调用也能实现内部bean的依赖。参考[7.12.1 基础概念：@Bean和@Configuration](https://blog.csdn.net/tangtong1/article/details/51960382#basic-concepts-bean-configuration)。

##### 注入内部依赖

当@Bean的方法对另外一个有依赖时，简单地调用另外一个@Bean注解的方法即可表达这种依赖：

```
@Configuration
public class AppConfig {

    @Bean
    public Foo foo() {
        return new Foo(bar());
    }

    @Bean
    public Bar bar() {
        return new Bar();
    }

}1234567891011121314
```

在上例中，foo这个bean通过构造函数注入接收了bar的引用。

- 这种方式仅仅适用于在@Configuration内部定义的@Bean方法。在普通的@Component类中不能声明内部依赖。

##### 查找方法注入

正如之前提到的，查找方法注入是一项很少使用到的先进的技术。它对于一个单例bean依赖另一个原型bean很有用。在Java中使用了一种很自然的方法来实现了这种模式。

```
public abstract class CommandManager {
    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();

        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
    return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}12345678910111213
```

使用Java配置，我们可以创建一个CommandManager的子类，实现其createCommand()方法，这样就可以让它查找到新的原型command对象。

```
@Bean
@Scope("prototype")
public AsyncCommand asyncCommand() {
    AsyncCommand command = new AsyncCommand();
    // inject dependencies here as required
    return command;
}

@Bean
public CommandManager commandManager() {
    // return new anonymous implementation of CommandManager with command() overridden
    // to return a new prototype Command object
    return new CommandManager() {
        protected Command createCommand() {
            return asyncCommand();
        }
    }
}123456789101112131415161718
```

##### 更多关于Java配置内部工作的信息

下面的例子显示了@Bean注解的方法被调用了两次：

```
@Configuration
public class AppConfig {

    @Bean
    public ClientService clientService1() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientService clientService2() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientDao clientDao() {
        return new ClientDaoImpl();
    }

}1234567891011121314151617181920212223
```

clientDao()方法在clientService1()和clientService2()中分别被调用了一次。由于这个方法创建了一个ClientDaoImpl的实例并返回了它，但是你可能希望有两个实例（每个service一个）。这种定义可能是有问题的：在Spring中，实例化的bean默认是单例的。这就是神奇的地方：所有的@Configuration类都会在启动的时候被CGLIB子类化。在子类中，所有的子类方法都会在调用父类的方法之前检查有没有缓存的bean，如果没有再创建一个新的实例。注意，从Spring3.2开始，classpath不再需要包含CGLIB了因为CGLIB相关的类已经被打包在org.springframework.cglib中了，并且可以直接使用。

- 这种行为可以会根据bean的作用域而变化，我们这里只讨论单例。
- 实际上还会有一些限制，因为CGLIB是在启动的时候动态地添加这些特性，所以配置的类不能是final的。不过，从4.3开始，任何构造方法都允许放置在配置类中，包含@Autowired或一个非默认的构造方法用于默认注入即可。如果想避免任何CGLIB带来的限制，考虑在非@Configuration类中使用@Bean方法，比如普通的@Component类。这样在@Bean方法之中跨方法调用就不会被拦截了，所以这样只能依赖于构造方法或方法组织的注入了。

#### 7.12.5 组合的Java配置

##### 使用@Import注解

与XML中使用**<import/>**一样用于模块化配置，@Import注解允许从另一个配置类中加载@Bean定义。

```
@Configuration
public class ConfigA {

     @Bean
    public A a() {
        return new A();
    }

}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }

}1234567891011121314151617181920
```

现在，我们不需要同时指定ConfigA.class和ConfigB.class了，只要明确地指定ConfigB即可：

```
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}1234567
```

这种方式简化了容器的实例化，只要处理一个类就行了，而不需要开发者记住大量的@Configuration类。

- 从Spring 4.2开始，@Import也可以支持对普通组件类的引用了，与AnnotationConfigApplicationContext.register()方法类似。这在避免组件扫描的时候很有用，使用少量的配置类作为切入点用于明确定义所有的组件类。

##### 在导入的@Bean定义上注入依赖

上面的例子可以很好地工作，但是太简单了。在大部分场景下，bean都会依赖另一个配置类中的bean。使用XML时，这没有什么问题，因为不需要编译，只要简单地声明ref=”someBean”即可，并信任Spring可以很好地处理它。当然，使用配置类时，Java编译器会有一些限制，那就是必须符合Java的语法。

幸运地，解决这个问题也很简单。正如我们之前讨论的，@Bean方法可以有任意的参数用于描述其依赖。让我们考虑一下实际的场景，有几个配置类，并且每个都依赖于其它的类：

```
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }

}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }

}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}12345678910111213141516171819202122232425262728293031323334353637
```

也可以有其它的方法来实现相同的结果。请记住@Configuration类也是容器中的一个bean：这意味着它们可以像其它bean一样使用@Autowired和@Value注解。

- 请确定你都是以最简单的方式注入的依赖。@Configuration类会在上下文初始化的早期被处理，所以它的依赖会在更早期被初始化。如果可能的话，请像上面这样使用参数化注入。

同样地，对于通过@Bean声明的BeanPostProcessor和BeanFactoryPostProcessor请谨慎对待。它们通常被声明为静态的@Bean方法，不会触发包含它们的配置类。另外，@Autowired和@Value在配置类本身上是不起作用的，因为它们太早被实例化了。

```
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }

}

@Configuration
public class RepositoryConfig {

    private final DataSource dataSource;

    @Autowired
    public RepositoryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}1234567891011121314151617181920212223242526272829303132333435363738394041424344454647
```

- @Configuration类中的构造方法注入只在Spring 4.3以后才支持。另外请注意，如果目标bean只有一个构造方法也可以不指定@Autowried，在上例中，RepositoryConfig构造方法上的@Autowired是非必要的。

在上面的场景中，@Autowired可以很好地工作，并提供希望的结果，但是被装配的bean的定义声明是模糊不清的。例如，当一个开发者查看ServiceConfig类时，你怎么知道@Autowired AccountRepository在哪定义的呢？它在代码中并不清楚，并且这可以很微小。记住Spring工具套件提供了一些工具，可以画出所有东西是怎么装配起来的——这可以是你需要的。同样地，你的IDE也可以很容易地找出所有的定义和AccountRepository类型引用的地方，并可以快速地找出@Bean方法定义的地方。

在某些情况下，这种含糊是不被接受的，并且你希望可以在IDE中直接从一个@Configuration类到另一个，可以考虑装配配置类本身：

```
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        // navigate 'through' the config class to the @Bean method!
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }

}12345678910111213
```

在上面的场景下，AccountRepository的定义就很明确了。然而，ServiceConfig与RepositoryConfig耦合了；这是一种折衷的方法。这种耦合某种程度上可以通过接口或抽象解决，如下：

```
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}

@Configuration
public interface RepositoryConfig {

    @Bean
    AccountRepository accountRepository();

}

@Configuration
public class DefaultRepositoryConfig implements RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(...);
    }

}

@Configuration
@Import({ServiceConfig.class, DefaultRepositoryConfig.class}) // import the concrete config!
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}12345678910111213141516171819202122232425262728293031323334353637383940414243444546
```

现在ServiceConfig就与具体的DefaultRepositoryConfig松耦合了，并且内置的IDE工具也可以生效：对于 开发者可以很容易地获得RepositoryConfig实现类的继承体系。使用这种方式，操纵@Configuration类和它们的依赖与基于接口的代码没有区别。

##### 有条件地包含@Configuration类或@Bean方法

有时候有条件地包含或不包含一个@Configuration类或@Bean方法很有用，这基于特定的系统状态。一种通用的方法是使用@Profile注解去激活bean，仅当指定的配置文件包含在了Spring的环境中才有效（[参考7.13.1 bean定义配置文件](https://blog.csdn.net/tangtong1/article/details/51960382#bean-definition-profile)）。

@Profile注解实际是实现了一个更灵活的注解@Conditional。@Condition注解表明一个@Bean被注册之前会先询问@Condition。

Condition接口的实现只要简单地提供matches(…)方法，并返回true或false即可。例如，下面是一个实际的Condition实现用于@Profile：

```
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    if (context.getEnvironment() != null) {
        // Read the @Profile annotation attributes
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                    return true;
                }
            }
            return false;
        }
    }
    return true;
}12345678910111213141516
```

更多信息请参考@Conditional的javadoc。

##### 绑定Java与XML配置

Spring的@Configuration类并不能100%地替代XML配置。一些情况下使用XML的命名空间仍然是最理想的方式来配置容器。在某些场景下，XML是很方便或必要的，你可以选择以XML为主，比如ClassPathXmlApplicationContext，或者以Java为主使用AnnotationConfigApplicationContext并在需要的时候使用@ImportResource注解导入XML配置。

###### XML为主使用@Configuration类

更受人喜爱的方法是从XML启动容器并包含@Configuration类。例如，在大型的已存在的系统中，以前是使用XML配置的，所以很容易地创建@Configuration类，并包含他们到XML文件中，下面我们会讲解以XML为主的案例。

记住@Configuration类仅仅用于bean的定义。在这个例子中，我们创建了一个叫做AppConfig的配置类，并把它包含到system-test-config.xml中。因为**<context:annotation-config/>**打开了，所以容器能够识别到@Configuration注解并处理其中的@Bean方法。

```
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }

}1234567891011121314151617
```

system-test-config.xml:

```
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>12345678910111213
```

jdbc.properties:

```
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=123
```

```
public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}12345
```

- 在上面的system-test-config.xml中，AppConfig的<bean/>并没有声明一个id。当然如果声明了也是可以授受的，但是对于没有被其它bean引用的bean并有必要声明id，并且它也没可能从容器获取一个明确的名字。同样地，DataSource也不需要一个明确的id，因为它是通过类型装配的。

因为@Configuration是被元注解@Component注解的，所以@Configuration注解的类也可以被自动扫描。同样使用上面的例子，我们可以重新定义system-test-config.xml来使用组件扫描。注意，这种情况下，我们就没必要明确地声明<context:annotation-config/>了，因为<context:component-scan/&tl;已经包含了同样的功能。

system-test-config.xml:

```
<beans>
    <!-- picks up and registers AppConfig as a bean definition -->
    <context:component-scan base-package="com.acme"/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>1234567891011
```

###### 以@Configuration类为主使用@ImportResource引入XML

在一些应用中，@Configuration类是主要的配置方式，也需要使用一些XML配置。在这些场景下，简单地使用@ImportResource并按需要定义XML文件即可。这种方式可以以Java为主，并保持少量的XML配置。

```
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }

}12345678910111213141516171819
```

properties-config.xml

```
<beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>123
jdbc.properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=1234
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```