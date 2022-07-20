import { ACGameObject } from "./ACGameObject";

export class Wall extends ACGameObject{
    constructor(r,c,gamemap){
        super();
        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226";

    }

    update(){
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        ctx.fillStyle = this.color;
        ctx.fillRect(this.c*L, this.r*L, L, L); //渲染一个小方块

    }
}