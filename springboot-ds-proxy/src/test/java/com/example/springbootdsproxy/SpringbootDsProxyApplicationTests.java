package com.example.springbootdsproxy;

import com.example.springbootdsproxy.dto.User;
import com.example.springbootdsproxy.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootDsProxyApplicationTests {

    @Autowired
    private UserService userService;

    //测试读写分离整合
    @Test
    public void read() {
        userService.read();
    }

    @Test
    public void write() {
        userService.write();
    }

    //测试读写分离数据库操作
    @Test
    public void save() {
        System.out.println(userService.save(new User(1, "Arvin1")));
        System.out.println(userService.save(new User(2, "Arvin2")));
        System.out.println(userService.save(new User(3, "Arvin3")));
    }
    @Test
    public void delete() {
        userService.delete(1);
        userService.delete(2);
        userService.delete(3);
    }
    @Test
    public void getUser() {
        System.out.println(userService.getUser(1));
        System.out.println(userService.getUser(2));
        System.out.println(userService.getUser(3));
    }

    @Test
    public void getUsers() {
        userService.getUsers().stream().forEach((user)->{
            System.out.println(user);
        });
    }
}
