package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "nutricionistas")
public class Nutricionista extends Especialista {

    public Nutricionista() {
    }

    public Nutricionista(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }
}
