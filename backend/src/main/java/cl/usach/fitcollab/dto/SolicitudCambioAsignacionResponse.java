package cl.usach.fitcollab.dto;

import java.time.LocalDateTime;

public class SolicitudCambioAsignacionResponse {

    private Long id;
    private Long deportistaId;
    private String tipoEspecialista;
    private String motivo;
    private String estado;
    private LocalDateTime fechaHora;

    public SolicitudCambioAsignacionResponse(
            Long id,
            Long deportistaId,
            String tipoEspecialista,
            String motivo,
            String estado,
            LocalDateTime fechaHora) {

        this.id = id;
        this.deportistaId = deportistaId;
        this.tipoEspecialista = tipoEspecialista;
        this.motivo = motivo;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public Long getId() {
        return id;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public String getTipoEspecialista() {
        return tipoEspecialista;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
