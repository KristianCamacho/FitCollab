<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const solicitudes = ref([])
const mensaje = ref('')
const error = ref('')
const cargando = ref(false)

const cargarSolicitudes = async () => {
  cargando.value = true
  try {
    const respuesta = await api.get('/solicitudes-modificacion/dieta/pendientes')
    solicitudes.value = respuesta.data
  } catch (err) {
    error.value = 'No se pudieron cargar las solicitudes'
  } finally {
    cargando.value = false
  }
}

const responderSolicitud = async (id, estado) => {
  mensaje.value = ''
  error.value = ''
  try {
    await api.put(`/solicitudes-modificacion/${id}/responder`, { estado })
    mensaje.value = `Solicitud ${estado === 'ACEPTADA' ? 'aprobada' : 'rechazada'} correctamente`
    await cargarSolicitudes()
  } catch (err) {
    if (err.response?.status === 400) {
      error.value = err.response.data
    } else {
      error.value = 'No se pudo responder la solicitud'
    }
  }
}

onMounted(cargarSolicitudes)
</script>

<template>
  <main class="solicitudes-container">
    <section class="encabezado">
      <span class="etiqueta">Nutricionista</span>
      <h1>Solicitudes de modificación</h1>
      <p>Revisa y responde las solicitudes de cambio de dieta de tus deportistas.</p>
    </section>

    <p v-if="cargando">Cargando solicitudes...</p>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="mensaje" class="exito">{{ mensaje }}</p>

    <p v-if="!cargando && solicitudes.length === 0">
      No hay solicitudes pendientes.
    </p>

    <div v-for="s in solicitudes" :key="s.id" class="tarjeta">
      <p>
        <strong>Deportista:</strong>
        {{ s.deportista?.nombre }} {{ s.deportista?.apellido }}
      </p>
      <p><strong>Motivo:</strong> {{ s.motivo }}</p>
      <p><strong>Fecha:</strong> {{ s.fechaHora }}</p>

      <div class="acciones">
        <button class="btn-aprobar" @click="responderSolicitud(s.id, 'ACEPTADA')">
          Aprobar
        </button>
        <button class="btn-rechazar" @click="responderSolicitud(s.id, 'RECHAZADA')">
          Rechazar
        </button>
      </div>
    </div>
  </main>
</template>

<style scoped>
.solicitudes-container {
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
  margin-bottom: 16px;
}

.acciones {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.btn-aprobar {
  padding: 8px 16px;
  background: green;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-rechazar {
  padding: 8px 16px;
  background: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.exito { color: green; }
.error { color: red; }
</style>