package com.kob.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//Mapper是class里面的crud
public interface UserMapper extends BaseMapper<User> {
    //这里的东西由mybatis_plus实现
}
