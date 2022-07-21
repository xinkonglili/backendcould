<template>
<div ref = "parent" class="gamemap">
    <canvas ref="canvas" tabindex = "0"></canvas>

</div>
    
</template>


<script>
import { GameMap } from "../assets/script/GameMap";
import {ref, onMounted} from "vue";  //onMounted,组件挂载完需执行哪些函数

export default {
    setup() {
        let parent = ref(null);
        let canvas = ref(null);

        onMounted(()=>{
            new GameMap(canvas.value.getContext('2d'), parent.value)
        });


        return{
            parent,
            canvas
        }

    }
}
</script>


<style scoped>
div.gamemap{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center; /*水平居中*/
    align-items: center;/*竖直居中*/
}

</style>


