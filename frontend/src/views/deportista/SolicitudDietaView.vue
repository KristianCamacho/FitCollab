<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const dietas = ref([])
const dietaSeleccionada = ref(null)
const motivo = ref('')
const mensaje = ref('')
const error = ref('')
const cargando = ref(false)
const enviando = ref(false)

const cargarDietas = async () => {
  cargando.value = true
  try {
    const respuesta = await api.get(`/dietas/deportista/${usuario.id}`)
    dietas.value = respuesta.data
  } catch (err) {
    error.value = 'No se pudieron cargar tus dietas'
  } finally {
    cargando.value = false
  }
}

const enviarSolicitud = async () => {
  mensaje.value = ''
  error.value = ''

  if (!dietaSeleccionada.value) {
    error.value = 'Debes seleccionar una dieta'
    return
  }

  if (!motivo.value.trim()) {
    error.value = 'Debes ingresar el motivo de la solicitud'
    return
  }

  enviando.value = true
  try {
    await api.post('/solicitudes-modificacion/dieta', {
      deportista: { id: usuario.id },
      dieta: { id: dietaSeleccionada.value },
      motivo: motivo.value,
      tipo: 'MODIFICACION_DIETA'
    })
    mensaje.value = 'Solicitud enviada correctamente, tu nutricionista la revisará pronto'
    motivo.value = ''
    dietaSeleccionada.value = null
  } catch (err) {
    if (err.response?.status === 400) {
      error.value = err.response.data
    } else {
      error.value = 'No se pudo enviar la solicitud'
    }
  } finally {
    enviando.value = false
  }
}

onMounted(cargarDietas)
</script>

<template>
  <main class="solicitud-container">
    <section class="encabezado">
      <span class="etiqueta">Mi dieta</span>
      <h1>Solicitar modificación de dieta</h1>
      <p>Indica qué cambios necesitas en tu plan alimenticio y tu nutricionista lo revisará.</p>
    </section>

    <p v-if="cargando">Cargando tus dietas...</p>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="mensaje" class="exito">{{ mensaje }}</p>

    <p v-if="!cargando && dietas.length === 0">
      No tienes planes alimenticios asignados todavía.
    </p>

    <div v-if="dietas.length > 0" class="tarjeta">
      <form @submit.prevent="enviarSolicitud">
        <label for="dieta">Selecciona la dieta que quieres modificar</label>
        <select id="dieta" v-model="dietaSeleccionada" required>
          <option disabled value="">Selecciona una opción</option>
          <option v-for="d in dietas" :key="d.id" :value="d.id">
            Dieta #{{ d.id }} - {{ d.comidas }}
          </option>
        </select>

        <label for="motivo">Motivo de la solicitud</label>
        <textarea
          id="motivo"
          v-model="motivo"
          placeholder="Ej: Quiero excluir el gluten de mi dieta porque me causa molestias"
          required
        ></textarea>

        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="mensaje" class="exito">{{ mensaje }}</p>

        <button type="submit" :disabled="enviando">
          {{ enviando ? 'Enviando...' : 'Enviar solicitud' }}
        </button>
      </form>
    </div>
  </main>
</template>

<style scoped>
.solicitud-container {
  max-width: 1100px;
  margin: 0 auto;
}

.encabezado {
  margin-bottom: 30px;
}

.etiqueta {
  color: var(--color-secondary);
  font-weight: 600;
}

h1 {
  margin: 6px 0 8px;
  font-size: 32px;
  color: var(--color-primary);
}

.encabezado p {
  color: var(--color-text-secondary);
}

.tarjeta {
  max-width: 600px;
  padding: 24px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

select {
  padding: 10px;
}

textarea {
  padding: 10px;
  min-height: 100px;
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

.exito { color: green; }
.error { color: red; }
</style>