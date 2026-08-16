<template>
  <div class="contenedor">
    <h2>Mi Equipo Profesional</h2>
    
    <button class="btn" @click="abrirModal('ENTRENADOR')">Pedir cambio de entrenador</button>
    <button class="btn" @click="abrirModal('NUTRICIONISTA')">Pedir cambio de nutricionista</button>

    <div v-if="mostrarModal" class="modal-fondo">
      <div class="modal-contenido">
        <h3>Solicitar cambio de {{ tipoEspecialista }}</h3>
        
        <label>Motivo del cambio:</label>
        <textarea v-model="motivo" rows="4" placeholder="Escriba el motivo del cambio aquí"></textarea>
        
        <div class="acciones">
          <button class="btn btn-cancelar" @click="cerrarModal">Cancelar</button>
          <button class="btn btn-confirmar" @click="enviarSolicitud">Enviar Solicitud</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const mostrarModal = ref(false);
const tipoEspecialista = ref('');
const motivo = ref('');

const usuarioString = localStorage.getItem('usuario');
const usuarioLogueado = usuarioString ? JSON.parse(usuarioString) : null;

const deportistaId = usuarioLogueado ? usuarioLogueado.id : null;

const abrirModal = (tipo) => {
  tipoEspecialista.value = tipo;
  mostrarModal.value = true;
};

const cerrarModal = () => {
  mostrarModal.value = false;
  motivo.value = '';
};

const enviarSolicitud = async () => {
  if (!deportistaId) {
    alert("Error: Sesión activa no encontrada. Inicie sesión nuevamente.");
    return;
  }

  try {
    const respuesta = await axios.post('http://localhost:8080/api/solicitudes-cambio', {
      deportistaId: deportistaId,
      tipoEspecialista: tipoEspecialista.value,
      motivo: motivo.value
    });
    
    alert("Solicitud enviada exitósamente.");
    cerrarModal();
  } catch (error) {
    if (error.response && error.response.status === 400) {
      alert("Error: " + error.response.data);
    } else {
      alert("Error al enviar la solicitud.");
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
  display: flex; 
  flex-direction: column;
}

textarea { 
  width: 100%; 
  margin: 10px 0; 
}

.acciones { 
  display: flex; 
  justify-content: space-between; 
  margin-top: 10px; 
}

.btn { 
  padding: 8px 12px; 
  cursor: pointer;
  }
</style>
