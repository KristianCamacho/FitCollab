package cl.usach.fitcollab.dto;

import java.time.LocalDateTime;

import cl.usach.fitcollab.entities.SolicitudModificacion;

public class SolicitudResponse {

    private Long id;
    private String tipo;
    private String estado;
    private String motivo;
    private LocalDateTime fechaHora;
    private Integer tiempoDisponibleMin;
    private Long rutinaId;
    private String rutinaNombre;
    private Integer rutinaDuracionMin;
    private Long deportistaId;
    private String deportistaNombre;

    public SolicitudResponse(SolicitudModificacion s) {
        this.id = s.getId();
        this.tipo = s.getTipo() != null ? s.getTipo().name() : null;
        this.estado = s.getEstado() != null ? s.getEstado().name() : null;
        this.motivo = s.getMotivo();
        this.fechaHora = s.getFechaHora();
        this.tiempoDisponibleMin = s.getTiempoDisponibleMin();
        this.deportistaId = s.getDeportista().getId();
        this.deportistaNombre =
                s.getDeportista().getNombre()
                + " "
                + s.getDeportista().getApellido();

        if (s.getRutina() != null) {
            this.rutinaId = s.getRutina().getId();
            this.rutinaNombre = s.getRutina().getNombre();
            this.rutinaDuracionMin = s.getRutina().getDuracionMinutos();
        }

        if (s.getDeportista() != null) {
            this.deportistaId = s.getDeportista().getId();
        }
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Integer getTiempoDisponibleMin() { return tiempoDisponibleMin; }
    public Long getRutinaId() { return rutinaId; }
    public String getRutinaNombre() { return rutinaNombre; }
    public Integer getRutinaDuracionMin() { return rutinaDuracionMin; }
    public Long getDeportistaId() { return deportistaId; }
    public String getDeportistaNombre() {return deportistaNombre;}
}