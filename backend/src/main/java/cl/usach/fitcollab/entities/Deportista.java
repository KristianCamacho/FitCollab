package cl.usach.fitcollab.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "deportistas")
public class Deportista extends Usuario {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ficha_personal_id", unique = true)
    private FichaPersonal fichaPersonal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "historial_id", unique = true)
    private Historial historial;

    @ManyToOne
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;

    @ManyToOne
    @JoinColumn(name = "nutricionista_id")
    private Nutricionista nutricionista;

    @OneToMany(mappedBy = "deportista")
    private List<Rutina> rutinas = new ArrayList<>();

    @OneToMany(mappedBy = "deportista")
    private List<Dieta> dietas = new ArrayList<>();

    @OneToMany(mappedBy = "deportista")
    private List<SolicitudModificacion> solicitudesModificacion = new ArrayList<>();

    @OneToMany(mappedBy = "deportista")
    private List<SolicitudCambioAsignacion> solicitudesCambioAsignacion = new ArrayList<>();

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

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public Nutricionista getNutricionista() {
        return nutricionista;
    }

    public void setNutricionista(Nutricionista nutricionista) {
        this.nutricionista = nutricionista;
    }

    public List<Rutina> getRutinas() {
        return rutinas;
    }

    public void setRutinas(List<Rutina> rutinas) {
        this.rutinas = rutinas;
    }

    public void agregarRutina(Rutina rutina) {
        rutinas.add(rutina);
        rutina.setDeportista(this);
    }

    public void eliminarRutina(Rutina rutina) {
        rutinas.remove(rutina);
        rutina.setDeportista(null);
    }

    public List<Dieta> getDietas() {
    return dietas;
    }

    public void setDietas(List<Dieta> dietas) {
        this.dietas = dietas;
    }

    public void agregarDieta(Dieta dieta) {
        dietas.add(dieta);
        dieta.setDeportista(this);
    }

    public void eliminarDieta(Dieta dieta) {
        dietas.remove(dieta);
        dieta.setDeportista(null);
    }

    public List<SolicitudModificacion> getSolicitudesModificacion() {
    return solicitudesModificacion;
    }

    public void setSolicitudesModificacion(List<SolicitudModificacion> solicitudesModificacion) {
        this.solicitudesModificacion = solicitudesModificacion;
    }

    public void agregarSolicitudModificacion(SolicitudModificacion solicitud) {
        solicitudesModificacion.add(solicitud);
        solicitud.setDeportista(this);
    }

    public List<SolicitudCambioAsignacion> getSolicitudesCambioAsignacion() {
    return solicitudesCambioAsignacion;
    }

    public void setSolicitudesCambioAsignacion(
            List<SolicitudCambioAsignacion> solicitudesCambioAsignacion) {
        this.solicitudesCambioAsignacion = solicitudesCambioAsignacion;
    }

    public void agregarSolicitudCambioAsignacion(SolicitudCambioAsignacion solicitud) {
        solicitudesCambioAsignacion.add(solicitud);
        solicitud.setDeportista(this);
    }
}
