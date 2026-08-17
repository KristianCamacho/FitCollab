package cl.usach.fitcollab.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class CrearRutinaRequest {

    @NotBlank(message = "El nombre de la rutina es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracionMinutos;

    @NotNull(message = "La intensidad es obligatoria")
    @Min(value = 1, message = "La intensidad debe ser mayor a 0")
    private Integer intensidad;

    @NotEmpty(message = "Debes seleccionar al menos un deportista")
    private List<Long> deportistaIds;

    @NotEmpty(message = "La rutina debe tener al menos un ejercicio")
    @Valid
    private List<EjercicioRequest> ejercicios;

    public CrearRutinaRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Integer getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(Integer intensidad) {
        this.intensidad = intensidad;
    }

    public List<Long> getDeportistaIds() {
        return deportistaIds;
    }

    public void setDeportistaIds(List<Long> deportistaIds) {
        this.deportistaIds = deportistaIds;
    }

    public List<EjercicioRequest> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<EjercicioRequest> ejercicios) {
        this.ejercicios = ejercicios;
    }
}
