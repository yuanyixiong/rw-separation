/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName DataSourceConfig
 * @Description TODO
 * @Date 2018/06/29 12:02
 * @Author shixun.online
 * @Version 1.0
 **/
@Configuration
public class MyBatisConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("masterDataSource1")
    DataSource masterDataSource1;

    @Autowired
    @Qualifier("masterDataSource2")
    DataSource masterDataSource2;

    @Autowired
    @Qualifier("slaveDataSource1")
    DataSource slaveDataSource1;

    @Autowired
    @Qualifier("slaveDataSource2")
    DataSource slaveDataSource2;

    @Value("${datasource.readSize}")
    private Integer readSize;

    @Value("${datasource.writeSize}")
    private Integer writeSize;

    /**
     * 把所有数据源都设置到路由中
     *
     * @return
     */
    @Bean
    public AbstractRoutingDataSource dataSouceProxy() {

        //重写数据源获取方式
        AbstractRoutingDataSource proxy = new AbstractRoutingDataSource() {
            private AtomicInteger readCount = new AtomicInteger(0);
            private AtomicInteger writeCount = new AtomicInteger(0);

            @Override
            protected Object determineCurrentLookupKey() {
                DataSourceContext.DataSourceType key = DataSourceContext.getDataSourceType();
                if (key == null) {
                    throw new NullPointerException("数据库路由时，决定使用哪个数据库源类型不能为空...");
                }

                //简单轮询的负载均衡
                switch (key) {
                    case SLAVE:
                        int readKey = readCount.getAndAdd(1) % readSize;
                        System.err.println("使用数据库：" + DataSourceContext.DataSourceType.SLAVE + "_" + (readKey + 1));
                        return DataSourceContext.DataSourceType.SLAVE + "_" + (readKey + 1);
                    case MASTER:
                        int writeKey = writeCount.getAndAdd(1) % writeSize;
                        System.err.println("使用数据库：" + DataSourceContext.DataSourceType.MASTER + "_" + (writeKey + 1));
                        return DataSourceContext.DataSourceType.MASTER + "_" + (writeKey + 1);
                }
                return null;
            }
        };

        //设置数据源
        Map<Object, Object> targetDataSource = new HashMap<Object, Object>();
        targetDataSource.put(DataSourceContext.DataSourceType.MASTER + "_1", masterDataSource1);
        targetDataSource.put(DataSourceContext.DataSourceType.MASTER + "_2", masterDataSource2);
        targetDataSource.put(DataSourceContext.DataSourceType.SLAVE + "_1", slaveDataSource1);
        targetDataSource.put(DataSourceContext.DataSourceType.SLAVE + "_2", slaveDataSource2);

        proxy.setTargetDataSources(targetDataSource);//设置多数据源
        proxy.setDefaultTargetDataSource(slaveDataSource1);//设置默认数据源
        return proxy;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        //设置数据源
        sqlSessionFactoryBean.setDataSource(dataSouceProxy());

        //设置mapper xml 和 mapper interface
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourceResolver.getResources("classpath:/com/example/springbootdsproxy/mapper/*.xml");
        sqlSessionFactoryBean.setMapperLocations(resources);//加载：Mapper接口的xml

        //设置分页插件
        Interceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        interceptor.setProperties(properties);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{interceptor});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSouceProxy());
    }
}
