<script setup>
import { onMounted, ref } from 'vue'
import api from '@/services/api'

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const dietas = ref([])
const cargando = ref(false)
const error = ref('')

const cargarDietas = async () => {
  error.value = ''

  if (!usuario) {
    error.value = 'No hay un usuario autenticado'
    return
  }

  cargando.value = true

  try {
    const respuesta = await api.get(
      `/dietas/deportista/${usuario.id}`,
    )

    dietas.value = respuesta.data
  } catch (err) {
    error.value =
      'No se pudieron cargar tus planes alimenticios'
  } finally {
    cargando.value = false
  }
}

onMounted(cargarDietas)
</script>

<template>
  <main class="dietas-container">
    <section class="encabezado">
      <span class="etiqueta">
        Deportista
      </span>

      <h1>Mis dietas</h1>

      <p>
        Revisa los planes alimenticios que tu nutricionista
        ha creado para ti.
      </p>
    </section>

    <p v-if="cargando">
      Cargando planes alimenticios...
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
        dietas.length === 0
      "
    >
      No tienes planes alimenticios asignados todavía.
    </p>

    <div
      v-for="dieta in dietas"
      :key="dieta.id"
      class="tarjeta"
    >
      <h2>
        Plan #{{ dieta.id }}
      </h2>

      <p>
        <strong>Nutricionista:</strong>
        {{ dieta.creadorNombre || 'No disponible' }}
      </p>

      <p>
        <strong>Comidas:</strong>
        {{ dieta.comidas }}
      </p>

      <p>
        <strong>Porciones:</strong>
        {{ dieta.porciones }}
      </p>

      <p>
        <strong>Horarios:</strong>
        {{ dieta.horarios }}
      </p>

      <p>
        <strong>Sugerencia alimenticia:</strong>
        {{
          dieta.sugerenciaAlimenticia ||
          'Sin sugerencias adicionales'
        }}
      </p>

      <p>
        <strong>Fecha de creación:</strong>
        {{ dieta.fechaCreacion }}
      </p>
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
  max-width: 650px;
  padding: 24px;
  margin-bottom: 16px;

  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.tarjeta h2 {
  margin-top: 0;
  color: var(--color-primary);
}

.error {
  color: red;
}
</style>