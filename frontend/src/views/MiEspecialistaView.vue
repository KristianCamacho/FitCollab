<script setup>
import { ref } from 'vue'
import api from '../services/api'

const mostrarModal = ref(false)
const tipoEspecialista = ref('')
const motivo = ref('')

const mensajeSistema = ref('')
const hayError = ref(false)
const enviando = ref(false)

const usuarioLogueado =
  JSON.parse(
    localStorage.getItem('usuario')
      || 'null',
  )

const deportistaId =
  usuarioLogueado?.id

const abrirModal = (tipo) => {
  tipoEspecialista.value = tipo
  motivo.value = ''

  mensajeSistema.value = ''
  hayError.value = false

  mostrarModal.value = true
}

const cerrarModal = () => {
  mostrarModal.value = false
  motivo.value = ''
}

const extraerMensajeError = (error) => {
  const data = error.response?.data

  if (typeof data === 'string') {
    return data
  }

  if (data?.message && data.message !== 'Conflict') {
    return data.message
  }

  if (data?.detail && data.detail !== 'Conflict') {
    return data.detail
  }

  if (error.response?.status === 409) {
    return 'Ya existe una solicitud de cambio pendiente para este especialista.'
  }

  if (error.response?.status === 404) {
    return 'No se encontró la información necesaria para realizar la solicitud.'
  }

  return 'Error al enviar la solicitud.'
}

const enviarSolicitud = async () => {
  if (!deportistaId) {
    hayError.value = true

    mensajeSistema.value =
      'Sesión no encontrada. Inicie sesión nuevamente.'

    return
  }

  if (!motivo.value.trim()) {
    hayError.value = true

    mensajeSistema.value =
      'Debe ingresar el motivo del cambio.'

    return
  }

  enviando.value = true

  try {
    await api.post(
      '/solicitudes-cambio',
      {
        deportistaId:
          deportistaId.toString(),

        tipoEspecialista:
          tipoEspecialista.value,

        motivo:
          motivo.value.trim(),
      },
    )

    hayError.value = false

    mensajeSistema.value =
      'Solicitud enviada correctamente.'

    cerrarModal()

  } catch (error) {
    hayError.value = true

    mensajeSistema.value =
      extraerMensajeError(error)

    cerrarModal()

  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <section class="pagina">

    <div class="encabezado">

      <p class="seccion">
        Asignaciones
      </p>

      <h2>
        Mi equipo profesional
      </h2>

      <p>
        Desde aquí puedes solicitar el cambio
        del profesional que tienes asignado.
      </p>

    </div>

    <div class="tarjetas">

      <article class="tarjeta">

        <h3>
          Entrenador
        </h3>

        <p>
          Solicita un cambio si necesitas
          ser reasignado a otro entrenador.
        </p>

        <button
          type="button"
          @click="abrirModal('ENTRENADOR')"
        >
          Pedir cambio de entrenador
        </button>

      </article>

      <article class="tarjeta">

        <h3>
          Nutricionista
        </h3>

        <p>
          Solicita un cambio si necesitas
          ser reasignado a otro nutricionista.
        </p>

        <button
          type="button"
          @click="abrirModal('NUTRICIONISTA')"
        >
          Pedir cambio de nutricionista
        </button>

      </article>

    </div>

    <p
      v-if="mensajeSistema"
      class="mensaje"
      :class="
        hayError
          ? 'mensaje-error'
          : 'mensaje-exito'
      "
    >
      {{ mensajeSistema }}
    </p>

    <div
      v-if="mostrarModal"
      class="modal-fondo"
      @click.self="cerrarModal"
    >

      <div class="modal-contenido">

        <h3>
          Solicitar cambio de
          {{ tipoEspecialista.toLowerCase() }}
        </h3>

        <label for="motivo-cambio">
          Motivo del cambio
        </label>

        <textarea
          id="motivo-cambio"
          v-model="motivo"
          rows="5"
          maxlength="500"
          placeholder="Explica brevemente por qué solicitas el cambio..."
        />

        <div class="acciones">

          <button
            type="button"
            class="boton-secundario"
            :disabled="enviando"
            @click="cerrarModal"
          >
            Cancelar
          </button>

          <button
            type="button"
            class="boton-principal"
            :disabled="enviando"
            @click="enviarSolicitud"
          >
            {{
              enviando
                ? 'Enviando...'
                : 'Enviar solicitud'
            }}
          </button>

        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>
.pagina {
  max-width: 900px;
  margin: 0 auto;
}

.encabezado {
  margin-bottom: 28px;
}

.seccion {
  margin: 0 0 6px;
  color: var(--color-secondary);
  font-weight: 700;
}

.encabezado h2 {
  margin: 0;
  color: var(--color-primary);
  font-size: 32px;
}

.encabezado p:last-child {
  color: var(--color-text-secondary);
}

.tarjetas {
  display: grid;
  grid-template-columns:
    repeat(2, minmax(0, 1fr));

  gap: 20px;
}

.tarjeta {
  padding: 24px;

  border:
    1px solid var(--color-border);

  border-radius:
    var(--border-radius);

  background:
    var(--color-surface);

  box-shadow:
    var(--shadow-card);
}

.tarjeta h3 {
  margin-top: 0;
  color: var(--color-primary);
}

.tarjeta p {
  min-height: 48px;
  color: var(--color-text-secondary);
}

.tarjeta button,
.boton-principal,
.boton-secundario {
  padding: 11px 16px;
  border-radius: 8px;
  cursor: pointer;
}

.tarjeta button,
.boton-principal {
  background:
    var(--color-accent);

  color:
    var(--color-text);

  font-weight: 600;
}

.tarjeta button:hover,
.boton-principal:hover {
  background:
    var(--color-accent-light);
}

.boton-secundario {
  background: #eee7de;
  color: var(--color-text);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.mensaje {
  margin-top: 20px;
  padding: 12px 14px;
  border-radius: 8px;
}

.mensaje-exito {
  background: #e7f6ed;
  color: var(--color-success);
}

.mensaje-error {
  background: #fdebec;
  color: var(--color-error);
}

.modal-fondo {
  position: fixed;
  inset: 0;
  z-index: 1000;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 20px;

  background:
    rgba(45, 36, 28, 0.55);
}

.modal-contenido {
  width: min(500px, 100%);
  padding: 24px;

  border-radius:
    var(--border-radius);

  background:
    var(--color-surface);

  box-shadow:
    var(--shadow-card);
}

.modal-contenido h3 {
  margin-top: 0;
  color: var(--color-primary);
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
}

textarea {
  width: 100%;
  resize: vertical;

  padding: 10px;

  border:
    1px solid var(--color-border);

  border-radius: 8px;
}

.acciones {
  display: flex;
  justify-content: flex-end;

  gap: 10px;
  margin-top: 18px;
}

@media (max-width: 700px) {
  .tarjetas {
    grid-template-columns: 1fr;
  }
}
</style>