package cl.usach.fitcollab.dto;

import cl.usach.fitcollab.entities.Rutina;

public class RutinaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private int duracionMinutos;
    private int intensidad;
    private String estado;
    private int calificacion;

    public RutinaResponse(Rutina rutina) {
        this.id = rutina.getId();
        this.nombre = rutina.getNombre();
        this.descripcion = rutina.getDescripcion();
        this.duracionMinutos = rutina.getDuracionMinutos();
        this.intensidad = rutina.getIntensidad();
        this.estado = rutina.getEstado() != null ? rutina.getEstado().name() : null;
        this.calificacion = rutina.getCalificacion();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public int getIntensidad() { return intensidad; }
    public String getEstado() { return estado; }
    public int getCalificacion() { return calificacion; }
}