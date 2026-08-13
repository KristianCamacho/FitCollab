package cl.usach.fitcollab.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fichas_personales")
public class FichaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String datosPersonales;

    private String objetivos;

    private String restriccionesAlimenticias;

    private LocalDateTime fechaActualizacion;

    public FichaPersonal() {
    }

    public FichaPersonal(
            String datosPersonales,
            String objetivos,
            String restriccionesAlimenticias,
            LocalDateTime fechaActualizacion) {

        this.datosPersonales = datosPersonales;
        this.objetivos = objetivos;
        this.restriccionesAlimenticias = restriccionesAlimenticias;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatosPersonales() {
        return datosPersonales;
    }

    public void setDatosPersonales(String datosPersonales) {
        this.datosPersonales = datosPersonales;
    }

    public String getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(String objetivos) {
        this.objetivos = objetivos;
    }

    public String getRestriccionesAlimenticias() {
        return restriccionesAlimenticias;
    }

    public void setRestriccionesAlimenticias(String restriccionesAlimenticias) {
        this.restriccionesAlimenticias = restriccionesAlimenticias;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
