//蛇里面的一个格子
export class Cell{
    constructor(r,c)
    {
        this.r = r;
        this.c = c;
        this.x = c + 0.5;
        this.y = r + 0.5; //中心点坐标


    }

}