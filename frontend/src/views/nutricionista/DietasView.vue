<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const dietas = ref([])
const dietaEditando = ref(null)
const mensaje = ref('')
const error = ref('')
const cargando = ref(false)
const guardando = ref(false)

const cargarDietas = async () => {
  cargando.value = true
  try {
    const respuesta = await api.get(`/dietas/nutricionista/${usuario.id}`)
    dietas.value = respuesta.data
  } catch (err) {
    error.value = 'No se pudieron cargar los planes alimenticios'
  } finally {
    cargando.value = false
  }
}

const editarDieta = (dieta) => {
  dietaEditando.value = { ...dieta }
}

const cancelarEdicion = () => {
  dietaEditando.value = null
  mensaje.value = ''
  error.value = ''
}

const guardarEdicion = async () => {
  mensaje.value = ''
  error.value = ''
  guardando.value = true
  try {
    await api.put(`/dietas/${dietaEditando.value.id}`, dietaEditando.value)
    mensaje.value = 'Plan alimenticio actualizado correctamente'
    dietaEditando.value = null
    await cargarDietas()
  } catch (err) {
    if (err.response?.status === 400) {
      error.value = err.response.data
    } else {
      error.value = 'No se pudo actualizar el plan alimenticio'
    }
  } finally {
    guardando.value = false
  }
}

const eliminarDieta = async (id) => {
  if (!confirm('¿Estás segura de que deseas eliminar este plan?')) return
  try {
    await api.delete(`/dietas/${id}`)
    mensaje.value = 'Plan alimenticio eliminado correctamente'
    await cargarDietas()
  } catch (err) {
    error.value = 'No se pudo eliminar el plan alimenticio'
  }
}

onMounted(cargarDietas)
</script>

<template>
  <main class="dietas-container">
    <section class="encabezado">
      <span class="etiqueta">Nutricionista</span>
      <h1>Mis planes alimenticios</h1>
      <p>Administra los planes alimenticios de tus deportistas.</p>
    </section>

    <button class="btn-crear" @click="router.push('/nutricionista/dietas/crear')">
      + Crear nuevo plan
    </button>

    <p v-if="cargando">Cargando planes...</p>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="mensaje" class="exito">{{ mensaje }}</p>

    <p v-if="!cargando && dietas.length === 0">
      No tienes planes alimenticios creados todavía.
    </p>

    <!-- LISTA DE DIETAS -->
    <div v-if="!dietaEditando">
      <div v-for="d in dietas" :key="d.id" class="tarjeta">
        <p><strong>Comidas:</strong> {{ d.comidas }}</p>
        <p><strong>Porciones:</strong> {{ d.porciones }}</p>
        <p><strong>Horarios:</strong> {{ d.horarios }}</p>
        <p><strong>Sugerencia:</strong> {{ d.sugerenciaAlimenticia }}</p>
        <p><strong>Creado:</strong> {{ d.fechaCreacion }}</p>

        <div class="acciones">
          <button class="btn-editar" @click="editarDieta(d)">
            Editar
          </button>
          <button class="btn-eliminar" @click="eliminarDieta(d.id)">
            Eliminar
          </button>
        </div>
      </div>
    </div>

    <!-- FORMULARIO DE EDICION -->
    <div v-if="dietaEditando" class="tarjeta">
      <h2>Editar plan alimenticio</h2>

      <form @submit.prevent="guardarEdicion">
        <label for="comidas">Comidas</label>
        <textarea
          id="comidas"
          v-model="dietaEditando.comidas"
          required
        ></textarea>

        <label for="porciones">Porciones</label>
        <textarea
          id="porciones"
          v-model="dietaEditando.porciones"
          required
        ></textarea>

        <label for="horarios">Horarios</label>
        <textarea
          id="horarios"
          v-model="dietaEditando.horarios"
          required
        ></textarea>

        <label for="sugerencia">Sugerencia alimenticia</label>
        <textarea
          id="sugerencia"
          v-model="dietaEditando.sugerenciaAlimenticia"
        ></textarea>

        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="mensaje" class="exito">{{ mensaje }}</p>

        <div class="acciones">
          <button type="submit" :disabled="guardando">
            {{ guardando ? 'Guardando...' : 'Guardar cambios' }}
          </button>
          <button type="button" class="btn-cancelar" @click="cancelarEdicion">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </main>
</template>

<style scoped>
.dietas-container {
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

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

textarea {
  padding: 10px;
  min-height: 60px;
  resize: vertical;
}

.acciones {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.btn-crear {
  margin-bottom: 24px;
  padding: 10px 20px;
  cursor: pointer;
}

.btn-editar {
  padding: 8px 16px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-eliminar {
  padding: 8px 16px;
  background: red;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancelar {
  padding: 8px 16px;
  background: gray;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.exito { color: green; }
.error { color: red; }
</style>