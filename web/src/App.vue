<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"


export default {
  components: {
    NavBar
  },
  setup() {
  }
}
</script>

<style>
body {
  background-image: url("./assets/images/oo.jpg");
  background-size: cover;
}
</style>

<!-- <template>
  <NavBar/>
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import $ from 'jquery'

export default {
  components :{
    NavBar
  },

  setup(){
    $.ajax({
      url:"http://127.0.0.1:3000/user/account/token/",
      type:"post",
      data:{
        username:'jinli',
        password:'pj',
      },

      success(resp){
        console.log(resp);
      },
      
      error(resp){
        console.log(resp);
      }
    });

    $.ajax({
      url:"http://127.0.0.1:3000/user/account/register/",
      type:"post",
      data:{
        username:"jinli3",
        password:"pj3",
        confirmedPassword:"pj3",
      },

      success(resp){
        console.log(resp);
      },

      error(resp){
        console.log(resp);
      }
    });

    $.ajax({
      url:"http://127.0.0.1:3000/user/account/info/",
      type:"get",
      headers:{
        Authorization:"Bearer "  + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmYWY5ZjgyNjJlNmU0Mjk4OTY1NDdlMmFhMWEzMzJkOCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY2MTQ4MzQyMywiZXhwIjoxNjYyNjkzMDIzfQ.pEfOcCo7PH_92o8l9egH5Q1lWenvpRbRQSPyWTudPfk"
      },

      success(resp){
        console.log(resp);
      },

      error(resp){
        console.log(resp);
      }
    });

  }
} 


</script>


<style>

body {
  background-image: url("./assets/images/oo.jpg");
  background-size: cover; 
}

</style> -->