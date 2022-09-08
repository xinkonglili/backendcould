package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    //在实现类里面调用了websocketServer中有实现游戏的api，startGame
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("start game: " + aId + " " + bId);
        //websocketServer中有实现游戏的api，叫startGame
        WebSocketServer.startGame(aId, bId);
        return "start game success";
    }
}
