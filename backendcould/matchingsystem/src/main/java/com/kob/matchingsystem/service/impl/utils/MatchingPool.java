package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//加注解@Component才可以把其他的类注入进来
@Component
public class MatchingPool extends Thread{
    //players多个线程共用的，就会有读写冲突问题，所以要加锁
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock(); //定义一个锁
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";
    //注入进来
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating){
        lock.unlock();
        try {
            players.add(new Player(userId,rating,0));
        } finally {
            lock.unlock();
        }

    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>(); //把没有被删除的用户存下来
            for (Player player : players){
                if (!player.getUserId().equals(userId)){
                    newPlayers.add(player);
                }
            }

            players = newPlayers;

        } finally {
            lock.unlock();
        }

    }

    private void increaseWaitingTime(){ //将当前所有的玩家等待时间加1
        for (Player player : players){
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b){ //判断2名玩家是否匹配
        //先计算2名玩家的分差
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        //计算2名玩家的等待时间
        int waitingTime = Math.min(a.getWaitingTime() , b.getWaitingTime());
        return ratingDelta <= waitingTime * 10; //如果分差小于等于等待时间*10，就匹配成功

    }

    //如果匹配要返回结果
    private void sendResult(Player a, Player b){
        System.out.println("sent result:" + a + " " + b);
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        restTemplate.postForObject(startGameUrl,data,String.class); //发请求
    }

    private void matchPlayers(){
        System.out.println("match players:" + players.toString());
        //已经匹配的玩家给存下来
        boolean[] used = new boolean[players.size()];
        //等待时间长的玩家应该要先匹配，越往前的玩家越新，越往后的玩家越老，所以应该从后开始向前扫描
        for (int i = 0;  i < players.size() ; i++) {
            if (used[i]) continue; //如果这名玩家已经被匹配过了，没匹配过，就循环找人和他匹配
            for (int j = i+1; j < players.size() ; j++) {
                if (used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if (checkMatched(a,b)){ //如何匹配，单独封装成一个函数
                    used[i] = used[j] = true;//若匹配成功
                    sendResult(a,b);//返回结果
                    break;
                }
            }
        }

        //把已经匹配过的玩家给删掉
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (!used[i]){
                newPlayers.add(players.get(i));
            }
        }

        players = newPlayers; //更新players

    }


    //开线程要重载函数
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime(); //2个函数都要对Players操作
                    matchPlayers();
                } finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

    }
}
