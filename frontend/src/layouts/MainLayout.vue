<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'

const router = useRouter()

const usuario = computed(() => {
  const datos = localStorage.getItem('usuario')

  if (!datos) {
    return null
  }

  return JSON.parse(datos)
})

const cerrarSesion = () => {
  localStorage.removeItem('usuario')
  router.push('/login')
}
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="marca">
        <h1>FitCollab</h1>
        <span>Fitness colaborativo</span>
      </div>

      <nav class="navegacion">
        <RouterLink to="/inicio">
          Inicio
        </RouterLink>

        <RouterLink to="/perfil">
          Perfil
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'ENTRENADOR'"
          to="/crear-rutina"
        >
          Crear rutina
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'ENTRENADOR'"
          to="/propuestas-rutina"
        >
          Rutinas propuestas
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'DEPORTISTA'"
          to="/mis-rutinas"
        >
          Mis rutinas
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'DEPORTISTA'"
          to="/calificar-rutina"
        >
          Calificar rutina
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'DEPORTISTA'"
          to="/proponer-rutina"
        >
          Proponer rutina
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'DEPORTISTA'"
          to="/mi-especialista"
        >
          Mi equipo profesional
        </RouterLink>

        <RouterLink
          v-if="usuario?.rol === 'ADMINISTRADOR'"
          to="/admin/solicitudes"
        >
          Solicitudes de cambio
        </RouterLink>
      </nav>

      <div class="usuario" v-if="usuario">
        <div>
          <strong>
            {{ usuario.nombre }} {{ usuario.apellido }}
          </strong>

          <span>
            {{ usuario.rol }}
          </span>
        </div>

        <button @click="cerrarSesion">
          Cerrar sesión
        </button>
      </div>
    </aside>

    <main class="contenido">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
}

.sidebar {
  width: var(--sidebar-width);
  min-height: 100vh;

  display: flex;
  flex-direction: column;

  padding: 28px 20px;

  background: var(--color-primary);
  color: white;
}

.marca h1 {
  margin: 0;
  font-size: 28px;
}

.marca span {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.75;
}

.navegacion {
  display: flex;
  flex-direction: column;
  gap: 8px;

  margin-top: 40px;
}

.navegacion a {
  padding: 12px 14px;
  border-radius: 8px;

  transition:
    background-color 0.2s,
    transform 0.2s;
}

.navegacion a:hover {
  background: rgba(255, 255, 255, 0.12);
}

.navegacion a.router-link-active {
  background: var(--color-accent);
  color: var(--color-text);
  font-weight: 600;
}

.usuario {
  margin-top: auto;

  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.usuario strong,
.usuario span {
  display: block;
}

.usuario span {
  margin-top: 4px;
  font-size: 12px;
  opacity: 0.7;
}

.usuario button {
  width: 100%;
  margin-top: 14px;
  padding: 10px;

  border-radius: 8px;

  background: rgba(255, 255, 255, 0.12);
  color: white;

  cursor: pointer;
}

.usuario button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.contenido {
  flex: 1;
  min-width: 0;
  padding: 40px;

  background: var(--color-background);
}

@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    min-height: auto;
  }

  .navegacion {
    margin-top: 20px;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .usuario {
    margin-top: 20px;
  }

  .contenido {
    padding: 20px;
  }
}
</style>