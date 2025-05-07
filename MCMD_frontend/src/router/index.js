import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/manage',
    name: 'Manage',
    component: () => import('../views/Manage.vue')
  },
  {
    path: '/material/:id',
    name: 'MaterialDetail',
    component: () => import('../views/MaterialDetail.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 如果用户使用浏览器的前进/后退按钮，则恢复到之前的位置
    if (savedPosition) {
      return savedPosition
    }
    // 否则，滚动到页面顶部
    return { top: 0 }
  }
})

export default router
