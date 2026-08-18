<script setup>
import {
  onMounted,
  ref,
} from 'vue'

import api from '../services/api'

const solicitudes = ref([])

const solicitudActiva =
  ref(null)

const modoRevision =
  ref('')

const listaEspecialistas =
  ref([])

const nuevoEspecialistaId =
  ref('')

const motivoRechazo =
  ref('')

const errorForm =
  ref('')

const mensajeSistema =
  ref('')

const cargando =
  ref(false)

const procesando =
  ref(false)

const extraerMensajeError = (
  error,
  mensajePorDefecto,
) => {

  if (
    typeof error.response?.data
    === 'string'
  ) {
    return error.response.data
  }

  return (
    error.response?.data?.detail
    || error.response?.data?.message
    || error.response?.data?.error
    || mensajePorDefecto
  )
}

const cargarLista = async () => {

  cargando.value = true
  mensajeSistema.value = ''

  try {

    const respuesta =
      await api.get(
        '/solicitudes-cambio/pendientes',
      )

    solicitudes.value =
      respuesta.data

  } catch (error) {

    mensajeSistema.value =
      extraerMensajeError(
        error,
        'No se pudieron cargar las solicitudes pendientes.',
      )

  } finally {

    cargando.value = false
  }
}

const abrirModal = (
  solicitud,
) => {

  solicitudActiva.value =
    solicitud

  modoRevision.value = ''

  listaEspecialistas.value =
    []

  nuevoEspecialistaId.value =
    ''

  motivoRechazo.value =
    ''

  errorForm.value =
    ''
}

const cerrarModal = () => {

  solicitudActiva.value =
    null

  modoRevision.value =
    ''

  listaEspecialistas.value =
    []

  nuevoEspecialistaId.value =
    ''

  motivoRechazo.value =
    ''

  errorForm.value =
    ''
}

const setModo = async (
  modo,
) => {

  modoRevision.value =
    modo

  errorForm.value =
    ''

  nuevoEspecialistaId.value =
    ''

  if (
    modo !== 'ACEPTAR'
  ) {
    return
  }

  const endpoint =
    solicitudActiva.value
      .tipoEspecialista
      === 'ENTRENADOR'

      ? '/solicitudes-cambio/entrenadores'

      : '/solicitudes-cambio/nutricionistas'

  try {

    const respuesta =
      await api.get(endpoint)

    listaEspecialistas.value =
      respuesta.data

  } catch (error) {

    errorForm.value =
      extraerMensajeError(
        error,
        'No se pudieron cargar los especialistas disponibles.',
      )
  }
}

const procesarRespuesta =
  async (esAceptada) => {

    errorForm.value = ''

    if (
      esAceptada
      && !nuevoEspecialistaId.value
    ) {

      errorForm.value =
        'Seleccione un especialista para continuar.'

      return
    }

    if (
      !esAceptada
      && !motivoRechazo.value.trim()
    ) {

      errorForm.value =
        'No puede rechazar sin una justificación.'

      return
    }

    procesando.value = true

    try {

      await api.put(
        `/solicitudes-cambio/${solicitudActiva.value.id}/responder`,
        {
          aceptada:
            esAceptada,

          nuevoEspecialistaId:
            esAceptada
              ? nuevoEspecialistaId.value
              : null,

          justificacionRechazo:
            esAceptada
              ? null
              : motivoRechazo.value.trim(),
        },
      )

      mensajeSistema.value =
        esAceptada
          ? 'Solicitud aceptada y asignación actualizada correctamente.'
          : 'Solicitud rechazada correctamente.'

      cerrarModal()

      await cargarLista()

    } catch (error) {

      errorForm.value =
        extraerMensajeError(
          error,
          'No se pudo procesar la solicitud.',
        )

    } finally {

      procesando.value = false
    }
  }

onMounted(cargarLista)
</script>

<template>
  <section class="pagina">

    <div class="encabezado">

      <p class="seccion">
        Administración
      </p>

      <h2>
        Solicitudes de cambio pendientes
      </h2>

      <p>
        Revisa las solicitudes de reasignación
        enviadas por los deportistas.
      </p>

    </div>

    <p
      v-if="mensajeSistema"
      class="mensaje-sistema"
    >
      {{ mensajeSistema }}
    </p>

    <div class="tabla-contenedor">

      <table>

        <thead>
          <tr>
            <th>ID</th>
            <th>Deportista</th>
            <th>Tipo</th>
            <th>Motivo</th>
            <th>Fecha</th>
            <th>Acción</th>
          </tr>
        </thead>

        <tbody>

          <tr v-if="cargando">

            <td
              colspan="6"
              class="estado-tabla"
            >
              Cargando solicitudes...
            </td>

          </tr>

          <tr
            v-for="solicitud in solicitudes"
            :key="solicitud.id"
          >

            <td>
              {{ solicitud.id }}
            </td>

            <td>
              {{ solicitud.deportistaNombre }}
            </td>

            <td>
              {{ solicitud.tipoEspecialista }}
            </td>

            <td>
              {{ solicitud.motivo }}
            </td>

            <td>
              {{
                new Date(
                  solicitud.fechaHora,
                ).toLocaleString()
              }}
            </td>

            <td>

              <button
                class="boton-revisar"
                @click="abrirModal(solicitud)"
              >
                Revisar
              </button>

            </td>

          </tr>

          <tr
            v-if="
              !cargando
              && solicitudes.length === 0
            "
          >

            <td
              colspan="6"
              class="estado-tabla"
            >
              No hay solicitudes pendientes.
            </td>

          </tr>

        </tbody>

      </table>

    </div>

    <div
      v-if="solicitudActiva"
      class="modal-fondo"
      @click.self="cerrarModal"
    >

      <div class="modal-contenido">

        <h3>
          Revisar solicitud
          #{{ solicitudActiva.id }}
        </h3>

        <div class="resumen">

          <p>
            <strong>
              Deportista:
            </strong>

            {{
              solicitudActiva
                .deportistaNombre
            }}
          </p>

          <p>
            <strong>
              Tipo:
            </strong>

            {{
              solicitudActiva
                .tipoEspecialista
            }}
          </p>

          <p>
            <strong>
              Motivo:
            </strong>

            {{
              solicitudActiva
                .motivo
            }}
          </p>

        </div>

        <p
          v-if="errorForm"
          class="texto-error"
        >
          {{ errorForm }}
        </p>

        <div
          v-if="!modoRevision"
          class="acciones-principales"
        >

          <button
            class="boton-aceptar"
            @click="setModo('ACEPTAR')"
          >
            Aceptar cambio
          </button>

          <button
            class="boton-rechazar"
            @click="setModo('RECHAZAR')"
          >
            Rechazar cambio
          </button>

          <button
            class="boton-secundario"
            @click="cerrarModal"
          >
            Cancelar
          </button>

        </div>

        <div
          v-else-if="
            modoRevision === 'ACEPTAR'
          "
        >

          <label for="nuevo-especialista">
            Asignar nuevo
            {{
              solicitudActiva
                .tipoEspecialista
                .toLowerCase()
            }}
          </label>

          <select
            id="nuevo-especialista"
            v-model="nuevoEspecialistaId"
          >

            <option
              disabled
              value=""
            >
              -- Seleccione en la lista --
            </option>

            <option
              v-for="
                especialista
                in listaEspecialistas
              "
              :key="especialista.id"
              :value="especialista.id"
            >
              {{ especialista.nombre }}
              {{ especialista.apellido }}
            </option>

          </select>

          <div class="acciones">

            <button
              class="boton-secundario"
              :disabled="procesando"
              @click="cerrarModal"
            >
              Cancelar
            </button>

            <button
              class="boton-aceptar"
              :disabled="procesando"
              @click="procesarRespuesta(true)"
            >
              {{
                procesando
                  ? 'Procesando...'
                  : 'Confirmar'
              }}
            </button>

          </div>

        </div>

        <div v-else>

          <label
            for="justificacion-rechazo"
          >
            Justificación del rechazo
          </label>

          <textarea
            id="justificacion-rechazo"
            v-model="motivoRechazo"
            rows="4"
            maxlength="500"
            placeholder="Indique por qué se rechaza la solicitud..."
          />

          <div class="acciones">

            <button
              class="boton-secundario"
              :disabled="procesando"
              @click="cerrarModal"
            >
              Cancelar
            </button>

            <button
              class="boton-rechazar"
              :disabled="procesando"
              @click="
                procesarRespuesta(false)
              "
            >
              {{
                procesando
                  ? 'Procesando...'
                  : 'Rechazar solicitud'
              }}
            </button>

          </div>

        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>
.pagina {
  max-width: 1100px;
  margin: 0 auto;
}

.encabezado {
  margin-bottom: 24px;
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

.mensaje-sistema {
  padding: 12px 14px;
  border-radius: 8px;

  background: #e7f6ed;
  color: var(--color-success);
}

.tabla-contenedor {
  overflow-x: auto;

  border:
    1px solid var(--color-border);

  border-radius:
    var(--border-radius);

  background:
    var(--color-surface);

  box-shadow:
    var(--shadow-card);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 14px;
  text-align: left;

  border-bottom:
    1px solid var(--color-border);
}

th {
  background: #f8efe5;
  color: var(--color-primary);
}

.estado-tabla {
  padding: 28px;
  text-align: center;

  color:
    var(--color-text-secondary);
}

.boton-revisar,
.boton-aceptar,
.boton-rechazar,
.boton-secundario {
  padding: 9px 13px;
  border-radius: 7px;
  cursor: pointer;
}

.boton-revisar,
.boton-aceptar {
  background:
    var(--color-accent);

  color:
    var(--color-text);

  font-weight: 600;
}

.boton-rechazar {
  background:
    var(--color-error);

  color: white;
}

.boton-secundario {
  background: #eee7de;
  color: var(--color-text);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  width: min(560px, 100%);
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

.resumen {
  padding: 14px;
  border-radius: 8px;

  background: #fff6ea;
}

label {
  display: block;
  margin: 16px 0 8px;
  font-weight: 600;
}

select,
textarea {
  width: 100%;
  padding: 10px;

  border:
    1px solid var(--color-border);

  border-radius: 8px;
}

textarea {
  resize: vertical;
}

.acciones,
.acciones-principales {
  display: flex;
  flex-wrap: wrap;

  justify-content: flex-end;

  gap: 10px;
  margin-top: 18px;
}

.texto-error {
  padding: 10px;
  border-radius: 8px;

  background: #fdebec;
  color: var(--color-error);
}
</style>