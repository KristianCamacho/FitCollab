<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

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
  <main class="inicio">
    <h1>Inicio</h1>

    <div v-if="usuario">
      <p>Bienvenido, {{ usuario.nombre }} {{ usuario.apellido }}</p>
      <p>Rol: {{ usuario.rol }}</p>

      <button @click="cerrarSesion">
        Cerrar sesión
      </button>
    </div>
  </main>
</template>

<style scoped>
.inicio {
  padding: 32px;
}

button {
  margin-top: 16px;
  padding: 10px 16px;
  cursor: pointer;
}
</style>
