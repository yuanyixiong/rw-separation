/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.config;

/**
 * 当前线程记录读写key
 * @ClassName DataSourceContext
 * @Description TODO
 * @Date 2018/06/29 11:27
 * @Author shixun.online
 * @Version 1.0
 **/
public class DataSourceContext {

    public enum DataSourceType {
        MASTER, SLAVE
    }

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType dataSourceType) {
        if (dataSourceType == null) throw new NullPointerException();
        contextHolder.set(dataSourceType);
    }

    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }

    public static void clearDataSourceType() {
        contextHolder.remove();
    }
}
