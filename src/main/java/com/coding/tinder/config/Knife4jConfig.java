package com.coding.tinder.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description Knife4j config
 * @ClassName Knife4jConfig
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfig {
    @Bean
    public Docket buildDocket() {

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .select()
                //这里指定扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.coding.tinder.contorller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
    private ApiInfo buildApiInfo() {
        Contact contact = new Contact("接口文档","https://github.com/ChickenAreYouSoBeautiful","3372563469@qq.com");
        return new ApiInfoBuilder()
                .title("用户中心接口文档")
                .description("用户中心接口文档")
                .contact(contact)
                .version("1.0.0").build();
    }
}
