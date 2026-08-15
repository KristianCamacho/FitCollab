<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'

const rutinas = ref([])
const rutinaSeleccionada = ref(null)
const calificacion = ref(null)
const mensaje = ref('')
const tipoMensaje = ref('')
const cargando = ref(false)

const puntajes = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

async function cargarRutinas() {
  try {
    const respuesta = await api.get('/rutinas')
    rutinas.value = respuesta.data
  } catch {
    mensaje.value = 'No se pudieron cargar las rutinas'
    tipoMensaje.value = 'error'
  }
}

async function enviarCalificacion() {
  mensaje.value = ''

  if (!rutinaSeleccionada.value) {
    mensaje.value = 'Debes seleccionar una rutina'
    tipoMensaje.value = 'error'
    return
  }

  if (!calificacion.value) {
    mensaje.value = 'Debes seleccionar una calificación'
    tipoMensaje.value = 'error'
    return
  }

  cargando.value = true

  try {
    await api.post(`/rutinas/${rutinaSeleccionada.value}/calificacion`, {
      calificacion: calificacion.value,
    })

    mensaje.value = 'Calificación registrada correctamente'
    tipoMensaje.value = 'exito'
    calificacion.value = null
    await cargarRutinas()
  } catch (error) {
    tipoMensaje.value = 'error'
    mensaje.value = error.response?.data?.message
      || 'Ocurrió un error al registrar la calificación'
  } finally {
    cargando.value = false
  }
}

onMounted(cargarRutinas)
</script>

<template>
  <div class="calificar">
    <h2>Calificar rutina</h2>

    <label for="rutina">Selecciona la rutina</label>
    <select id="rutina" v-model="rutinaSeleccionada">
      <option :value="null" disabled>-- Elige una rutina --</option>
      <option v-for="rutina in rutinas" :key="rutina.id" :value="rutina.id">
        {{ rutina.nombre }} ({{ rutina.duracionMinutos }} min)
      </option>
    </select>

    <p class="etiqueta">¿Cómo evalúas esta rutina? (1 = muy mala, 10 = excelente)</p>

    <div class="puntajes">
      <button
        v-for="valor in puntajes"
        :key="valor"
        type="button"
        :class="{ activo: calificacion === valor }"
        @click="calificacion = valor"
      >
        {{ valor }}
      </button>
    </div>

    <button class="enviar" :disabled="cargando" @click="enviarCalificacion">
      {{ cargando ? 'Enviando...' : 'Enviar calificación' }}
    </button>

    <p v-if="mensaje" :class="tipoMensaje">{{ mensaje }}</p>
  </div>
</template>

<style scoped>
.calificar {
  max-width: 520px;
  margin: 0 auto;
  padding: 1.5rem;
}

label,
.etiqueta {
  display: block;
  margin: 1rem 0 0.5rem;
  font-weight: 600;
}

select {
  width: 100%;
  padding: 0.6rem;
  font-size: 1rem;
}

.puntajes {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.puntajes button {
  width: 44px;
  height: 44px;
  font-size: 1rem;
  cursor: pointer;
  border: 1px solid #999;
  background: #fff;
  border-radius: 6px;
}

.puntajes button.activo {
  background: #2c7be5;
  color: #fff;
  border-color: #2c7be5;
}

.enviar {
  margin-top: 1.5rem;
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
  cursor: pointer;
  background: #2c7be5;
  color: #fff;
  border: none;
  border-radius: 6px;
}

.enviar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.exito {
  margin-top: 1rem;
  color: #1a7f37;
  font-weight: 600;
}

.error {
  margin-top: 1rem;
  color: #c62828;
  font-weight: 600;
}
</style>