<script setup>
import { onMounted, ref } from 'vue'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const edad = ref(null)
const peso = ref(null)
const altura = ref(null)
const objetivo = ref('')
const restriccionesAlimenticias = ref('')

const mensaje = ref('')
const error = ref('')
const guardando = ref(false)

const cargarFicha = async () => {
  if (!usuario) {
    return
  }

  try {
    const respuesta = await api.get(
      `/deportistas/${usuario.id}/ficha-personal`,
    )

    edad.value = respuesta.data.edad
    peso.value = respuesta.data.peso
    altura.value = respuesta.data.altura
    objetivo.value = respuesta.data.objetivo
    restriccionesAlimenticias.value =
      respuesta.data.restriccionesAlimenticias
  } catch (err) {
    if (err.response?.status !== 404) {
      console.error(err)
      error.value = 'No se pudo cargar la información personal'
    }
  }
}

onMounted(() => {
  cargarFicha()
})

const guardarFicha = async () => {
  mensaje.value = ''
  error.value = ''

  if (!usuario) {
    error.value = 'No hay un usuario autenticado'
    return
  }

  guardando.value = true

  try {
    await api.put(`/deportistas/${usuario.id}/ficha-personal`, {
      edad: edad.value,
      peso: peso.value,
      altura: altura.value,
      objetivo: objetivo.value,
      restriccionesAlimenticias: restriccionesAlimenticias.value,
    })

    mensaje.value = 'Información personal guardada correctamente'
  } catch (err) {
    console.error(err)
    error.value = 'No se pudo guardar la información personal'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <main class="perfil-container">
    <section class="perfil-card">
      <h1>Perfil</h1>
      <h2>Información personal</h2>

      <form @submit.prevent="guardarFicha">
        <label for="edad">Edad</label>
        <input
          id="edad"
          v-model.number="edad"
          type="number"
          min="1"
          required
        />

        <label for="peso">Peso (kg)</label>
        <input
          id="peso"
          v-model.number="peso"
          type="number"
          min="1"
          step="0.1"
          required
        />

        <label for="altura">Altura (m)</label>
        <input
          id="altura"
          v-model.number="altura"
          type="number"
          min="0.5"
          step="0.01"
          required
        />

        <label for="objetivo">Objetivo</label>
        <textarea
          id="objetivo"
          v-model="objetivo"
          placeholder="Ej: aumentar masa muscular"
          required
        ></textarea>

        <label for="restricciones">
          Restricciones alimenticias
        </label>

        <textarea
          id="restricciones"
          v-model="restriccionesAlimenticias"
          placeholder="Ej: intolerancia a la lactosa"
        ></textarea>

        <p v-if="mensaje" class="exito">
          {{ mensaje }}
        </p>

        <p v-if="error" class="error">
          {{ error }}
        </p>

        <button type="submit" :disabled="guardando">
          {{ guardando ? 'Guardando...' : 'Guardar información' }}
        </button>
      </form>
    </section>
  </main>
</template>

<style scoped>
.perfil-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.perfil-card {
  width: 100%;
  max-width: 520px;
  padding: 32px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

input,
textarea {
  padding: 10px;
}

textarea {
  min-height: 80px;
  resize: vertical;
}

button {
  margin-top: 12px;
  padding: 10px;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
}

.exito {
  color: green;
}

.error {
  color: red;
}
</style>