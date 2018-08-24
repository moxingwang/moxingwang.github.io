---
layout: post
title: '如何在SpringBoot里使用SwaggerUI'
date: 2018-08-24
author: M.莫
cover: 'source/article/article_beautiful-beauty-blond-289225.jpg'
tags: swagger
---

## Swagger
> Swagger是一种和语言无关的规范和框架，用于定义服务接口，主要用于描述RESTful的API。它专注于为API创建优秀的文档和客户端库。支持Swagger的API可以为API方法生成交互式的文档，让用户可以通过以可视化的方式试验，查看请求和响应、头文件和返回代码，从而发现API的功能。

## SpringBoot嵌入SwaggerUI
### 步骤

#### 1.jar包引入

````
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.2.2</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.2.2</version>
    <scope>compile</scope>
</dependency>
````

#### 2.基于SpringBoot配置SwaggerConfig

````
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket newsApi() {
        //return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.any()).build();
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		docket.enable(true);
		docket.apiInfo(apiInfo()).select().paths(PathSelectors.any()).build();
		return docket;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("订单中心测试平台").description("在这里你可以浏览项目所有接口，并提供相关测试工具")
				.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open").contact("test")
				.license("China Red Star Licence Version 1.0").licenseUrl("#").version("1.0").build();
	}
}

````

#### 3.WebConfig配置说明

> 这里有一个需要注意的问题，让WebConfig去继承WebMvcAutoConfigurationAdapter而不是直接继承WebMvcConfigurerAdapter，否则Swagger的页面出不来。

````
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcAutoConfigurationAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        return converter;
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/jsp");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public StandardServletMultipartResolver getStandardServletMultipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
````

#### 4.SwaggerUI页面访问

````
http://localhost:8080/projectName/swagger-ui.html#!/
````