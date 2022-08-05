package com.kob.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    //如何在controller里面调用数据库的接口，接口在userMapper里面----必须加个注解@Autowired
    @Autowired
    UserMapper userMapper;

    @GetMapping("/user/all")
    public List<User> getAll(){
        return userMapper.selectList(null);
    }

    //查询某个用户
    @GetMapping("/user/{userId}/")
    public List<User> getUser(@PathVariable int userId){ //返回一堆用户
        //使用复杂的sql语句，要使用条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);//根据指定的条件（url），查询某一个用户
        //queryWrapper.ge("id",2).le("id",3);//输入合规的url，会返回查询id>=2 && <=3 的用户
        return userMapper.selectList(queryWrapper);
    }

    //可以添加相同id的用户
    @GetMapping("/add/user/{userId}/{username}/{password}") //http://localhost:3000/add/user/5/%22e%22/pe%22/  e带字符串符号了
    public String addUser(
            @PathVariable int userId,
            @PathVariable String username,
            @PathVariable String password)
    {
        if (password.length() < 6){
            System.out.println("密码太短了");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(password);
        User user = new User(userId,username,encode);//http://localhost:3000/add/user/5/f/pf
        userMapper.insert(user);
        return "Add user Successfully!";
    }

    @GetMapping("del/user/{userId}") //http://localhost:3000/del/user/5
    public String delUser(@PathVariable int userId){
        userMapper.deleteById(userId); //都是由mybatis_plus实现的
        return "del user Successfully!";
    }

}
