<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  mostrar: Boolean,
  rutina: Object,
  creadorId: Number,
})

const emit = defineEmits(['cerrar', 'guardar'])

const esEdicion = reactive({ value: false })

const form = reactive({
  id: null,
  nombre: '',
  descripcion: '',
  duracionMinutos: 45,
  intensidad: 5,
  deportistaId: null,
  creadorId: null,
  ejercicios: [],
})

watch(
  () => props.rutina,
  (nuevaRutina) => {
    if (nuevaRutina) {
      esEdicion.value = true
      form.id = nuevaRutina.id
      form.nombre = nuevaRutina.nombre
      form.descripcion = nuevaRutina.descripcion
      form.duracionMinutos = nuevaRutina.duracionMinutos
      form.intensidad = nuevaRutina.intensidad
      form.deportistaId = nuevaRutina.deportistaId || null
      form.creadorId = nuevaRutina.creadorId || props.creadorId
      form.ejercicios = nuevaRutina.ejercicios ? nuevaRutina.ejercicios.map((e) => ({ ...e })) : []
    } else {
      esEdicion.value = false
      resetForm()
    }
  },
  { immediate: true },
)

function resetForm() {
  form.id = null
  form.nombre = ''
  form.descripcion = ''
  form.duracionMinutos = 45
  form.intensidad = 5
  form.deportistaId = null
  form.creadorId = props.creadorId || 1
  form.ejercicios = []
}

function agregarEjercicio() {
  form.ejercicios.push({ nombre: '', series: 3, repeticiones: 10, notaTecnica: '' })
}

function eliminarEjercicio(index) {
  form.ejercicios.splice(index, 1)
}

function cerrar() { emit('cerrar') }
function guardar() { emit('guardar', { ...form, esEdicion: esEdicion.value }) }
</script>

<template>
  <div v-if="mostrar" class="modal-overlay">
    <div class="modal-card">
      <h2>{{ esEdicion.value ? 'Editar Rutina' : 'Crear Nueva Rutina' }}</h2>

      <form @submit.prevent="guardar">
        <div class="campo">
          <label>Nombre de la Rutina</label>
          <input v-model="form.nombre" type="text" placeholder="Ej: Rutina Hipertrofia A" required />
        </div>

        <div class="campo">
          <label>Descripción</label>
          <textarea v-model="form.descripcion" rows="2" placeholder="Detalles de la rutina"></textarea>
        </div>

        <div class="fila">
          <div class="campo">
            <label>Duración (Minutos)</label>
            <input v-model.number="form.duracionMinutos" type="number" min="1" required />
          </div>

          <div class="campo">
            <label>Intensidad (1 a 10)</label>
            <input v-model.number="form.intensidad" type="number" min="1" max="10" required />
          </div>
        </div>

        <div class="campo">
          <label>ID Deportista Asignado (Opcional)</label>
          <input v-model.number="form.deportistaId" type="number" placeholder="Ej: 1" />
        </div>

        <hr class="divisor" />
        <h3>Ejercicios</h3>

        <div v-for="(ejercicio, idx) in form.ejercicios" :key="idx" class="ejercicio-box">
          <div class="ejercicio-header">
            <span>Ejercicio #{{ idx + 1 }}</span>
            <button type="button" class="btn-eliminar-sm" @click="eliminarEjercicio(idx)">✕</button>
          </div>

          <div class="campo">
            <input v-model="ejercicio.nombre" type="text" placeholder="Nombre Ej: Press Banco" required />
          </div>

          <div class="fila">
            <div class="campo">
              <input v-model.number="ejercicio.series" type="number" placeholder="Series" min="1" required />
            </div>
            <div class="campo">
              <input v-model.number="ejercicio.repeticiones" type="number" placeholder="Repeticiones" min="1" required />
            </div>
          </div>

          <div class="campo">
            <input v-model="ejercicio.notaTecnica" type="text" placeholder="Nota técnica (Ej: Descanso 90s)" />
          </div>
        </div>

        <button type="button" class="btn-secundario" @click="agregarEjercicio">+ Agregar Ejercicio</button>

        <div class="acciones">
          <button type="button" class="btn-cancelar" @click="cerrar">Cancelar</button>
          <button type="submit" class="btn-principal">Guardar Rutina</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(45, 36, 28, 0.5);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.modal-card {
  background: var(--color-surface); padding: 28px; border-radius: var(--border-radius);
  border: 1px solid var(--color-border); box-shadow: var(--shadow-card);
  width: 100%; max-width: 540px; max-height: 85vh; overflow-y: auto;
}
h2 { color: var(--color-primary); margin-top: 0; font-size: 22px; }
h3 { color: var(--color-primary); font-size: 16px; margin: 10px 0; }
.campo { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; }
.fila { display: flex; gap: 10px; }
.fila .campo { flex: 1; }
label { font-size: 13px; font-weight: 600; color: var(--color-text-secondary); }
input, textarea { padding: 10px; border: 1px solid var(--color-border); border-radius: 6px; background: var(--color-background); color: var(--color-text); }
.divisor { border: 0; border-top: 1px solid var(--color-border); margin: 16px 0; }
.ejercicio-box { background: var(--color-background); border: 1px solid var(--color-border); border-radius: 6px; padding: 12px; margin-bottom: 10px; }
.ejercicio-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 600; }
.btn-eliminar-sm { background: var(--color-error); color: white; border-radius: 50%; width: 22px; height: 22px; cursor: pointer; }
.btn-secundario { background: var(--color-secondary); color: white; border-radius: 6px; padding: 8px 12px; cursor: pointer; margin-bottom: 16px; }
.btn-principal { background: var(--color-primary); color: white; border-radius: 6px; padding: 10px 18px; cursor: pointer; font-weight: 600; }
.btn-cancelar { background: #888; color: white; border-radius: 6px; padding: 10px 18px; cursor: pointer; }
.acciones { display: flex; justify-content: flex-end; gap: 10px; margin-top: 16px; }
</style>