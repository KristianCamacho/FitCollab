package cl.usach.fitcollab.dto;

import cl.usach.fitcollab.entities.Deportista;


public class DeportistaResumenResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String correo;

    public DeportistaResumenResponse(Deportista deportista) {
        this.id = deportista.getId();
        this.nombre = deportista.getNombre();
        this.apellido = deportista.getApellido();
        this.correo = deportista.getCorreo();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }
}
