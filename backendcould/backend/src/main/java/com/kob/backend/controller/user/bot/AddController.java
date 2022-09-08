package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class AddController {
    //把刚刚实现的接口注入进来
    @Autowired
    private AddService addService;

    @PostMapping("/api/user/bot/add/")
    public Map<String ,String> add(@RequestParam Map<String,String> data){
        return addService.add(data);
    }
}
