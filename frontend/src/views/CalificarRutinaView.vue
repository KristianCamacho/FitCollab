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

const usuario = JSON.parse(localStorage.getItem('usuario') || 'null')

async function cargarRutinas() {
  if (!usuario?.id) {
    mensaje.value = 'No se encontró la sesión del usuario'
    tipoMensaje.value = 'error'
    return
  }

  try {
    const respuesta = await api.get(
      `/rutinas/deportista/${usuario.id}`,
    )

    rutinas.value = respuesta.data.filter(
      (rutina) => rutina.estado === 'REALIZADA',
    )
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
    await api.post(
      `/rutinas/${rutinaSeleccionada.value}/calificacion`,
      {
        calificacion: calificacion.value,
      },
    )

    mensaje.value = 'Calificación registrada correctamente'
    tipoMensaje.value = 'exito'

    calificacion.value = null
    rutinaSeleccionada.value = null

    await cargarRutinas()
  } catch (error) {
    tipoMensaje.value = 'error'

    if (error.response?.status === 409) {
      mensaje.value = 'Rutina ya calificada'
    } else {
      mensaje.value =
        error.response?.data?.message ||
        'Ocurrió un error al registrar la calificación'
    }
  }
  finally {
    cargando.value = false
  }
}

onMounted(cargarRutinas)
</script>

<template>
  <div class="calificar">
    <h2>Calificar rutina</h2>

    <label for="rutina">
      Selecciona la rutina
    </label>

    <select
      id="rutina"
      v-model="rutinaSeleccionada"
    >
      <option
        :value="null"
        disabled
      >
        -- Elige una rutina --
      </option>

      <option
        v-for="rutina in rutinas"
        :key="rutina.id"
        :value="rutina.id"
      >
        {{ rutina.nombre }}
        ({{ rutina.duracionMinutos }} min)
      </option>
    </select>

    <p
      v-if="rutinas.length === 0"
      class="sin-rutinas"
    >
      No tienes rutinas realizadas pendientes de calificación.
    </p>

    <p class="etiqueta">
      ¿Cómo evalúas esta rutina?
      (1 = muy mala, 10 = excelente)
    </p>

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

    <button
      class="enviar"
      :disabled="cargando"
      @click="enviarCalificacion"
    >
      {{ cargando ? 'Enviando...' : 'Enviar calificación' }}
    </button>

    <p
      v-if="mensaje"
      :class="tipoMensaje"
    >
      {{ mensaje }}
    </p>
  </div>
</template>

<style scoped>
.calificar {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;

  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

h2 {
  margin-top: 0;
  color: var(--color-primary);
}

label,
.etiqueta {
  display: block;
  margin: 1rem 0 0.5rem;
  font-weight: 600;
}

select {
  width: 100%;
  padding: 0.7rem;

  border: 1px solid var(--color-border);
  border-radius: 6px;

  background: white;
}

.puntajes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.puntajes button {
  width: 44px;
  height: 44px;

  cursor: pointer;

  border: 1px solid var(--color-border);
  border-radius: 6px;

  background: white;
  color: var(--color-text);
}

.puntajes button:hover {
  background: var(--color-accent-light);
}

.puntajes button.activo {
  background: var(--color-accent);
  border-color: var(--color-accent);
  color: var(--color-text);
  font-weight: 600;
}

.enviar {
  width: 100%;
  margin-top: 24px;
  padding: 12px;

  cursor: pointer;

  background: var(--color-primary);
  color: white;

  border-radius: 6px;
}

.enviar:hover {
  background: var(--color-primary-hover);
}

.enviar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.exito {
  margin-top: 16px;
  color: var(--color-success);
  font-weight: 600;
}

.error {
  margin-top: 16px;
  color: var(--color-error);
  font-weight: 600;
}

.sin-rutinas {
  color: var(--color-text-secondary);
}
</style>