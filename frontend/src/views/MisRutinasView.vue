<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const rutinas = ref([])
const mensaje = ref('')

const etiquetasEstado = {
  PENDIENTE_VALIDACION: 'Pendiente de validación',
  ACEPTADA: 'Aceptada',
  RECHAZADA: 'Rechazada',
  ASIGNADA: 'Asignada',
  REALIZADA: 'Realizada',
}

async function cargarRutinas() {
  if (!usuario) return

  try {
    const respuesta = await api.get(`/rutinas/deportista/${usuario.id}`)
    rutinas.value = respuesta.data
  } catch {
    mensaje.value = 'No se pudieron cargar tus rutinas'
  }
}

onMounted(cargarRutinas)
</script>

<template>
  <div class="mis-rutinas">
    <div class="encabezado">
      <h2>Mis rutinas</h2>
      <RouterLink class="proponer" to="/proponer-rutina">
        + Proponer rutina
      </RouterLink>
    </div>

    <p v-if="mensaje" class="error">{{ mensaje }}</p>

    <div v-if="rutinas.length" class="lista">
      <article v-for="rutina in rutinas" :key="rutina.id" class="rutina">
        <header>
          <h3>{{ rutina.nombre }}</h3>
          <span class="estado">
            {{ etiquetasEstado[rutina.estado] || rutina.estado }}
          </span>
        </header>

        <p v-if="rutina.descripcion">{{ rutina.descripcion }}</p>

        <p class="detalle">
          {{ rutina.duracionMinutos }} min · Intensidad {{ rutina.intensidad }}
          <span v-if="rutina.creadorNombre"> · Entrenador: {{ rutina.creadorNombre }}</span>
        </p>

        <div v-if="rutina.ejercicios && rutina.ejercicios.length" class="ejercicios">
          <h4>Ejercicios</h4>
          <ul>
            <li v-for="ejercicio in rutina.ejercicios" :key="ejercicio.id">
              <span class="nombre-ejercicio">{{ ejercicio.nombre }}</span>
              <span v-if="ejercicio.grupoMuscular" class="grupo-muscular">
                ({{ ejercicio.grupoMuscular }})
              </span>
              <span class="series-reps">
                {{ ejercicio.series }} series x {{ ejercicio.repeticiones }} repeticiones
              </span>
              <span v-if="ejercicio.notaTecnica" class="nota-tecnica">
                {{ ejercicio.notaTecnica }}
              </span>
            </li>
          </ul>
        </div>
      </article>
    </div>

    <p v-else-if="!mensaje">Aún no tienes rutinas asignadas</p>
  </div>
</template>

<style scoped>
.mis-rutinas {
  max-width: 720px;
  margin: 0 auto;
  padding: 1.5rem;
}

.encabezado {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  gap: 1rem;
}

.proponer {
  padding: 0.6rem 1rem;
  background: #2c7be5;
  color: #fff;
  border-radius: 6px;
  white-space: nowrap;
}

.lista {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.rutina {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.rutina header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.estado {
  font-size: 0.8rem;
  font-weight: 600;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background: #eef4ff;
  color: #2c7be5;
  white-space: nowrap;
}

.detalle {
  color: #666;
  font-size: 0.9rem;
}

.ejercicios {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.ejercicios h4 {
  margin: 0 0 0.5rem;
  font-size: 0.9rem;
  color: #444;
}

.ejercicios ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.ejercicios li {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.4rem;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #fafafa;
  border-radius: 6px;
}

.nombre-ejercicio {
  font-weight: 600;
}

.grupo-muscular {
  color: #888;
  font-size: 0.8rem;
}

.series-reps {
  color: #2c7be5;
  font-weight: 500;
}

.nota-tecnica {
  width: 100%;
  color: #777;
  font-size: 0.8rem;
  font-style: italic;
}

.error {
  color: #c62828;
  font-weight: 600;
}
</style>