package cl.usach.fitcollab.dto;

import java.time.LocalDateTime;

import cl.usach.fitcollab.entities.Dieta;

public class DietaResponse {

    private Long id;
    private String comidas;
    private String porciones;
    private String horarios;
    private String sugerenciaAlimenticia;
    private LocalDateTime fechaCreacion;

    private Long creadorId;
    private String creadorNombre;

    private Long deportistaId;
    private String deportistaNombre;

    public DietaResponse(Dieta dieta) {
        this.id = dieta.getId();
        this.comidas = dieta.getComidas();
        this.porciones = dieta.getPorciones();
        this.horarios = dieta.getHorarios();
        this.sugerenciaAlimenticia = dieta.getSugerenciaAlimenticia();
        this.fechaCreacion = dieta.getFechaCreacion();

        if (dieta.getCreador() != null) {
            this.creadorId = dieta.getCreador().getId();
            this.creadorNombre =
                    dieta.getCreador().getNombre()
                    + " "
                    + dieta.getCreador().getApellido();
        }

        if (dieta.getDeportista() != null) {
            this.deportistaId = dieta.getDeportista().getId();
            this.deportistaNombre =
                    dieta.getDeportista().getNombre()
                    + " "
                    + dieta.getDeportista().getApellido();
        }
    }

    public Long getId() {
        return id;
    }

    public String getComidas() {
        return comidas;
    }

    public String getPorciones() {
        return porciones;
    }

    public String getHorarios() {
        return horarios;
    }

    public String getSugerenciaAlimenticia() {
        return sugerenciaAlimenticia;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public String getCreadorNombre() {
        return creadorNombre;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public String getDeportistaNombre() {
        return deportistaNombre;
    }
}