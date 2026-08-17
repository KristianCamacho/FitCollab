package cl.usach.fitcollab.dto;

import cl.usach.fitcollab.entities.Ejercicio;

public class EjercicioResponse {

    private Long id;
    private String nombre;
    private String grupoMuscular;
    private int series;
    private int repeticiones;
    private String notaTecnica;

    public EjercicioResponse(Ejercicio ejercicio) {
        this.id = ejercicio.getId();
        this.nombre = ejercicio.getNombre();
        this.grupoMuscular = ejercicio.getCatalogoEjercicio() != null
                ? ejercicio.getCatalogoEjercicio().getGrupoMuscular()
                : null;
        this.series = ejercicio.getSeries();
        this.repeticiones = ejercicio.getRepeticiones();
        this.notaTecnica = ejercicio.getNotaTecnica();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public String getNotaTecnica() {
        return notaTecnica;
    }
}