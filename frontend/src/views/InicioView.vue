<script setup>
import { computed } from 'vue'

const usuario = computed(() => {
  const datos = localStorage.getItem('usuario')
  return datos ? JSON.parse(datos) : null
})
</script>

<template>
  <section class="inicio">
    <div class="encabezado">
      <span class="etiqueta">Inicio</span>

      <h1>
        Bienvenido, {{ usuario?.nombre }}
      </h1>

      <p>
        Desde aquí podrás acceder a las distintas funcionalidades de FitCollab.
      </p>
    </div>

    <div class="tarjeta">
      <h2>Tu cuenta</h2>

      <p>
        <strong>Nombre:</strong>
        {{ usuario?.nombre }} {{ usuario?.apellido }}
      </p>

      <p>
        <strong>Correo:</strong>
        {{ usuario?.correo }}
      </p>

      <p>
        <strong>Rol:</strong>
        {{ usuario?.rol }}
      </p>
    </div>
  </section>
</template>

<style scoped>
.inicio {
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
  max-width: 500px;

  padding: 24px;

  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-card);
}

.tarjeta h2 {
  margin-top: 0;
  color: var(--color-primary);
}
</style>
