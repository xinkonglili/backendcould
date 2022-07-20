import { createRouter, createWebHistory } from 'vue-router'
import PKIndexView from '../views/pk/PKIndexView.vue'
import RankListIndexView from '../views/ranklist/RankListIndexView.vue'
import RecordIndexView from '../views/record/RecordIndexView.vue'
import BotsIndexView from '../views/user/bots/BotsIndexView.vue'
import NotFound from '../views/error/NotFound.vue'

const routes = [

  {
    path: "/",  //重定向到pk页面
    name:"home",
    redirect:"/pk/"
  },

  {
    path: "/pk/",
    name: "pk_index",
    component: PKIndexView,
  },

  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RankListIndexView,
  },

  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
  },

  {
    path: "/bots/",
    name: "bots_index",
    component: BotsIndexView,
  },

  {
    path: "/error/",
    name: "error_index",
    component: NotFound,
  },


  {
    path: "/:catchAll(.*)",  //匹配所有不合法路径
    name:"invalid",
    redirect:"/error",

  },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
