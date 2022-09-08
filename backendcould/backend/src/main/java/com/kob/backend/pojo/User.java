package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//帮我们实get和set方法的注解
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //id自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private Integer rating;

    public User(int userId, String username, String encode) {
    }
}
