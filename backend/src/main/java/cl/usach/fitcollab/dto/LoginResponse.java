package cl.usach.fitcollab.dto;

public class LoginResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;

    public LoginResponse(Long id, String nombre, String apellido, String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
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

    public String getRol() {
        return rol;
    }
}