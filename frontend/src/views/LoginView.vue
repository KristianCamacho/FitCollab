<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const correo = ref('')
const contrasena = ref('')
const error = ref('')
const cargando = ref(false)

const iniciarSesion = async () => {
  error.value = ''
  cargando.value = true

  try {
    const respuesta = await api.post('/auth/login', {
      correo: correo.value,
      contrasena: contrasena.value,
    })

    localStorage.setItem('usuario', JSON.stringify(respuesta.data))

    router.push('/inicio')
  } catch (err) {
    if (err.response?.status === 401) {
      error.value = 'Correo o contraseña incorrectos'
    } else {
      error.value = 'No se pudo conectar con el servidor'
    }
  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <main class="login-container">
    <section class="login-card">
      <h1>FitCollab</h1>
      <h2>Iniciar sesión</h2>

      <form @submit.prevent="iniciarSesion">
        <label for="correo">Correo</label>

        <input
          id="correo"
          v-model="correo"
          type="email"
          placeholder="correo@ejemplo.com"
          required
        />

        <label for="contrasena">Contraseña</label>

        <input
          id="contrasena"
          v-model="contrasena"
          type="password"
          placeholder="Contraseña"
          required
        />

        <p v-if="error" class="error">
          {{ error }}
        </p>

        <button type="submit" :disabled="cargando">
          {{ cargando ? 'Ingresando...' : 'Ingresar' }}
        </button>
      </form>
    </section>
  </main>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 100%;
  max-width: 380px;
  padding: 32px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

input {
  padding: 10px;
}

button {
  margin-top: 12px;
  padding: 10px;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
}

.error {
  color: red;
}
</style>