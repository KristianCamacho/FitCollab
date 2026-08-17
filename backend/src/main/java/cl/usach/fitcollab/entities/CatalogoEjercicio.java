package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalogo_ejercicios")
public class CatalogoEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String grupoMuscular;

    private String notaTecnicaSugerida;

    public CatalogoEjercicio() {
    }

    public CatalogoEjercicio(String nombre, String grupoMuscular, String notaTecnicaSugerida) {
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.notaTecnicaSugerida = notaTecnicaSugerida;
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

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public String getNotaTecnicaSugerida() {
        return notaTecnicaSugerida;
    }

    public void setNotaTecnicaSugerida(String notaTecnicaSugerida) {
        this.notaTecnicaSugerida = notaTecnicaSugerida;
    }
}