package org.yx.annotation;

/**
 * Created by 杨欣 on 2018/6/21.
 */

/**
 * 一,什么是注解
 *   注解是java5以后引入的一个新特征.作用于程序元素上:包,类,构造方法,方法,成员变量和参数上,起到
 * 配置,说明,解析和使用的功能
 *
 * 二,java自带注解
 *   @Override : 表示重写父类中的方法
 *   @Deprecated : 表示该方法不建议使用
 *   @SuppressWarnings : 忽略编译器的警告
 * 三,元注解 : 用于创建注解的注解
 *   @Target : 表示该注解作用范围
 *   @Retention : 表示该注解生命周期
 *   @Document : 表示是否将注解添加到javadoc中
 *   @Inherited : 表示是否在父类中寻找注解
 *   上面四种的常用的,网上大多介绍注解也都介绍了这四个注解,但是在java.lang.annotation包中除了这四个还有:
 *   @Native : Indicates that a field defining a constant value may be referenced from native code.
 *   @Repeatable : The annotation type java.lang.annotation.Repeatable is used to indicate that the
 *   annotation type whose declaration it (meta-)annotates is repeatable.
 *   对应的是api文档中的英文解释,用得少就不翻译了
 * 四,自定义注解
 *
 *
 */
public class Note {
}
