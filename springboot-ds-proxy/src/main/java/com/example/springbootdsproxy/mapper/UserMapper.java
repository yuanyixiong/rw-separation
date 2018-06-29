/********************************************
 * Copyright (c) , shixun.online
 *
 * All rights reserved
 *
 *********************************************/
package com.example.springbootdsproxy.mapper;

import com.example.springbootdsproxy.dto.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Date 2018/06/29 14:16
 * @Author shixun.online
 * @Version 1.0
 **/
@Mapper
public interface UserMapper {

    @Insert("insert into user(id,name)values(#{id},#{name})")
    int save(User user);

    @Select("select * from user where id=#{id}")
    User getUser(int id);

    @Select("select * from user")
    List<User> getUsers();

    @Delete("delete from user where id=#{id}")
    int delete(int id);
}
