import { ACGameObject } from "./ACGameObject";
import { Cell } from "./Cell";

export class Snake extends ACGameObject{
    constructor(info,gamemap){//传2个参数，蛇基本信息和地图
        super();
        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;
        this.cells = [new Cell(info.r,info.c)]; //存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null;//下一步的目的地在哪


        this.speed = 5;//蛇每秒移动多少格子             
        this.direction = -1; //-1表示没有指令，0，1，2，3表示上右下左
        this.status = "idle";//idle:表示静止，move：表示正在移动，die：表示死亡

        this.dr = [-1,0,1,0];//行偏移量
        this.dc = [0,1,0,-1];//列

        this.step = 0; //表示回合数
        this.eps = 1e-2;  //允许的误差0.001



    }


    start(){

    }

    set_direction(d){
        this.direction = d;   
    }   
    
    check_tail_increating(){//检测当前回合，蛇的长度是否增加
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;  //过10步以后，每增加3步变长一步
        return false;

    }

    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d],this.cells[0].c + this.dc[d]);
        this.direction = -1; //清空操作
        this.status = "move"; 
        this.step++;  
        
        const k = this.cells.length;
        for(let i =k; i > 0; i--){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
        }

        if(!this.gamemap.check_vaild(this.next_cell)){  //下一步操作撞了，蛇直接去世
            this.status = "die";
        }
    }

    update_move(){
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance  < this.eps){ //走到目标点了
            this.cells[0] = this.next_cell; //添加一个新蛇头
            this.next_cell = null;
            this.status = "idle"; //走完了，停下来

            if(!this.check_tail_increating()){
                this.cells.pop();//蛇没有变长，砍掉蛇尾
            }
        

        }else{
            const move_distance = this.speed * this.timedelta/1000; //每2帧之间走的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy /distance;

            if(this.check_tail_increating()){
                const k =  this.cells.length;
                const tail = this.cells[k-1], tail_target = this.cells[k-2]; //把tail移到tail_target
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y +=  move_distance * tail_dy / distance;
            }
        }

    }

    update(){//每一帧执行一次
        if(this.status ===  "move"){
            this.update_move();
        }
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;

        if(this.status === "die"){
            ctx.fillStyle = "white";
        }

        for(const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);  //半径，画半弧的起始和终止
            ctx.fill();
        }

        for(let i = 1; i< this.cells.length; i++){
            const a = this.cells[i-1], b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) continue;

            if(Math.abs(a.x - b.x) < this.eps){
                ctx.fillRect((a.x - 0.4) * L,  Math.min(a.y, b.y) * L,  L* 0.8,  Math.abs(a.y - b.y) * L  );
            }else{
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L,  Math.abs(a.x - b.x ) * L ,  L* 0.8);
            }



        }
       
    }

}
