package cl.usach.fitcollab.dto;

import java.util.List;

import cl.usach.fitcollab.entities.Rutina;

public class RutinaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private int duracionMinutos;
    private int intensidad;
    private String estado;
    private int calificacion;
    private Long deportistaId;
    private String deportistaNombre;
    private Long creadorId;
    private String creadorNombre;
    private List<EjercicioResponse> ejercicios;

    public RutinaResponse(Rutina rutina) {
        this.id = rutina.getId();
        this.nombre = rutina.getNombre();
        this.descripcion = rutina.getDescripcion();
        this.duracionMinutos = rutina.getDuracionMinutos();
        this.intensidad = rutina.getIntensidad();
        this.estado = rutina.getEstado() != null ? rutina.getEstado().name() : null;
        this.calificacion = rutina.getCalificacion();

        if (rutina.getDeportista() != null) {
            this.deportistaId = rutina.getDeportista().getId();
            this.deportistaNombre = rutina.getDeportista().getNombre()
                    + " " + rutina.getDeportista().getApellido();
        }

        if (rutina.getCreador() != null) {
            this.creadorId = rutina.getCreador().getId();
            this.creadorNombre = rutina.getCreador().getNombre()
                    + " " + rutina.getCreador().getApellido();
        }

        if (rutina.getEjercicios() != null) {
            this.ejercicios = rutina.getEjercicios().stream()
                    .map(EjercicioResponse::new)
                    .toList();
        }
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public int getIntensidad() { return intensidad; }
    public String getEstado() { return estado; }
    public int getCalificacion() { return calificacion; }
    public Long getDeportistaId() { return deportistaId; }
    public String getDeportistaNombre() { return deportistaNombre; }
    public Long getCreadorId() { return creadorId; }
    public String getCreadorNombre() { return creadorNombre; }
    public List<EjercicioResponse> getEjercicios() { return ejercicios; }
}