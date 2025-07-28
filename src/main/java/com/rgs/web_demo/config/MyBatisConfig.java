package com.rgs.web_demo.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan(basePackages = "com.rgs.web_demo.mapper")
public class MyBatisConfig {

    // 1. SqlSessionFactory 설정
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml")
        );
        sessionFactoryBean.setTypeAliasesPackage("com.rgs.web_demo.domain");

        return sessionFactoryBean.getObject();
    }

    // 2. SqlSessionTemplate 등록 (선택사항)
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // 3. 필요 시 TypeHandler 추가 가능
    // 예: Enum <-> String 매핑 등
    /*
    @Bean
    public TypeHandler<MyEnumType> myEnumTypeHandler() {
        return new MyEnumTypeHandler();
    }
    */
}
