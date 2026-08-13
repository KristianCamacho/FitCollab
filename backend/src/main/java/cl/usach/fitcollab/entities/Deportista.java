package cl.usach.fitcollab.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "deportistas")
public class Deportista extends Usuario {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ficha_personal_id", unique = true)
    private FichaPersonal fichaPersonal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "historial_id", unique = true)
    private Historial historial;

    public Deportista() {
    }

    public Deportista(String nombre, String apellido, String correo, String contrasena) {
        super(nombre, apellido, correo, contrasena);
    }

    public FichaPersonal getFichaPersonal() {
        return fichaPersonal;
    }

    public void setFichaPersonal(FichaPersonal fichaPersonal) {
        this.fichaPersonal = fichaPersonal;
    }

    public Historial getHistorial() {
        return historial;
    }

    public void setHistorial(Historial historial) {
        this.historial = historial;
    }
}
