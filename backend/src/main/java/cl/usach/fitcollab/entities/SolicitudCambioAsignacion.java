package cl.usach.fitcollab.entities;

import java.time.LocalDateTime;

import cl.usach.fitcollab.enums.EstadoSolicitud;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitudes_cambio_asignacion")
public class SolicitudCambioAsignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    private String motivo;

    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "deportista_id", nullable = false)
    private Deportista deportista;

    @Column(name = "justificacion_rechazo", length = 500)
    private String justificacionRechazo;

    @Column(name = "tipo_especialista")
    private String tipoEspecialista;

    public SolicitudCambioAsignacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Deportista getDeportista() {
        return deportista;
    }

    public void setDeportista(Deportista deportista) {
        this.deportista = deportista;
    }

    public String getJustificacionRechazo() { return justificacionRechazo; }

    public void setJustificacionRechazo(String justificacionRechazo) { this.justificacionRechazo = justificacionRechazo; }

    public String getTipoEspecialista() { return tipoEspecialista; }

    public void setTipoEspecialista(String tipoEspecialista) { this.tipoEspecialista = tipoEspecialista; }

}
