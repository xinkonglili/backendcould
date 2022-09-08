package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

//实现浏览器之间的地图同步
public class Game extends Thread{
     private final Integer rows;
     private final Integer cols;
     private final Integer inner_walls_count;
     private final int[][] g;//地图
     private final static int[] dx = {-1, 0, 1, 0}, dy = {0,1 ,0,-1}; //辅助数组
     private final Player playerA,playerB;

     private Integer nextStepA = null;
     private Integer nextStepB = null; //玩家的下一步操作，0，1，2，3 上下左右

    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; //playing-->finished
    private String loser = ""; //all:平局， A：A输 B：B输

    public Game(Integer rows, Integer cols, Integer inner_walls_count,Integer idA, Integer idB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows-2,1,new ArrayList<>());
        playerB = new Player(idB,1,cols-2,new ArrayList<>()); //蛇的初始坐标（1，cols-2）
    }

    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }

    //加锁--两个玩家走下一步的时候可能会冲突
    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }

    }

    public void setNextStepB(Integer nextStepB){  //从外部调用这2个函数，用来给nextStepA，nextStepB赋值
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }

    }

    public int[][] getG(){
        return g;
    }

    private boolean check_connectivity(int sx,int sy,int tx,int ty){
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4 ; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] ==0){
                if (check_connectivity(x,y,tx,ty)){
                    g[sx][sy] = 0;
                    return true;
                }
            }

        }



        g[sx][sy] = 0; //恢复现场
        return false;
    }

    private boolean draw(){ //画地图
        for (int i = 0; i < this.rows;i++){
            for (int j = 0; j < this.cols;j++){
                g[i][j] = 0; //把地图清空
            }
        }

        for (int r = 0; r < this.rows;r++){
            g[r][0] = g[r][this.cols-1] = 1;
        }

        for (int c =0; c < this.cols;c++){
            g[0][c] = g[this.rows-1][c] = 1;
        }

        Random random = new Random();
        for (int i =0; i < this.inner_walls_count/2; i++){
            for (int j = 0; j < 1000;j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1|| g[this.rows-1-r][this.cols-1-c] == 1)
                    continue;

                if (r == this.rows  - 2 && c == 1 || r == 1 && c == this.cols-2)
                    continue;

                g[r][c] = g[this.rows-1-r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2,1,1,this.cols-2);

    }

    public void  createMap(){
        for (int i = 0; i < 1000; i++){
            if (draw())  break;
        }
    }

    //下一步操作
    private boolean nextStep(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 如果某名玩家超过一定的时间还没有发生操作，我们要结束这个操作，同时告诉那名玩家没有进行操作
        for (int i = 0; i < 50; i++){
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){ //
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n-1; i++){
            if (cellsA.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n-1; i++){
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y){
                return false;
            }
        }

        return true; //如果没有找到矛盾
    }

    private void judge(){
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        //判断A,B的最后一步是不是合法的

        boolean validA = check_valid(cellsA,cellsB);
        boolean validB = check_valid(cellsB,cellsA);

        if (!validA || !validB){
            status = "finished";

            if (!validA && !validB){
                loser = "all";
            } else if (!validA){
                loser = "A";
            }else {
                loser = "B";
            }
        }

    }

    private void sendAllMessage(String message){ //像2名玩家广播信息
        if (WebSocketServer.users.get(playerA.getId())!= null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId())!= null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove(){ //向2个client传递移动信息
        //有读入操作加锁
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            //清空2名玩家的信息，为下次做准备
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }


    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult(){  //向2个玩家宣布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser",loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
        //saveToDatabase();
    }

    @Override
    public void run() {
        for (int i = 0; i<1000;i++){
            if (nextStep()){ //是否获取了两条蛇的下一步操作
                judge();

                if (status.equals("playing")){
                    sendMove();
                }else {
                    sendResult();
                    break;
                }
            }else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if (nextStepA == null){
                        loser = "A";
                    }else {
                        loser = "B";
                    }

                } finally {
                    lock.unlock();
                }
                sendResult();
                break;

            }
        }
    }
}
