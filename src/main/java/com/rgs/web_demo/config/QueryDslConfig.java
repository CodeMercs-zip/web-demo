package com.rgs.web_demo.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

    private final DataSource dataSource;
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(this.entityManager);
    }

    @Bean
    public SQLQueryFactory sqlQueryFactory() {
        OracleTemplates templates = new OracleTemplates();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(
                templates);
        return new SQLQueryFactory(configuration, dataSource);
    }
}

