package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrenadores")
public class Entrenador extends Especialista {

    public Entrenador() {
    }

    public Entrenador(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }
}
