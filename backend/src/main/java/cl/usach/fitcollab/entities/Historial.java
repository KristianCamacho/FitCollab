package cl.usach.fitcollab.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "historiales")
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<Rutina> rutinasAnteriores = new ArrayList<>();

    @ManyToMany
    private List<Dieta> dietasAnteriores = new ArrayList<>();

    public Historial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Rutina> getRutinasAnteriores() {
        return rutinasAnteriores;
    }

    public void setRutinasAnteriores(List<Rutina> rutinasAnteriores) {
        this.rutinasAnteriores = rutinasAnteriores;
    }

    public List<Dieta> getDietasAnteriores() {
        return dietasAnteriores;
    }

    public void setDietasAnteriores(List<Dieta> dietasAnteriores) {
        this.dietasAnteriores = dietasAnteriores;
    }

    public void agregarRutina(Rutina rutina) {
        rutinasAnteriores.add(rutina);
    }

    public void agregarDieta(Dieta dieta) {
        dietasAnteriores.add(dieta);
    }
}
