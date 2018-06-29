/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.service;

import com.example.springbootdsproxy.config.ReadDataSource;
import com.example.springbootdsproxy.config.WriteDataSource;
import com.example.springbootdsproxy.dto.User;
import com.example.springbootdsproxy.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName UserService
 * @Description TODO
 * @Date 2018/06/29 11:25
 * @Author shixun.online
 * @Version 1.0
 **/
@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ReadDataSource
    public void read() {
        System.out.println("读取数据");
    }

    @WriteDataSource
    public void write() {
        System.out.println("写入数据");
    }

    @Autowired
    private UserMapper userMapper;

    @ReadDataSource
    public List<User> getUsers() {
        return userMapper.getUsers();
    }

    @ReadDataSource
    public User getUser(int id) {
        return userMapper.getUser(id);
    }

    @WriteDataSource
    public int save(User user) {
        return userMapper.save(user);
    }

    @WriteDataSource
    public int delete(int id) {
        return userMapper.delete(id);
    }
}
