<script setup>
import { onMounted, ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
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
  if (!usuario || usuario.rol !== 'DEPORTISTA') return

  try {
    const respuesta = await api.get(`/deportistas/${usuario.id}/ficha-personal`)

    edad.value = respuesta.data.edad
    peso.value = respuesta.data.peso
    altura.value = respuesta.data.altura
    objetivo.value = respuesta.data.objetivo
    restriccionesAlimenticias.value = respuesta.data.restriccionesAlimenticias
  } catch (err) {
    if (err.response?.status !== 404) {
      console.error(err)
      error.value = 'No se pudo cargar la información personal'
    }
  }
}

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

const deportistas = ref([])
const mensajeDeportistas = ref('')

const cargarDeportistas = async () => {
  if (!usuario || usuario.rol !== 'ENTRENADOR') return

  try {
    const respuesta = await api.get(`/entrenadores/${usuario.id}/deportistas`)
    deportistas.value = respuesta.data

    if (deportistas.value.length === 0) {
      mensajeDeportistas.value =
        'El sistema muestra un mensaje de error indicando que no hay deportistas asignados'
    }
  } catch {
    mensajeDeportistas.value = 'No se pudieron cargar tus deportistas'
  }
}

const tieneDeportistas = computed(() => deportistas.value.length > 0)

onMounted(() => {
  cargarFicha()
  cargarDeportistas()
})
</script>

<template>
  <main class="perfil-container">
    <section v-if="usuario?.rol === 'DEPORTISTA'" class="perfil-card">
      <h1>Perfil</h1>
      <h2>Información personal</h2>

      <form @submit.prevent="guardarFicha">
        <label for="edad">Edad</label>
        <input id="edad" v-model.number="edad" type="number" min="1" required />

        <label for="peso">Peso (kg)</label>
        <input id="peso" v-model.number="peso" type="number" min="1" step="0.1" required />

        <label for="altura">Altura (m)</label>
        <input id="altura" v-model.number="altura" type="number" min="0.5" step="0.01" required />

        <label for="objetivo">Objetivo</label>
        <textarea
          id="objetivo"
          v-model="objetivo"
          placeholder="Ej: aumentar masa muscular"
          required
        ></textarea>

        <label for="restricciones">Restricciones alimenticias</label>
        <textarea
          id="restricciones"
          v-model="restriccionesAlimenticias"
          placeholder="Ej: intolerancia a la lactosa"
        ></textarea>

        <p v-if="mensaje" class="exito">{{ mensaje }}</p>
        <p v-if="error" class="error">{{ error }}</p>

        <button type="submit" :disabled="guardando">
          {{ guardando ? 'Guardando...' : 'Guardar información' }}
        </button>
      </form>
    </section>

    <section v-else-if="usuario?.rol === 'ENTRENADOR'" class="perfil-card ancho">
      <h1>Perfil</h1>
      <h2>Mis deportistas</h2>

      <div v-if="tieneDeportistas" class="lista-deportistas">
        <article v-for="deportista in deportistas" :key="deportista.id" class="deportista">
          <strong>{{ deportista.nombre }} {{ deportista.apellido }}</strong>
          <span>{{ deportista.correo }}</span>
        </article>
      </div>

      <p v-else class="error">{{ mensajeDeportistas }}</p>

      <RouterLink v-if="tieneDeportistas" class="boton-crear-rutina" to="/crear-rutina">
        Crear rutina para mis deportistas
      </RouterLink>
    </section>

    <section v-else class="perfil-card">
      <h1>Perfil</h1>
      <p><strong>Nombre:</strong> {{ usuario?.nombre }} {{ usuario?.apellido }}</p>
      <p><strong>Correo:</strong> {{ usuario?.correo }}</p>
      <p><strong>Rol:</strong> {{ usuario?.rol }}</p>
    </section>
  </main>
</template>

<style scoped>
.perfil-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
}

.perfil-card {
  width: 100%;
  max-width: 520px;
  padding: 32px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.perfil-card.ancho {
  max-width: 680px;
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
  color: #c62828;
  font-weight: 600;
}

.lista-deportistas {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin: 1rem 0;
}

.deportista {
  display: flex;
  flex-direction: column;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.deportista span {
  font-size: 0.85rem;
  color: #666;
}

.boton-crear-rutina {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.7rem 1.2rem;
  background: #2c7be5;
  color: #fff;
  border-radius: 6px;
  font-weight: 600;
}
</style>