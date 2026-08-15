<script setup>
import { ref, onMounted } from 'vue'
import { getSolicitudes, responderSolicitud } from '@/services/api'

const solicitudes = ref([])
const cargando = ref(true)

async function cargarSolicitudes() {
  cargando.value = true
  try {
    const res = await getSolicitudes()
    solicitudes.value = res.data
  } catch (err) {
    alert('Error al cargar la lista de propuestas')
  } finally {
    cargando.value = false
  }
}

async function responder(id, estado) {
  try {
    await responderSolicitud(id, estado)
    alert(`Propuesta #${id} marcada como ${estado}`)
    cargarSolicitudes()
  } catch (err) {
    alert('Error al actualizar el estado de la propuesta')
  }
}

onMounted(() => { cargarSolicitudes() })
</script>

<template>
  <section class="solicitudes-container">
    <div class="encabezado">
      <span class="etiqueta">Colaboración</span>
      <h1>Propuestas y Adaptaciones</h1>
    </div>

    <div v-if="cargando" class="estado">Cargando propuestas...</div>

    <div v-else-if="solicitudes.length === 0" class="tarjeta-vacia">
      No hay solicitudes de propuesta o modificación pendientes.
    </div>

    <div v-else class="tabla-wrapper">
      <table class="tabla-solicitudes">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tipo</th>
            <th>Motivo</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sol in solicitudes" :key="sol.id">
            <td>#{{ sol.id }}</td>
            <td><strong>{{ sol.tipo }}</strong></td>
            <td>{{ sol.motivo }}</td>
            <td>{{ sol.fechaHora ? new Date(sol.fechaHora).toLocaleDateString() : '-' }}</td>
            <td>
              <span :class="['badge-estado', sol.estado?.toLowerCase()]">
                {{ sol.estado }}
              </span>
            </td>
            <td>
              <div v-if="sol.estado === 'PENDIENTE'" class="acciones">
                <button class="btn-aceptar" @click="responder(sol.id, 'ACEPTADA')">Aceptar</button>
                <button class="btn-rechazar" @click="responder(sol.id, 'RECHAZADA')">Rechazar</button>
              </div>
              <span v-else class="texto-completado">Completado</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.solicitudes-container { max-width: 1100px; margin: 0 auto; }
.encabezado { margin-bottom: 24px; }
.etiqueta { color: var(--color-secondary); font-weight: 600; }
h1 { margin: 4px 0 0; font-size: 30px; color: var(--color-primary); }
.tabla-wrapper {
  background: var(--color-surface); border: 1px solid var(--color-border);
  border-radius: var(--border-radius); box-shadow: var(--shadow-card); overflow-x: auto;
}
.tabla-solicitudes { width: 100%; border-collapse: collapse; text-align: left; }
.tabla-solicitudes th, .tabla-solicitudes td { padding: 14px 18px; border-bottom: 1px solid var(--color-border); font-size: 14px; }
.tabla-solicitudes th { background: var(--color-background); color: var(--color-primary); font-weight: 600; }
.badge-estado { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
.badge-estado.pendiente { background: #FFF3CD; color: #856404; }
.badge-estado.aceptada { background: #D4EDDA; color: #155724; }
.badge-estado.rechazada { background: #F8D7DA; color: #721C24; }
.acciones { display: flex; gap: 6px; }
.btn-aceptar { background: var(--color-success); color: white; padding: 6px 12px; border-radius: 6px; cursor: pointer; }
.btn-rechazar { background: var(--color-error); color: white; padding: 6px 12px; border-radius: 6px; cursor: pointer; }
.texto-completado { color: var(--color-text-secondary); font-size: 13px; }
.tarjeta-vacia { background: var(--color-surface); padding: 24px; border: 1px solid var(--color-border); border-radius: var(--border-radius); text-align: center; color: var(--color-text-secondary); }
</style>