package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    //做数据库查询
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
       Map<String, String>  map  = new HashMap<>();
       if(username == null){
           map.put("error_message", "用户名不能为空");
           return map;
       }
       if (password == null || confirmPassword == null){
           map.put("error_message", "密码不能为空");
           return map;
       }

       username = username.trim(); //删掉首位的空白字符，包括制表符，回车等
       if(username.length() == 0){
           map.put("error_message","用户名不能为空");
           return map;
       }

       if (username.length() > 100){
           map.put("error_message", "用户名长度不能大于100");
           return map;

       }

       if (password.length() > 100 ||confirmPassword.length() > 100){
           map.put("error_message", "密码长度不能大于100");
           return map;
       }

       if (password.length() == 0 || confirmPassword.length() == 0){
           map.put("error_message", "密码不能为空");
           return map;
       }

       if (!password.equals(confirmPassword)){
           map.put("error_message","二次密码不一致");
           return map;
       }

       //从数据库查询是不是有用户名等于username的用户
        QueryWrapper<User>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        //将结果存到list里面
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("error_message","用户名已存在");
            return map;
        }

        //上面的验证都存在时，把用户存到数据库里面
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/112937_lg_c525959216.jpg";
        User user = new User(null,username,encodedPassword,photo,1500);
        userMapper.insert(user);//把用户加到数据库里面
        map.put("error_message", "success");
        return map;

    }
}
