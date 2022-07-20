import { ACGameObject } from "./ACGameObject";
import { Wall } from "./Wall";

export class GameMap extends ACGameObject{
    constructor(ctx,parent){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0; //表示一个单位的长度

        this.rows = 13;
        this.cols = 13;

        this.inner_walls_count = 40;
        this.walls = [];

    }


    check_connectivity(g, sx, sy, tx, ty){ //地图，起点横纵坐标，终点横纵坐标
        if(sx == tx && sy == ty){
            return true;
        }
        g[sx][sy] = true;//把当前标记已经走过了
        let dx = [-1,0,1,0] ,dy = [0,1,0,-1]//上，右，下，左    
        for(let i = 0; i < 4; i++){
            let x = sx + dx[i];
            let y = sy + dy[i];
            if(!g[x][y] && this.check_connectivity(g,x,y,tx,ty)){//没有撞墙，并且可以搜到终点
                return true;
            }
        }

        return false;
    }

    //使用二维数组
    create_walls(){
        const g = [];
        for(let r = 0; r<this.rows;r++){
            g[r] = [];
            for(let c = 0; c<this.cols;c++){
                g[r][c] = false;
            }
        }

        //给四周加上障碍物
        for(let r =0; r<this.rows;r++){
            g[r][0] = g[r][this.cols-1] = true;   //先给外面一层加上墙    
        }

        for(let c =0;c<this.cols;c++){
            g[0][c] = g[this.cols-1][c] = true;
        }

        //创建随机障碍物
        for(let i = 0; i < this.inner_walls_count / 2; i++){
            for(let j = 0; j < 1000; j++){
                let r = parseInt(Math.random() * this.rows);//(Math.random() [0,1]---[0,this.rows],在这个范围内随机方块
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c] || g[c][r]) continue;
                //把左下角和右下角初始化蛇的地方清掉
                if(r == this.rows-2 && c == 1 || c == this.cols - 2 && r == 1){
                    continue;
                }

                g[r][c] = g[c][r] = true;
                break;  
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        if(!this.check_connectivity(copy_g, this.rows-2, 1, 1, this.cols -2)) return false;

        for(let r = 0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }

        return true;
    }

  

    start(){
        for(let i = 0 ; i < 1000; i++){
            if(this.create_walls()){
                break;
            }
        }
    }

    update_size(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols,  this.parent.clientHeight / this.rows)); //去掉方格之间的间隙
        this.ctx.canvas.width  = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update(){
        this.update_size();
        this.render();

    }

    render(){ //渲染，把游戏对象画到地图上
        const color_even = "#AAD751"  //偶数格子
        const color_odd = "#A2D149";//奇数格子
        for(let r =0; r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if((r+c)%2==0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }

                this.ctx.fillRect(c*this.L, r*this.L,this.L,this.L); //坐标加小矩形边长
            }
        }
        
    }
}