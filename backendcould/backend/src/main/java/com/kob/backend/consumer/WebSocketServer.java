package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
//每次来一个链接就是new一个WebSocketServer的实例
//WebSocket---每来一个链接都会新开一个线程来维护
/*WebSocketServer client1 = new WebSocketServer();
WebSocketServer client2 = new WebSocketServer();*/


public class WebSocketServer {
    //存储所有的链接
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    //开个线程池来存储玩家，当线程池里面的玩家数量有2个人，就会匹配
    private User user;//判断链接是谁--通过id找
    private Session session = null;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate; //可以在2个springboot之间进行通信
    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove";
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;

    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;//建立连接的时候把session存下来
        System.out.println("connect!");
        Integer userId = JwtAuthentication.getUserId(token);//把id取出来
        this.user = userMapper.selectById(userId);

        if (this.user != null){
            users.put(userId, this);
        }else {
            this.session.close();
        }

    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("Connect Close!");
        if (this.user != null){
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer bId){
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        //这里使用了多线程，重新new一个地图类，然后start一个线程
        Game game = new Game(13,14,20, a.getId(), b.getId());
        game.createMap();
        if (users.get(a.getId())!= null) //确保有一方断开链接
            users.get(a.getId()).game = game;
        if (users.get(b.getId())!= null)
            users.get(b.getId()).game = game;

        game.start();


        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map",game.getG());


        //给a传信息
        JSONObject respA = new JSONObject();
        respA.put("event","start-matching");
        respA.put("opponent_username",b.getUsername());
        respA.put("opponent_photo",b.getPhoto());
        respA.put("game",respGame);
        if (users.get(a.getId())!= null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        //给b传信息
        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        if (users.get(b.getId())!= null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private  void startMatching(){
        System.out.println("start-matching");
        //向后端发请求，有个工具，要先配置一下--RestTemplate
        //定义请求的类型
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);//发请求，java的反射机制
    }

    private void stopMatching(){
        System.out.println("stop-matching");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);

    }

    private void move(int direction){
        if (game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if (game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {  //当作路由
        // 从Client（后端）接收消息
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event"); //前端有个事件是event
        if("start-matching".equals(event)){
            startMatching();
        }else if ("stop-matching".equals(event)){
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){ //从后端向前端发送信息
        //client发消息是异步的，所以要加锁synchronized
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

