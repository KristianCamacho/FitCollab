<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

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
const enviando = ref(false)

async function cargarCatalogo() {
  try {
    const respuesta = await api.get('/catalogo-ejercicios')
    catalogo.value = respuesta.data
  } catch {
    mensaje.value = 'No se pudo cargar el catálogo de ejercicios'
    tipoMensaje.value = 'error'
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

async function proponerRutina() {
  mensaje.value = ''

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

  enviando.value = true

  try {
    await api.post(`/rutinas/deportista/${usuario.id}/proponer`, {
      nombre: nombre.value,
      descripcion: descripcion.value,
      duracionMinutos: duracionMinutos.value,
      intensidad: intensidad.value,
      ejercicios: ejerciciosValidos,
    })

    mensaje.value = 'Rutina propuesta enviada a tu entrenador'
    tipoMensaje.value = 'exito'

    setTimeout(() => router.push('/mis-rutinas'), 1200)
  } catch (error) {
    tipoMensaje.value = 'error'
    mensaje.value =
      error.response?.data?.message ||
      error.response?.data ||
      'Ocurrió un error al proponer la rutina'
  } finally {
    enviando.value = false
  }
}

onMounted(cargarCatalogo)
</script>

<template>
  <div class="proponer-rutina">
    <h2>Proponer rutina</h2>

    <section>
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

    <button class="enviar" :disabled="enviando" @click="proponerRutina">
      {{ enviando ? 'Enviando...' : 'Proponer y enviar' }}
    </button>

    <p v-if="mensaje" :class="tipoMensaje">{{ mensaje }}</p>
  </div>
</template>

<style scoped>
.proponer-rutina {
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