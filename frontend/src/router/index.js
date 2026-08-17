import { createRouter, createWebHistory } from 'vue-router'
import CrearRutinaView from '../views/CrearRutinaView.vue'
import MisRutinasView from '../views/MisRutinasView.vue'
import ProponerRutinaView from '../views/ProponerRutinaView.vue'
import PropuestasRutinaView from '../views/PropuestasRutinaView.vue'

import LoginView from '../views/LoginView.vue'
import InicioView from '../views/InicioView.vue'
import PerfilView from '../views/PerfilView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import CalificarRutinaView from '../views/CalificarRutinaView.vue'

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

        {
          path: 'crear-rutina',
          name: 'crear-rutina',
          component: CrearRutinaView,
          meta: { rolesPermitidos: ['ENTRENADOR'] },
        },

        {
          path: 'propuestas-rutina',
          name: 'propuestas-rutina',
          component: PropuestasRutinaView,
          meta: { rolesPermitidos: ['ENTRENADOR'] },
        },

        {
          path: 'mis-rutinas',
          name: 'mis-rutinas',
          component: MisRutinasView,
          meta: { rolesPermitidos: ['DEPORTISTA'] },
        },

        {
          path: 'calificar-rutina',
          name: 'calificar-rutina',
          component: CalificarRutinaView,
          meta: { rolesPermitidos: ['DEPORTISTA'] },
        },

        {
          path: 'proponer-rutina',
          name: 'proponer-rutina',
          component: ProponerRutinaView,
          meta: { rolesPermitidos: ['DEPORTISTA'] },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const usuarioGuardado = localStorage.getItem('usuario')
  const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

  if (to.matched.some((ruta) => ruta.meta.requiereAutenticacion) && !usuario) {
    return '/login'
  }

  if (to.path === '/login' && usuario) {
    return '/inicio'
  }

  const rolesPermitidos = to.meta.rolesPermitidos

  if (rolesPermitidos && usuario && !rolesPermitidos.includes(usuario.rol)) {
    return '/inicio'
  }
})

export default router