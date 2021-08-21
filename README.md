MyBatis Generator Zero(MBGZ)

# 改造说明

- 本项目是为了给 [web-project-template](https://github.com/giiiiiithub/web-project-template) 项目自动化生成模板代码。
- 基于MBG 1.4.0改造，原项目地址: `https://github.com/mybatis/generator/tree/mybatis-generator-1.4.0`

改造内容如下：
- 在mapper xml包含以下方法:
    - boolean save(XxxVo xxxVo)
    - boolean update(XxxVo xxxVo)
    - List<Xxx> find(XxxVo XxxVo)
    - Xxx findById(IdType id)
    - boolean delete(IdType id)
    - int deleteByIds(List<XType> ids)
    XxxVo表示入参, Xxx表示model, IdType表示主键在java中的类型
- 在mapper xml中生成 baseSelect语句，默认对单表查询。在find和findById方法中include该sql。
- 新增生成java mapper空接口，并继承`IMapper`接口，该接口定义在`web-project-template`项目中
- 新增生成model类，对应表字段的封装。
- 新增生成vo类，对应接口请求参数的封装。
- 新增生成XxxService接口和XxxServiceImpl实现类。
- 新增生成XxxController类，实现基本的增删改查功能。

# 关于主键

本项目区别0主键，1主键，多主键，不同数量的主键生成的crud xml、serviceImpl, controller会有些许不同。

## xml区别：
- 0主键 包含：`save, find, update, delete`, 因为没有id，所以没有`deleteById, deleteByIds, findById`
- 1主键 包含：`save, find, update, delete, deleteById, deleteByIds, findById`
- 多主键 包含：`save, find, update, delete, deleteById, findById`

## serviceImpl区别：
- 0主键： 对`deleteById, deleteByIds, findById`三个方法重写，直接抛出异常，禁止操作。
- 多主键 对`deleteByIds`方法重写，调用`deleteBatch`方法实现

## controller区别：
- 0主键 包含：`save, find, update, delete` 因为没有id，所以去掉了`deleteById, findById`
- 1主键 包含：`save, find, update, delete, deleteById, findById`
- 多主键 包含：`save, find, update, delete, findById`, 多主键情况下，可以用`delete`代替`deleteById`

# 使用方法

##  MBG配置文件
- context常量属性:`id="simple" targetRuntime="MyBatis3SimpleZero"`
- context属性：targetPackage表示项目包，targetProject表示项目绝对路径
- javaClientGenerator常量属性`type="ZEROXMLMAPPER"`
- sqlMapGenerator的`targetPackage`属性表示mapper xml存放位置,在项目的: `src/main/resources/{targetPackage}`位置
- javaModelVoGenerator元素,表示生成vo类
- javaServiceGenerator元素,表示生成service接口和实现类
- javaControllerGenerator元素,表示生成controller类, 属性`controllerPath`表示请求路径，也就是为class上的`@RequestMapping`指定值
- 其他配置参考mybatis-generator项目文档。

以下为配置示例:
```xml
<!DOCTYPE generatorConfiguration PUBLIC
        "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">


<generatorConfiguration>
  <context id="simple" targetRuntime="MyBatis3SimpleZero"
           targetPackage="com.zero.template" targetProject="C:\web-project-template">

    <commentGenerator>
      <property name="suppressAllComments" value="true" />
    </commentGenerator>

    <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                    connectionURL="jdbc:mysql://localhost:3306/web_template?user=xxxx&amp;password=yyyy" />

    <javaModelGenerator/>

    <javaModelVoGenerator/>

    <javaServiceGenerator/>

    <javaControllerGenerator controllerPath="sys_user" />

    <sqlMapGenerator targetPackage="mappers"/>

    <javaClientGenerator type="ZEROXMLMAPPER" />


    <table tableName="sys_user" alias="sys_user" schema="web_template" catalog="web_template">

    </table>
  </context>
</generatorConfiguration>

```

## 快速使用

sample目录下提供了直接可用的代码配置。进入sample，修改generatorConfig.xml，双击generate.bat。代码将自动生成。


## 生成代码
1. 进入core/mybatis-generator-core目录，执行`mvn clean package -Dmaven.test.skip=true`编译打包该模块
2. 将mybatis-generator-core-1.4.0.jar, jdbc驱动jar, 配置文件放在同意目录下，执行如下命令:
`java -cp mybatis-generator-core-1.4.0.jar;mysql-connector-java-8.0.20.jar org.mybatis.generator.api.ShellRunner -configfile generatorConfig.xml`