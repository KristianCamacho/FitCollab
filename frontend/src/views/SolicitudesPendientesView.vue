<template>
  <div class="contenedor">
    <h2>Solicitudes de Cambio Pendientes</h2>
    
    <table border="1" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Deportista</th>
          <th>Tipo</th>
          <th>Motivo</th>
          <th>Acción</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="solicitud in solicitudes" :key="solicitud.id">
          <td>{{ solicitud.id }}</td>
          <td>{{ solicitud.deportista.nombre }} {{ solicitud.deportista.apellido }}</td>
          <td>{{ solicitud.tipoEspecialista }}</td>
          <td>{{ solicitud.motivo }}</td>
          <td>
            <button class="btn" @click="abrirModal(solicitud)">Revisar</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="solicitudActiva" class="modal-fondo">
      <div class="modal-contenido">
        <h3>Revisar Solicitud #{{ solicitudActiva.id }}</h3>
        <p><strong>Deportista:</strong> {{ solicitudActiva.deportista.nombre }}</p>
        <p><strong>Motivo:</strong> {{ solicitudActiva.motivo }}</p>

        <div v-if="!decisionTomada">
          <p>¿Qué desea hacer con esta solicitud?</p>
          <button class="btn btn-aceptar" @click="prepararAceptar">Aceptar</button>
          <button class="btn btn-rechazar" @click="prepararRechazar">Rechazar</button>
          <button class="btn" @click="cerrarModal">Cancelar</button>
        </div>

        <div v-if="decisionTomada === 'ACEPTAR'">
          <label>Seleccione el nuevo especialista:</label>
          <select v-model="nuevoEspecialistaId">
            <option disabled value="">-- Seleccione uno --</option>
            <option v-for="prof in especialistasDisponibles" :key="prof.id" :value="prof.id">
              {{ prof.nombre }} {{ prof.apellido }}
            </option>
          </select>
          <div class="acciones">
            <button class="btn btn-confirmar" @click="enviarRespuesta(true)">Confirmar Aceptación</button>
            <button class="btn" @click="cerrarModal">Cancelar</button>
          </div>
        </div>

        <div v-if="decisionTomada === 'RECHAZAR'">
          <label>Justificación del rechazo:</label>
          <textarea v-model="justificacion" rows="4" placeholder="Escriba la justificación aqui"></textarea>
          <div class="acciones">
            <button class="btn btn-confirmar" @click="enviarRespuesta(false)">Confirmar Rechazo</button>
            <button class="btn" @click="cerrarModal">Cancelar</button>
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
const decisionTomada = ref('');
const especialistasDisponibles = ref([]);
const nuevoEspecialistaId = ref('');
const justificacion = ref('');

const cargarSolicitudes = async () => {
  try {
    const respuesta = await axios.get('http://localhost:8080/api/solicitudes-cambio/pendientes');
    solicitudes.value = respuesta.data;
  } catch (error) {
    console.error("Error al cargar solicitudes", error);
  }
};

onMounted(() => {
  cargarSolicitudes();
});

const abrirModal = (solicitud) => {
  solicitudActiva.value = solicitud;
  decisionTomada.value = '';
};

const cerrarModal = () => {
  solicitudActiva.value = null;
  decisionTomada.value = '';
  nuevoEspecialistaId.value = '';
  justificacion.value = '';
};

const prepararAceptar = async () => {
  decisionTomada.value = 'ACEPTAR';
  const ruta = solicitudActiva.value.tipoEspecialista === 'ENTRENADOR' 
      ? '/entrenadores' 
      : '/nutricionistas';
  
  try {
    const res = await axios.get(`http://localhost:8080/api/solicitudes-cambio${ruta}`);
    especialistasDisponibles.value = res.data;
  } catch (error) {
    alert("Error al cargar especialistas disponibles.");
  }
};

const prepararRechazar = () => {
  decisionTomada.value = 'RECHAZAR';
};

const enviarRespuesta = async (aceptada) => {
  if (aceptada && !nuevoEspecialistaId.value) {
    alert("Debe seleccionar un especialista.");
    return;
  }
  if (!aceptada && (!justificacion.value || justificacion.value.trim() === '')) {
    alert("Debe ingresar una justificación para poder rechazar.");
    return;
  }

  try {
    await axios.put(`http://localhost:8080/api/solicitudes-cambio/${solicitudActiva.value.id}/responder`, {
      aceptada: aceptada,
      nuevoEspecialistaId: nuevoEspecialistaId.value,
      justificacionRechazo: justificacion.value
    });
    alert("Respuesta guardada exitósamente.");
    cerrarModal();
    cargarSolicitudes();
  } catch (error) {
    if (error.response && error.response.status === 400) {
      alert("Error: " + error.response.data);
    } else {
      alert("Error al procesar la respuesta.");
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
  background: rgba(0,0,0,0.5); 
  display: flex; 
  justify-content: center; 
  align-items: center;
}

.modal-contenido {
  background: white; 
  padding: 20px; 
  border-radius: 8px; 
  width: 400px;
}

textarea, select { 
    width: 100%; 
    margin: 10px 0; 
    padding: 8px; 
}

.acciones { 
    display: flex; 
    justify-content: space-between; 
    margin-top: 10px; 
}

.btn { 
    padding: 8px 12px; 
    cursor: pointer; 
    margin-right: 5px;
}

.btn-aceptar { 
    background-color: #4CAF50; 
    color: white; 
    border: none; 
}

.btn-rechazar { 
    background-color: #f44336; 
    color: white; 
    border: none; 
}

table { 
    margin-top: 20px; 
    border-collapse: collapse; 
}

th, td { 
    padding: 10px; 
    text-align: left; 
}
</style>
