package com.feng.zxyf.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 手动配置 MyBatis 与 PageHelper
 * <p>
 * 说明：由于项目基于 Spring Boot 4.1（Spring Framework 7），
 * mybatis-spring-boot-starter / pagehelper-spring-boot-starter 的自动配置
 * （面向 Boot3 编写）不会生效，因此这里手动装配 SqlSessionFactory、
 * SqlSessionTemplate 并向 MyBatis 注册 PageHelper 分页拦截器。
 */
@Configuration
public class MybatisConfig {

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // 开启驼峰命名自动映射：create_time -> createTime
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setMapUnderscoreToCamelCase(true);
        config.setArgNameBasedConstructorAutoMapping(true);
        factory.setConfiguration(config);

        // 注入 PageHelper 分页拦截器
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties props = new Properties();
        props.setProperty("helperDialect", "mysql");
        props.setProperty("reasonable", "true");
        props.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(props);
        factory.setPlugins(new Interceptor[]{pageInterceptor});

        return factory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}