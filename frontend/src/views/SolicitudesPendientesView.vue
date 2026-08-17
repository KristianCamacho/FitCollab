<template>
  <div class="contenedor">
    <h2>Solicitudes de Cambio Pendientes</h2>
    
    <table border="1" width="100%" class="tabla-solicitudes">
      <thead>
        <tr>
          <th>ID</th>
          <th>Deportista</th>
          <th>Especialidad</th>
          <th>Motivo</th>
          <th>Acción</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="s in solicitudes" :key="s.id">
          <td>{{ s.id }}</td>
          <td>{{ s.deportista.nombre }} {{ s.deportista.apellido }}</td>
          <td>{{ s.tipoEspecialista }}</td>
          <td>{{ s.motivo }}</td>
          <td>
            <button class="btn" @click="abrirModal(s)">Revisar</button>
          </td>
        </tr>
        <tr v-if="solicitudes.length === 0">
          <td colspan="5" style="text-align: center;">No hay solicitudes pendientes.</td>
        </tr>
      </tbody>
    </table>

    <div v-if="solicitudActiva" class="modal-fondo">
      <div class="modal-contenido">
        <h3>Revisar Solicitud #{{ solicitudActiva.id }}</h3>
        <p><strong>Deportista:</strong> {{ solicitudActiva.deportista.nombre }}</p>
        <p><strong>Motivo original:</strong> {{ solicitudActiva.motivo }}</p>

        <p v-if="errorForm" class="texto-error">{{ errorForm }}</p>

        <div v-if="!modoRevision">
          <p>¿Qué decisión va a tomar?</p>
          <div class="acciones-principales">
            <button class="btn btn-aceptar" @click="setModo('ACEPTAR')">Aceptar Cambio</button>
            <button class="btn btn-rechazar" @click="setModo('RECHAZAR')">Rechazar Cambio</button>
            <button class="btn" @click="cerrarModal">Cancelar</button>
          </div>
        </div>

        <div v-if="modoRevision === 'ACEPTAR'">
          <label>Asignar nuevo Especialista:</label>
          <select v-model="nuevoEspecialistaId">
            <option disabled value="">-- Seleccione en la lista --</option>
            <option v-for="esp in listaEspecialista es" :key="esp.id" :value="esp.id">
              {{ esp.nombre }} {{ esp.apellido }}
            </option>
          </select>
          <div class="acciones">
            <button class="btn" @click="cerrarModal">Cancelar</button>
            <button class="btn btn-confirmar" @click="procesarRespuesta(true)">Confirmar</button>
          </div>
        </div>

        <div v-if="modoRevision === 'RECHAZAR'">
          <label>Indique la justificación del rechazo:</label>
          <textarea v-model="motivoRechazo" rows="4" placeholder="Ej: No hay Especialistas disponibles..."></textarea>
          <div class="acciones">
            <button class="btn" @click="cerrarModal">Cancelar</button>
            <button class="btn btn-rechazar" @click="procesarRespuesta(false)">Rechazar Solicitud</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const solicitudes = ref([]);
const solicitudActiva = ref(null);
const modoRevision = ref(''); 
const listaEspecialistas = ref([]);
const nuevoEspecialistaId = ref('');
const motivoRechazo = ref('');
const errorForm = ref('');

const cargarLista = async () => {
  try {
    const respuesta = await axios.get('http://localhost:8080/api/solicitudes-cambio/pendientes');
    solicitudes.value = respuesta.data;
  } catch (error) {
    console.error("Fallo al obtener solicitudes:", error);
  }
};

onMounted(() => {
  cargarLista();
});

const abrirModal = (solicitud) => {
  solicitudActiva.value = solicitud;
  modoRevision.value = '';
  errorForm.value = '';
};

const cerrarModal = () => {
  solicitudActiva.value = null;
  modoRevision.value = '';
  nuevoEspecialistaId.value = '';
  motivoRechazo.value = '';
  errorForm.value = '';
};

const setModo = async (modo) => {
  modoRevision.value = modo;
  errorForm.value = '';

  if (modo === 'ACEPTAR') {
    const endpoint = solicitudActiva.value.tipoEspecialista === 'ENTRENADOR' 
        ? '/entrenadores' 
        : '/nutricionistas';
    
    try {
      const respuesta = await axios.get(`http://localhost:8080/api/solicitudes-cambio${endpoint}`);
      listaEspecialistas.value = respuesta.data;
    } catch (error) {
      console.error("Error cargando Especialistas", error);
      errorForm.value = "No se pudieron cargar los especialistas disponibles.";
    }
  }
};

const procesarRespuesta = async (esAceptada) => {
  errorForm.value = '';

  if (esAceptada && !nuevoEspecialistaId.value) {
    errorForm.value = "Seleccione un Especialista para continuar.";
    return;
  }
  if (!esAceptada && !motivoRechazo.value.trim()) {
    errorForm.value = "No puede rechazar sin una justificación.";
    return;
  }

  try {
    const payload = {
      aceptada: esAceptada,
      nuevoEspecialistaId: nuevoEspecialistaId.value,
      justificacionRechazo: motivoRechazo.value
    };
    
    console.log("Enviando resolución:", payload);

    await axios.put(`http://localhost:8080/api/solicitudes-cambio/${solicitudActiva.value.id}/responder`, payload);
    
    cerrarModal();
    cargarLista();
  } catch (error) {
    console.error(error);
    if (error.response?.status === 400) {
      errorForm.value = error.response.data;
    } else {
      errorForm.value = "Error al enviar la solicitud.";
    }
  }
};
</script>

<style scoped>
.modal-fondo {
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%;
  background: rgba(0,0,0,0.6); 
  display: flex; 
  justify-content: center; 
  align-items: center;
}

.modal-contenido {
  background: white; 
  padding: 25px; 
  border-radius: 8px; 
  width: 450px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

textarea, select { 
  width: 100%; 
  margin: 10px 0; 
  padding: 8px; 
  box-sizing: border-box; 
}

.acciones, .acciones-principales { 
  display: flex; 
  justify-content: flex-end; 
  gap: 10px;
  margin-top: 15px; 
}

.btn { 
  padding: 8px 15px; 
  cursor: pointer; 
  border-radius: 4px;
  border: 1px solid #ccc;
}

.btn-aceptar, .btn-confirmar { 
  background-color: #2e7d32; 
  color: white; 
  border: none; 
}

.btn-rechazar { 
  background-color: #c62828; 
  color: white; 
  border: none; 
}

.tabla-solicitudes { 
  margin-top: 20px; 
  border-collapse: collapse; 
}

th, td { 
  padding: 12px; 
  text-align: left; 
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
}

.texto-error {
  color: red;
  font-size: 0.9em;
  margin-bottom: 10px;
}
</style>