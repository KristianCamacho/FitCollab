import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/LoginView.vue'
import InicioView from '../views/InicioView.vue'
import PerfilView from '../views/PerfilView.vue'
import MainLayout from '../layouts/MainLayout.vue'

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
      path: '/',
      component: MainLayout,
      meta: {
        requiereAutenticacion: true,
      },
      children: [
        {
          path: 'inicio',
          name: 'inicio',
          component: InicioView,
        },
        {
          path: 'perfil',
          name: 'perfil',
          component: PerfilView,
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const usuario = localStorage.getItem('usuario')

  if (to.matched.some((ruta) => ruta.meta.requiereAutenticacion) && !usuario) {
    return '/login'
  }

  if (to.path === '/login' && usuario) {
    return '/inicio'
  }
})

export default router
