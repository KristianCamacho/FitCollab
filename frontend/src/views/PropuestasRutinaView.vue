<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const propuestas = ref([])
const mensaje = ref('')
const procesandoId = ref(null)

async function cargarPropuestas() {
  if (!usuario) return

  try {
    const respuesta = await api.get(`/rutinas/entrenador/${usuario.id}/propuestas`)
    propuestas.value = respuesta.data
  } catch {
    mensaje.value = 'No se pudieron cargar las rutinas propuestas'
  }
}

async function responder(rutinaId, aceptar) {
  procesandoId.value = rutinaId

  try {
    await api.put(`/rutinas/${rutinaId}/${aceptar ? 'aceptar' : 'rechazar'}`)
    propuestas.value = propuestas.value.filter((r) => r.id !== rutinaId)
  } catch (error) {
    mensaje.value =
      error.response?.data?.message ||
      error.response?.data ||
      'No se pudo procesar la respuesta'
  } finally {
    procesandoId.value = null
  }
}

onMounted(cargarPropuestas)
</script>

<template>
  <div class="propuestas-rutina">
    <h2>Rutinas propuestas por deportistas</h2>

    <p v-if="mensaje" class="error">{{ mensaje }}</p>

    <div v-if="propuestas.length" class="lista">
      <article v-for="rutina in propuestas" :key="rutina.id" class="propuesta">
        <header>
          <h3>{{ rutina.nombre }}</h3>
          <span class="deportista">{{ rutina.deportistaNombre }}</span>
        </header>

        <p v-if="rutina.descripcion">{{ rutina.descripcion }}</p>

        <p class="detalle">
          {{ rutina.duracionMinutos }} min · Intensidad {{ rutina.intensidad }}
        </p>

        <div class="acciones">
          <button
            class="aceptar"
            :disabled="procesandoId === rutina.id"
            @click="responder(rutina.id, true)"
          >
            {{ procesandoId === rutina.id ? 'Procesando...' : 'Aceptar' }}
          </button>

          <button
            class="rechazar"
            :disabled="procesandoId === rutina.id"
            @click="responder(rutina.id, false)"
          >
            {{ procesandoId === rutina.id ? 'Procesando...' : 'Rechazar' }}
          </button>
        </div>
      </article>
    </div>

    <p v-else-if="!mensaje">No tienes rutinas propuestas pendientes de validación</p>
  </div>
</template>

<style scoped>
.propuestas-rutina {
  max-width: 720px;
  margin: 0 auto;
  padding: 1.5rem;
}

h2 {
  margin-bottom: 1.5rem;
}

.lista {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.propuesta {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.propuesta header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.deportista {
  font-size: 0.8rem;
  font-weight: 600;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background: #eef4ff;
  color: #2c7be5;
  white-space: nowrap;
}

.detalle {
  color: #666;
  font-size: 0.9rem;
}

.acciones {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.acciones button {
  flex: 1;
  padding: 0.6rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.aceptar {
  background: #198754;
  color: #fff;
}

.rechazar {
  background: #dc3545;
  color: #fff;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #c62828;
  font-weight: 600;
}
</style>