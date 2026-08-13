package cl.usach.fitcollab.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario {

    public Administrador() {
    }

    public Administrador(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }
}
