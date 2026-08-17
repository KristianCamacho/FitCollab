package cl.usach.fitcollab.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrenadores")
public class Entrenador extends Especialista {

    @OneToMany(mappedBy = "entrenador")
    private List<Deportista> deportistas = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "historial_id", unique = true)
    private Historial historial;

    public Entrenador() {
    }

    public Entrenador(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }

    public List<Deportista> getDeportistas() {
        return deportistas;
    }

    public void setDeportistas(List<Deportista> deportistas) {
        this.deportistas = deportistas;
    }

    public void agregarDeportista(Deportista deportista) {
        deportistas.add(deportista);
        deportista.setEntrenador(this);
    }

    public void eliminarDeportista(Deportista deportista) {
        deportistas.remove(deportista);
        deportista.setEntrenador(null);
    }

    public Historial getHistorial() {
        return historial;
    }

    public void setHistorial(Historial historial) {
        this.historial = historial;
    }
}