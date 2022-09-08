package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//按住alt+insert
@Service
public class AddServiceImpl implements AddService {

    //把数据库引入进来
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        //取出当前的user--背下来
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        //把插入的信息取出来
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");


        //判断
        Map<String , String> map = new HashMap<>();

        if (title == null || title.length() == 0){
            map.put("error_message","标题不能为空");
            return map;
        }

        if (title.length() > 100 ){
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        if (description == null && description.length() == 0){
            description = "这个用户很神秘，什么也没有留下~";
        }

        if (description.length() > 300 ){
            map.put("error_message", "Bot描述的长度不能大于300");
            return map;
        }

        if (content == null && content.length() == 0){
            map.put("error_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000){
            map.put("error_message", "代码长度不能大于10000");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(),title,description,content, now, now);
        botMapper.insert(bot);
        map.put("error_message", "success");

        return map;
    }
}
