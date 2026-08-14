import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/LoginView.vue'
import InicioView from '../views/InicioView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/inicio',
      name: 'inicio',
      component: InicioView,
      meta: {
        requiereAutenticacion: true,
      },
    },
  ],
})

router.beforeEach((to) => {
  const usuario = localStorage.getItem('usuario')

  if (to.meta.requiereAutenticacion && !usuario) {
    return '/login'
  }

  if (to.path === '/login' && usuario) {
    return '/inicio'
  }
})

export default router
