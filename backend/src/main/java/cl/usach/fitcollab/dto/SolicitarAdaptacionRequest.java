package cl.usach.fitcollab.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SolicitarAdaptacionRequest {

    @NotNull(message = "El deportista es obligatorio")
    private Long deportistaId;

    @NotNull(message = "La rutina es obligatoria")
    private Long rutinaId;

    @NotNull(message = "El tiempo disponible es obligatorio")
    @Positive(message = "El tiempo disponible debe ser mayor a cero")
    private Integer tiempoDisponibleMin;

    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    private String motivo;

    public Long getDeportistaId() { return deportistaId; }
    public void setDeportistaId(Long deportistaId) { this.deportistaId = deportistaId; }

    public Long getRutinaId() { return rutinaId; }
    public void setRutinaId(Long rutinaId) { this.rutinaId = rutinaId; }

    public Integer getTiempoDisponibleMin() { return tiempoDisponibleMin; }
    public void setTiempoDisponibleMin(Integer t) { this.tiempoDisponibleMin = t; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}