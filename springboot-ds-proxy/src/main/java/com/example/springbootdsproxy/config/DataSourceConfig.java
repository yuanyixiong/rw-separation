/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName DataSourceConfig
 * @Description TODO
 * @Date 2018/06/29 14:26
 * @Author shixun.online
 * @Version 1.0
 **/
@Configuration
public class DataSourceConfig {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean(name = "masterDataSource1")
    @ConfigurationProperties(prefix = "datasource.master1")
    public DataSource masterDataSource1() {
        logger.info("-----初始化master1数据库配置-----");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "masterDataSource2")
    @ConfigurationProperties(prefix = "datasource.master2")
    public DataSource masterDataSource2() {
        logger.info("-----初始化master2数据库配置-----");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "slaveDataSource1")
    @ConfigurationProperties(prefix = "datasource.slave1")
    public DataSource slaveDataSource1() {
        logger.info("-----初始化slave1数据库配置-----");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    @Bean(name = "slaveDataSource2")
    @ConfigurationProperties(prefix = "datasource.slave2")
    public DataSource slaveDataSource2() {
        logger.info("-----初始化slave2数据库配置-----");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }
}
