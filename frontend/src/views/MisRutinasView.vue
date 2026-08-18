<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const rutinas = ref([])
const cargando = ref(false)
const mensaje = ref('')
const error = ref('')

const cargarRutinas = async () => {
  error.value = ''

  if (!usuario) {
    error.value = 'No hay un usuario autenticado'
    return
  }

  cargando.value = true

  try {
    const respuesta = await api.get(
      `/rutinas/deportista/${usuario.id}`,
    )

    rutinas.value = respuesta.data
  } catch (err) {
    error.value = 'No se pudieron cargar tus rutinas'
  } finally {
    cargando.value = false
  }
}

const marcarComoRealizada = async (rutina) => {
  mensaje.value = ''
  error.value = ''

  try {
    await api.put(`/rutinas/${rutina.id}/realizada`)

    await cargarRutinas()

    mensaje.value = 'Rutina marcada como realizada'
  } catch (err) {
    if (typeof err.response?.data === 'string') {
      error.value = err.response.data
    } else {
      error.value =
        err.response?.data?.detail ||
        err.response?.data?.message ||
        'No se pudo marcar la rutina como realizada'
    }
  }
}

const textoEstado = (estado) => {
  const textos = {
    ASIGNADA: 'Asignada',
    PENDIENTE_VALIDACION: 'Pendiente',
    ACEPTADA: 'Aceptada',
    RECHAZADA: 'Rechazada',
    REALIZADA: 'Realizada',
  }

  return textos[estado] || estado
}

onMounted(cargarRutinas)
</script>

<template>
  <main class="rutinas-container">
    <section class="encabezado">
      <h1>Mis rutinas</h1>

      <button
        class="btn-proponer"
        @click="router.push('/proponer-rutina')"
      >
        + Proponer rutina
      </button>
    </section>

    <p v-if="cargando">
      Cargando rutinas...
    </p>

    <p
      v-if="mensaje"
      class="exito"
    >
      {{ mensaje }}
    </p>

    <p
      v-if="error"
      class="error"
    >
      {{ error }}
    </p>

    <p
      v-if="
        !cargando &&
        !error &&
        rutinas.length === 0
      "
    >
      Aún no tienes rutinas asignadas.
    </p>

    <article
      v-for="rutina in rutinas"
      :key="rutina.id"
      class="tarjeta-rutina"
    >
      <div class="cabecera-rutina">
        <h2>
          {{ rutina.nombre }}
        </h2>

        <span
          class="estado"
          :class="`estado-${rutina.estado?.toLowerCase()}`"
        >
          {{ textoEstado(rutina.estado) }}
        </span>
      </div>

      <p class="descripcion">
        {{ rutina.descripcion }}
      </p>

      <p class="datos">
        {{ rutina.duracionMinutos }} min
        · Intensidad {{ rutina.intensidad }}
        <template v-if="rutina.creadorNombre">
          · Entrenador: {{ rutina.creadorNombre }}
        </template>
      </p>

      <div
        v-if="rutina.ejercicios?.length"
        class="ejercicios"
      >
        <h3>Ejercicios</h3>

        <div
          v-for="ejercicio in rutina.ejercicios"
          :key="ejercicio.id"
          class="ejercicio"
        >
          <div>
            <strong>
              {{
                ejercicio.nombre ||
                ejercicio.ejercicioNombre ||
                'Ejercicio'
              }}
            </strong>

            <span
              v-if="
                ejercicio.grupoMuscular ||
                ejercicio.ejercicioGrupoMuscular
              "
              class="grupo"
            >
              ({{
                ejercicio.grupoMuscular ||
                ejercicio.ejercicioGrupoMuscular
              }})
            </span>

            <span class="series">
              {{
                ejercicio.series
              }}
              series x
              {{
                ejercicio.repeticiones
              }}
              repeticiones
            </span>
          </div>

          <em
            v-if="
              ejercicio.notaTecnica ||
              ejercicio.notaTecnicaSugerida
            "
          >
            {{
              ejercicio.notaTecnica ||
              ejercicio.notaTecnicaSugerida
            }}
          </em>
        </div>
      </div>

      <div
        v-if="rutina.estado === 'ACEPTADA' ||rutina.estado === 'ASIGNADA'"
        class="acciones"
      >
        <button
          class="btn-realizada"
          @click="marcarComoRealizada(rutina)"
        >
          Marcar como realizada
        </button>
      </div>

      <p
        v-if="
          rutina.estado === 'REALIZADA' &&
          rutina.calificacion > 0
        "
        class="calificacion"
      >
        Calificación: {{ rutina.calificacion }}/10
      </p>
    </article>
  </main>
</template>

<style scoped>
.rutinas-container {
  max-width: 1100px;
  margin: 0 auto;
}

.encabezado {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 30px;
}

.encabezado h1 {
  margin: 0;
  color: var(--color-primary);
}

.btn-proponer {
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  background: #347ee8;
  color: white;
  cursor: pointer;
}

.tarjeta-rutina {
  padding: 24px;
  margin-bottom: 16px;

  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.cabecera-rutina {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.cabecera-rutina h2 {
  margin: 0;
  color: var(--color-primary);
}

.estado {
  padding: 5px 12px;
  border-radius: 18px;
  background: #e8f1ff;
  color: #1769d2;
  font-size: 13px;
}

.descripcion {
  margin-top: 24px;
}

.datos {
  color: var(--color-text-secondary);
}

.ejercicios {
  margin-top: 18px;
  padding-top: 18px;
  border-top: 1px solid var(--color-border);
}

.ejercicios h3 {
  margin: 0 0 14px;
  font-size: 14px;
}

.ejercicio {
  padding: 12px;
  margin-bottom: 8px;

  background: var(--color-background);
  border-radius: 6px;
}

.grupo {
  margin-left: 5px;
  color: var(--color-text-secondary);
}

.series {
  margin-left: 6px;
  color: #1769d2;
  font-weight: 600;
}

.ejercicio em {
  display: block;
  margin-top: 8px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.acciones {
  margin-top: 18px;
}

.btn-realizada {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;

  background: var(--color-primary);
  color: white;

  cursor: pointer;
}

.btn-realizada:hover {
  opacity: 0.9;
}

.calificacion {
  margin-top: 16px;
  font-weight: 600;
}

.exito {
  color: green;
}

.error {
  color: red;
}

@media (max-width: 700px) {
  .encabezado {
    align-items: stretch;
    flex-direction: column;
  }

  .cabecera-rutina {
    align-items: flex-start;
  }
}
</style>