package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "especialistas")
public abstract class Especialista extends Usuario {

    public Especialista() {
    }

    public Especialista(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }
}
