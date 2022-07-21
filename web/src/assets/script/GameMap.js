import { ACGameObject } from "./ACGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends ACGameObject{
    constructor(ctx,parent){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0; //表示一个单位的长度
        

        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 40;
        this.walls = [];

        //创建2条蛇
        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows-2, c: 1}, this),
            new Snake({id: 1, color:"#F94848", r: 1, c: this.cols-2},this),
        ];

    }


    check_connectivity(g, sx, sy, tx, ty){ //判断是否连通的，无论障碍物有多少，一定得有一条连通的路。地图，起点横纵坐标，终点横纵坐标
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
            g[0][c] = g[this.rows-1][c] = true;
        }

        //创建随机障碍物
        for(let i = 0; i < this.inner_walls_count / 2; i++){
            for(let j = 0; j < 1000; j++){
                let r = parseInt(Math.random() * this.rows);//(Math.random() [0,1]---[0,this.rows],在这个范围内随机方块
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c] || g[this.rows-1-r][this.cols-1-c]) continue;
                //把左下角和右下角初始化蛇的地方清掉
                if(r == this.rows-2 && c == 1 || c == this.cols - 2 && r == 1){
                    continue;
                }

                g[r][c] = g[this.rows-1-r][this.cols-1-c] = true;
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

    add_listening_evens(){
        this.ctx.canvas.focus();

        const [snake0,snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if(e.key === 'w') snake0.set_direction(0);
            else if (e.key === 'd') snake0.set_direction(1);
            else if(e.key === 's') snake0.set_direction(2);
            else if(e.key === 'a') snake0.set_direction(3);
            else if(e.key === 'ArrowUp') snake1.set_direction(0);
            else if(e.key === 'ArrowRight') snake1.set_direction(1);
            else if(e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
        });
    }
  

    start(){
        for(let i = 0 ; i < 1000; i++)
            if(this.create_walls())
                break;
        this.add_listening_evens();
    }
 

  

    update_size(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols,  this.parent.clientHeight / this.rows)); //去掉方格之间的间隙
        this.ctx.canvas.width  = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    //判断2条蛇是否准备好了下一个回合
    check_ready(){
        for(const snake of this.snakes){
            if(snake.status !== "idle") return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    next_step(){//让两条蛇进入下一回合
        for(const snake of this.snakes){
            snake.next_step();
        }
    }


    check_vaild(cell){ //检测合法性
        for(const wall of this.walls){
            if(wall.r === cell.r && wall.c === cell.c){
                return false;
            }
        }

        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increating()){ //当蛇尾前进的时候，蛇尾不要判断
                k--;
            }

            for(let i =0; i < k; i++){
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c){
                    return false;
                }
            }
        }

        return true;

    }

    update(){
        this.update_size();
        if(this.check_ready()){
            this.next_step();
        }
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