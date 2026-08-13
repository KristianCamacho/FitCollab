package cl.usach.fitcollab.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "nutricionistas")
public class Nutricionista extends Especialista {

    @OneToMany(mappedBy = "nutricionista")
    private List<Deportista> deportistas = new ArrayList<>();

    public Nutricionista() {
    }

    public Nutricionista(String nombre, String apellido, String correo, String contrasena) {
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
        deportista.setNutricionista(this);
    }

    public void eliminarDeportista(Deportista deportista) {
        deportistas.remove(deportista);
        deportista.setNutricionista(null);
    }
}