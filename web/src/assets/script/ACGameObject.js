const AC_Game_Object = [];

export class ACGameObject{
    constructor(){
        AC_Game_Object.push(this);
        this.timedelta = 0; //2帧之间的时间间隔
        this.has_exected = false; //物体是否执行过

    }

    start(){//只执行一次

    }

    update(){//每刷新一帧执行一次

    }

    on_destory(){ //在删除之前执行

    }

    destory(){
        this.on_destory();
        for(let i in AC_Game_Object){
            const obj = AC_Game_Object[i];
            if(obj == this){
                AC_Game_Object.splice(i);
                break;
            }
        }
    }

}

let last_timestamp; //上次执行的时刻
const step = timestamp => {
    for(let obj of AC_Game_Object){  //of is value   ...   in is index
        if(!obj.has_exected){
            obj.has_exected = true;
            obj.start();
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step);
}

requestAnimationFrame(step)
