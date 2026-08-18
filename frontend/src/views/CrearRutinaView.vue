<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const deportistas = ref([])
const deportistasSeleccionados = ref([])
const catalogo = ref([])

const nombre = ref('')
const descripcion = ref('')
const duracionMinutos = ref(null)
const intensidad = ref(null)

const ejercicios = ref([
  { catalogoEjercicioId: null, series: null, repeticiones: null, notaTecnica: '' },
])

const mensaje = ref('')
const tipoMensaje = ref('')
const cargando = ref(false)

async function cargarDeportistas() {
  if (!usuario) return

  try {
    const respuesta = await api.get(`/entrenadores/${usuario.id}/deportistas`)
    deportistas.value = respuesta.data

    if (deportistas.value.length === 0) {
      mensaje.value = 'No tienes deportistas asignados'
      tipoMensaje.value = 'error'
    }
  } catch {
    mensaje.value = 'No se pudieron cargar tus deportistas'
    tipoMensaje.value = 'error'
  }
}

async function cargarCatalogo() {
  try {
    const respuesta = await api.get('/catalogo-ejercicios')
    catalogo.value = respuesta.data
  } catch {
    mensaje.value = 'No se pudo cargar el catálogo de ejercicios'
    tipoMensaje.value = 'error'
  }
}

function alternarDeportista(id) {
  const indice = deportistasSeleccionados.value.indexOf(id)
  if (indice === -1) {
    deportistasSeleccionados.value.push(id)
  } else {
    deportistasSeleccionados.value.splice(indice, 1)
  }
}

function agregarEjercicio() {
  ejercicios.value.push({
    catalogoEjercicioId: null,
    series: null,
    repeticiones: null,
    notaTecnica: '',
  })
}

function quitarEjercicio(indice) {
  ejercicios.value.splice(indice, 1)
}

async function crearRutina() {
  mensaje.value = ''

  if (deportistasSeleccionados.value.length === 0) {
    mensaje.value = 'Debes seleccionar al menos un deportista'
    tipoMensaje.value = 'error'
    return
  }

  if (!nombre.value || !duracionMinutos.value || !intensidad.value) {
    mensaje.value = 'Debes completar todos los campos obligatorios de la rutina'
    tipoMensaje.value = 'error'
    return
  }

  const ejerciciosValidos = ejercicios.value.filter(
    (e) => e.catalogoEjercicioId && e.series && e.repeticiones,
  )

  if (ejerciciosValidos.length === 0) {
    mensaje.value = 'Debes seleccionar al menos un ejercicio del catálogo'
    tipoMensaje.value = 'error'
    return
  }

  cargando.value = true

  try {
    await api.post(`/rutinas/entrenador/${usuario.id}`, {
      nombre: nombre.value,
      descripcion: descripcion.value,
      duracionMinutos: duracionMinutos.value,
      intensidad: intensidad.value,
      deportistaIds: deportistasSeleccionados.value,
      ejercicios: ejerciciosValidos,
    })

    mensaje.value = 'Rutina creada y enviada a los deportistas seleccionados'
    tipoMensaje.value = 'exito'

    nombre.value = ''
    descripcion.value = ''
    duracionMinutos.value = null
    intensidad.value = null
    deportistasSeleccionados.value = []
    ejercicios.value = [
      { catalogoEjercicioId: null, series: null, repeticiones: null, notaTecnica: '' },
    ]
  } catch (error) {
    tipoMensaje.value = 'error'
    mensaje.value =
      error.response?.data?.message ||
      error.response?.data ||
      'Ocurrió un error al crear la rutina'
  } finally {
    cargando.value = false
  }
}

onMounted(() => {
  cargarDeportistas()
  cargarCatalogo()
})
</script>

<template>
  <div class="crear-rutina">
    <h2>Crear rutina</h2>

    <section>
      <h3>Mis deportistas</h3>

      <div v-if="deportistas.length" class="deportistas">
        <label v-for="deportista in deportistas" :key="deportista.id" class="deportista">
          <input
            type="checkbox"
            :value="deportista.id"
            :checked="deportistasSeleccionados.includes(deportista.id)"
            @change="alternarDeportista(deportista.id)"
          />
          {{ deportista.nombre }} {{ deportista.apellido }}
        </label>
      </div>

      <p v-else class="error">No tienes deportistas asignados</p>
    </section>

    <section>
      <h3>Datos de la rutina</h3>

      <label for="nombre">Nombre</label>
      <input id="nombre" v-model="nombre" type="text" required />

      <label for="descripcion">Descripción</label>
      <textarea id="descripcion" v-model="descripcion"></textarea>

      <label for="duracion">Duración (minutos)</label>
      <input id="duracion" v-model.number="duracionMinutos" type="number" min="1" required />

      <label for="intensidad">Intensidad (1 a 10)</label>
      <input
        id="intensidad"
        v-model.number="intensidad"
        type="number"
        min="1"
        max="10"
        required
      />
    </section>

    <section>
      <h3>Ejercicios (seleccionados del catálogo)</h3>

      <div v-for="(ejercicio, indice) in ejercicios" :key="indice" class="ejercicio">
        <select v-model.number="ejercicio.catalogoEjercicioId">
          <option :value="null" disabled>-- Elige un ejercicio --</option>
          <option v-for="item in catalogo" :key="item.id" :value="item.id">
            {{ item.nombre }} ({{ item.grupoMuscular }})
          </option>
        </select>

        <input v-model.number="ejercicio.series" type="number" min="1" placeholder="Series" />
        <input
          v-model.number="ejercicio.repeticiones"
          type="number"
          min="1"
          placeholder="Repeticiones"
        />
        <input
          v-model="ejercicio.notaTecnica"
          type="text"
          placeholder="Nota técnica (opcional)"
        />

        <button
          type="button"
          class="quitar"
          :disabled="ejercicios.length === 1"
          @click="quitarEjercicio(indice)"
        >
          Quitar
        </button>
      </div>

      <button type="button" class="agregar" @click="agregarEjercicio">
        + Agregar ejercicio
      </button>
    </section>

    <button class="enviar" :disabled="cargando" @click="crearRutina">
      {{ cargando ? 'Enviando...' : 'Enviar rutina' }}
    </button>

    <p v-if="mensaje" :class="tipoMensaje">{{ mensaje }}</p>
  </div>
</template>

<style scoped>
.crear-rutina {
  max-width: 640px;
  margin: 0 auto;
  padding: 1.5rem;
}

section {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin: 0.75rem 0 0.35rem;
  font-weight: 600;
}

input,
textarea,
select {
  width: 100%;
  padding: 0.6rem;
  font-size: 1rem;
}

.deportistas {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.deportista {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: normal;
  margin: 0;
  padding: 0.75rem 1rem;

  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;

  cursor: pointer;
}

.deportista input[type='checkbox'] {
  width: auto;
  margin: 0;
  flex-shrink: 0;
}

.ejercicio {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 2fr auto;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  align-items: center;
}

.quitar {
  padding: 0.5rem;
  cursor: pointer;
  background: #f1f1f1;
}

.agregar {
  margin-top: 0.5rem;
  padding: 0.6rem;
  cursor: pointer;
  background: #eef4ff;
}

.enviar {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
  cursor: pointer;
  background: #2c7be5;
  color: #fff;
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