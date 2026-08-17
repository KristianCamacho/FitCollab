package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int series;
    private int repeticiones;
    private String notaTecnica;

 
    @ManyToOne
    @JoinColumn(name = "catalogo_ejercicio_id")
    private CatalogoEjercicio catalogoEjercicio;

    public Ejercicio() {
    }

    public Ejercicio(CatalogoEjercicio catalogoEjercicio, int series, int repeticiones, String notaTecnica) {
        this.catalogoEjercicio = catalogoEjercicio;
        this.nombre = catalogoEjercicio != null ? catalogoEjercicio.getNombre() : null;
        this.series = series;
        this.repeticiones = repeticiones;
        this.notaTecnica = notaTecnica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public String getNotaTecnica() {
        return notaTecnica;
    }

    public void setNotaTecnica(String notaTecnica) {
        this.notaTecnica = notaTecnica;
    }

    public CatalogoEjercicio getCatalogoEjercicio() {
        return catalogoEjercicio;
    }

    public void setCatalogoEjercicio(CatalogoEjercicio catalogoEjercicio) {
        this.catalogoEjercicio = catalogoEjercicio;
    }
}