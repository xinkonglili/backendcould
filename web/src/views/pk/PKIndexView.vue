<template>  
   <PlayGround />   
   

    <div class="d-grid gap-2 col-6 mx-auto" style="text-align: center;  width: 150px;">
        <button class="btn btn-primary" type="button">Restart a game</button>
    </div>

    
  
    
</template>


<script>
import PlayGround from '../../components/PlayGround.vue'

export default{
    components:{
        PlayGround
    }
}

</script>



<style scoped>

</style>