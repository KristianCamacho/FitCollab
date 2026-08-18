import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/LoginView.vue'
import InicioView from '../views/InicioView.vue'
import PerfilView from '../views/PerfilView.vue'

import CrearRutinaView from '../views/CrearRutinaView.vue'
import MisRutinasView from '../views/MisRutinasView.vue'
import CalificarRutinaView from '../views/CalificarRutinaView.vue'
import ProponerRutinaView from '../views/ProponerRutinaView.vue'
import PropuestasRutinaView from '../views/PropuestasRutinaView.vue'

import MiEspecialistaView from '../views/MiEspecialistaView.vue'
import SolicitudesPendientesView from '../views/SolicitudesPendientesView.vue'

import CrearDietaView from '../views/nutricionista/CrearDietaView.vue'
import SolicitudesView from '../views/nutricionista/SolicitudesView.vue'
import DietasView from '../views/nutricionista/DietasView.vue'
import SolicitudDietaView from '../views/deportista/SolicitudDietaView.vue'

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

        {
          path: 'crear-rutina',
          name: 'crear-rutina',
          component: CrearRutinaView,

          meta: {
            rolesPermitidos: ['ENTRENADOR'],
          },
        },

        {
          path: 'propuestas-rutina',
          name: 'propuestas-rutina',
          component: PropuestasRutinaView,

          meta: {
            rolesPermitidos: ['ENTRENADOR'],
          },
        },

        {
          path: 'mis-rutinas',
          name: 'mis-rutinas',
          component: MisRutinasView,

          meta: {
            rolesPermitidos: ['DEPORTISTA'],
          },
        },

        {
          path: 'calificar-rutina',
          name: 'calificar-rutina',
          component: CalificarRutinaView,

          meta: {
            rolesPermitidos: ['DEPORTISTA'],
          },
        },

        {
          path: 'proponer-rutina',
          name: 'proponer-rutina',
          component: ProponerRutinaView,

          meta: {
            rolesPermitidos: ['DEPORTISTA'],
          },
        },

        {
          path: 'mi-especialista',
          name: 'mi-especialista',
          component: MiEspecialistaView,

          meta: {
            rolesPermitidos: ['DEPORTISTA'],
          },
        },

        {
          path: 'admin/solicitudes',
          name: 'admin-solicitudes',
          component: SolicitudesPendientesView,

          meta: {
            rolesPermitidos: ['ADMINISTRADOR'],
          },
        },

        {
          path: 'nutricionista/dietas/crear',
          name: 'crear-dieta',
          component: CrearDietaView,

          meta: {
            rolesPermitidos: ['NUTRICIONISTA'],
          },
        },

        {
          path: 'nutricionista/solicitudes',
          name: 'solicitudes-dieta',
          component: SolicitudesView,

          meta: {
            rolesPermitidos: ['NUTRICIONISTA'],
          },
        },

        {
          path: 'nutricionista/dietas',
          name: 'dietas',
          component: DietasView,

          meta: {
            rolesPermitidos: ['NUTRICIONISTA'],
          },
        },

        {
          path: 'deportista/solicitud-dieta',
          name: 'solicitud-dieta',
          component: SolicitudDietaView,

          meta: {
            rolesPermitidos: ['DEPORTISTA'],
          },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const usuarioGuardado =
    localStorage.getItem('usuario')

  const usuario =
    usuarioGuardado
      ? JSON.parse(usuarioGuardado)
      : null

  if (
    to.matched.some(
      (ruta) =>
        ruta.meta.requiereAutenticacion,
    ) &&
    !usuario
  ) {
    return '/login'
  }

  if (
    to.path === '/login' &&
    usuario
  ) {
    return '/inicio'
  }

  const rolesPermitidos =
    to.meta.rolesPermitidos

  if (
    rolesPermitidos &&
    usuario &&
    !rolesPermitidos.includes(
      usuario.rol,
    )
  ) {
    return '/inicio'
  }
})

export default router