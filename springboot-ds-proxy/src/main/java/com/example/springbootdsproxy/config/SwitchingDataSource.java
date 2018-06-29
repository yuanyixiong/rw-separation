/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 读写分离的数据库切面
 *
 * @ClassName SwitchingDataSource
 * @Description TODO
 * @Date 2018/06/29 11:32
 * @Author shixun.online
 * @Version 1.0
 **/
@Aspect
@Component
public class SwitchingDataSource {

    public static final Logger logger = LoggerFactory.getLogger(SwitchingDataSource.class);

    /**
     * 切换数据源为读类型的
     *
     * @param proceedingJoinPoint
     * @param read
     * @return
     * @throws Throwable
     */
    @Around("@annotation(read)")
    public Object proceedRead(ProceedingJoinPoint proceedingJoinPoint, ReadDataSource read) throws Throwable {
        try {
            System.out.println("设置读数据库");
            DataSourceContext.setDataSourceType(DataSourceContext.DataSourceType.SLAVE);
            Object result = proceedingJoinPoint.proceed();
            return result;
        } finally {
            DataSourceContext.clearDataSourceType();
            logger.info("restore database connection");
        }
    }

    /**
     * 切换数据源为写类型的
     *
     * @param proceedingJoinPoint
     * @param write
     * @return
     * @throws Throwable
     */
    @Around("@annotation(write)")
    public Object proceedWrite(ProceedingJoinPoint proceedingJoinPoint, WriteDataSource write) throws Throwable {
        try {
            System.out.println("设置写数据库");
            DataSourceContext.setDataSourceType(DataSourceContext.DataSourceType.MASTER);
            Object result = proceedingJoinPoint.proceed();
            return result;
        } finally {
            DataSourceContext.clearDataSourceType();
            logger.info("restore database connection");
        }
    }
}
