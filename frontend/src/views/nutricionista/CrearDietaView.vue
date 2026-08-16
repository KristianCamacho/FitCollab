<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const usuarioGuardado = localStorage.getItem('usuario')
const usuario = usuarioGuardado ? JSON.parse(usuarioGuardado) : null

const comidas = ref('')
const porciones = ref('')
const horarios = ref('')
const sugerencia = ref('')

const mensaje = ref('')
const error = ref('')
const guardando = ref(false)

const crearDieta = async () => {
    mensaje.value = ''
    error.value = ''

    if (!usuario){
        error.value = 'No hay un usuario autenticado'
        return
    }

    guardando.value = true

    try {
        await api.post('/dietas',{
            comidas: comidas.value,
            porciones: porciones.value,
            horarios: horarios.value,
            sugerenciaAlimenticia: sugerencia.value,
            creadorId: usuario.id
        })

        mensaje.value = 'Plan alimenticio creado correctamente'
        setTimeout(() => router.push('/nutricionista/dietas'), 1500)
    } catch (err) {
        if (err.response?.status === 400) {
            error.value = err.response.data
        } else {
            error.value = 'No se pudo crear el plan alimenticio'
        }
    } finally {
        guardando.value = false
    }
}
</script>

<template>
  <main class="crear-dieta-container">
    <section class="tarjeta">
      <h1>Crear plan alimenticio</h1>

      <form @submit.prevent="crearDieta">
        <label for="comidas">Comidas</label>
        <textarea
          id="comidas"
          v-model="comidas"
          placeholder="Ej: Desayuno, almuerzo, once, cena"
          required
        ></textarea>

        <label for="porciones">Porciones</label>
        <textarea
          id="porciones"
          v-model="porciones"
          placeholder="Ej: 200g, 350g, 150g, 250g"
          required
        ></textarea>

        <label for="horarios">Horarios</label>
        <textarea
          id="horarios"
          v-model="horarios"
          placeholder="Ej: 8:00, 13:00, 17:00, 20:00"
          required
        ></textarea>

        <label for="sugerencia">Sugerencia alimenticia</label>
        <textarea
          id="sugerencia"
          v-model="sugerencia"
          placeholder="Ej: Evitar azúcar procesada, aumentar consumo de agua"
        ></textarea>

        <p v-if="mensaje" class="exito">{{ mensaje }}</p>
        <p v-if="error" class="error">{{ error }}</p>

        <button type="submit" :disabled="guardando">
          {{ guardando ? 'Guardando...' : 'Crear plan' }}
        </button>
      </form>
    </section>
  </main>
</template>

<style scoped>
.crear-dieta-container {
  max-width: 1100px;
  margin: 0 auto;
}
.tarjeta {
  max-width: 600px;
  padding: 24px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.tarjeta h1 {
  margin-top: 0;
  color: var(--color-primary);
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

textarea {
  padding: 10px;
  min-height: 80px;
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