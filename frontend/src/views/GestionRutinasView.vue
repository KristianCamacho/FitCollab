<script setup>
import { ref, onMounted, computed } from 'vue'
import { getRutinas, crearRutina, actualizarRutina, eliminarRutina, crearSolicitud } from '@/services/api'
import RutinaFormModal from '@/components/RutinaFormModal.vue'

const usuario = computed(() => {
  const datos = localStorage.getItem('usuario')
  return datos ? JSON.parse(datos) : null
})

const rutinas = ref([])
const cargando = ref(true)
const modalVisible = ref(false)
const rutinaSeleccionada = ref(null)

async function cargarRutinas() {
  cargando.value = true
  try {
    const res = await getRutinas()
    rutinas.value = res.data
  } catch (err) {
    alert('Error al cargar las rutinas')
  } finally {
    cargando.value = false
  }
}

function abrirModalCrear() {
  rutinaSeleccionada.value = null
  modalVisible.value = true
}

function abrirModalEditar(rutina) {
  rutinaSeleccionada.value = rutina
  modalVisible.value = true
}

async function guardarRutina(data) {
  try {
    if (data.esEdicion) {
      await actualizarRutina(data.id, data)
      alert('Rutina actualizada exitosamente')
    } else {
      data.creadorId = usuario.value?.id || 1
      await crearRutina(data)
      alert('Rutina creada exitosamente')
    }
    modalVisible.value = false
    cargarRutinas()
  } catch (err) {
    alert('Error al procesar la rutina')
  }
}

async function eliminar(id) {
  if (confirm('¿Deseas eliminar esta rutina?')) {
    try {
      await eliminarRutina(id)
      alert('Rutina eliminada')
      cargarRutinas()
    } catch (err) {
      alert('Error al eliminar la rutina')
    }
  }
}

async function proponerAdaptacion(rutina) {
  const motivo = prompt(`Ingrese el motivo para proponer la adaptación a "${rutina.nombre}":`)
  if (!motivo) return

  try {
    await crearSolicitud({
      tipo: 'ADAPTACION_RUTINA_TIEMPO',
      motivo: motivo,
      deportistaId: rutina.deportistaId || 1,
      especialistaId: usuario.value?.id || 1,
      rutinaId: rutina.id,
    })
    alert('Propuesta enviada con éxito')
  } catch (err) {
    alert('Error al proponer la adaptación')
  }
}

onMounted(() => { cargarRutinas() })
</script>

<template>
  <section class="rutinas-container">
    <div class="encabezado">
      <div>
        <span class="etiqueta">Entrenamiento</span>
        <h1>Gestión de Rutinas</h1>
      </div>
      <button class="btn-crear" @click="abrirModalCrear">+ Crear Rutina</button>
    </div>

    <div v-if="cargando" class="estado">Cargando rutinas...</div>

    <div v-else-if="rutinas.length === 0" class="tarjeta-vacia">
      No hay rutinas registradas. Haz clic en el botón superior para crear una.
    </div>

    <div v-else class="grid-rutinas">
      <div v-for="rutina in rutinas" :key="rutina.id" class="tarjeta-rutina">
        <div class="card-top">
          <h2>{{ rutina.nombre }}</h2>
          <span class="badge-intensidad">Intensidad: {{ rutina.intensidad }}/10</span>
        </div>

        <p class="descripcion">{{ rutina.descripcion || 'Sin descripción' }}</p>

        <div class="info-meta">
          <span><strong>⏱ Duración:</strong> {{ rutina.duracionMinutos }} min</span>
          <span><strong>👤 Deportista:</strong> {{ rutina.deportistaNombre || 'Sin asignar' }}</span>
        </div>

        <div class="seccion-ejercicios">
          <h4>Ejercicios ({{ rutina.ejercicios?.length || 0 }})</h4>
          <ul>
            <li v-for="ej in rutina.ejercicios" :key="ej.id">
              <strong>{{ ej.nombre }}</strong> — {{ ej.series }} series x {{ ej.repeticiones }} reps
              <span v-if="ej.notaTecnica" class="nota">({{ ej.notaTecnica }})</span>
            </li>
          </ul>
        </div>

        <div class="acciones-card">
          <button class="btn-editar" @click="abrirModalEditar(rutina)">Editar</button>
          <button class="btn-eliminar" @click="eliminar(rutina.id)">Eliminar</button>
          <button class="btn-propuesta" @click="proponerAdaptacion(rutina)">Proponer Adaptación</button>
        </div>
      </div>
    </div>

    <RutinaFormModal
      :mostrar="modalVisible"
      :rutina="rutinaSeleccionada"
      :creadorId="usuario?.id"
      @cerrar="modalVisible = false"
      @guardar="guardarRutina"
    />
  </section>
</template>

<style scoped>
.rutinas-container { max-width: 1100px; margin: 0 auto; }
.encabezado { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.etiqueta { color: var(--color-secondary); font-weight: 600; }
h1 { margin: 4px 0 0; font-size: 30px; color: var(--color-primary); }
.btn-crear { background: var(--color-primary); color: white; padding: 10px 18px; border-radius: var(--border-radius); cursor: pointer; font-weight: 600; }
.grid-rutinas { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; }
.tarjeta-rutina {
  background: var(--color-surface); border: 1px solid var(--color-border);
  border-radius: var(--border-radius); padding: 20px; box-shadow: var(--shadow-card);
  display: flex; flex-direction: column; justify-content: space-between;
}
.card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 10px; }
.card-top h2 { margin: 0; font-size: 18px; color: var(--color-primary); }
.badge-intensidad { background: var(--color-accent-light); color: var(--color-text); padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: 600; }
.descripcion { color: var(--color-text-secondary); margin: 10px 0; font-size: 14px; }
.info-meta { display: flex; flex-direction: column; gap: 4px; font-size: 13px; margin-bottom: 12px; }
.seccion-ejercicios { border-top: 1px solid var(--color-border); padding-top: 10px; margin-bottom: 14px; }
.seccion-ejercicios h4 { margin: 0 0 6px 0; color: var(--color-primary); font-size: 14px; }
.seccion-ejercicios ul { padding-left: 18px; margin: 0; font-size: 13px; }
.nota { color: var(--color-text-secondary); font-style: italic; }
.acciones-card { display: flex; gap: 8px; flex-wrap: wrap; }
.btn-editar { background: var(--color-secondary); color: white; padding: 6px 12px; border-radius: 6px; cursor: pointer; }
.btn-eliminar { background: var(--color-error); color: white; padding: 6px 12px; border-radius: 6px; cursor: pointer; }
.btn-propuesta { background: var(--color-accent); color: var(--color-text); padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: 600; }
.tarjeta-vacia { background: var(--color-surface); padding: 24px; border: 1px solid var(--color-border); border-radius: var(--border-radius); text-align: center; color: var(--color-text-secondary); }
</style>