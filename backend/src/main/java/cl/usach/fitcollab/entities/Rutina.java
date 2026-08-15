package cl.usach.fitcollab.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cl.usach.fitcollab.enums.EstadoRutina;

@Entity
@Table(name = "rutinas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private int duracionMinutos;
    private int intensidad;

    @Enumerated(EnumType.STRING)
    private EstadoRutina estado;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    @JsonIgnoreProperties({"rutinas", "hibernateLazyInitializer", "handler"})
    private Entrenador creador;

    @ManyToOne
    @JoinColumn(name = "deportista_id")
    @JsonIgnoreProperties({
        "dietas", 
        "rutinas", 
        "solicitudesModificacion", 
        "solicitudesCambioAsignacion", 
        "hibernateLazyInitializer", 
        "handler"
    })
    private Deportista deportista;

    private int calificacion;
    private int disponibilidadTiempo;
    private int nivelCansancio;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rutina_id")
    private List<Ejercicio> ejercicios = new ArrayList<>();

    public Rutina() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public int getIntensidad() { return intensidad; }
    public void setIntensidad(int intensidad) { this.intensidad = intensidad; }

    public EstadoRutina getEstado() { return estado; }
    public void setEstado(EstadoRutina estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Entrenador getCreador() { return creador; }
    public void setCreador(Entrenador creador) { this.creador = creador; }

    public Deportista getDeportista() { return deportista; }
    public void setDeportista(Deportista deportista) { this.deportista = deportista; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public int getDisponibilidadTiempo() { return disponibilidadTiempo; }
    public void setDisponibilidadTiempo(int disponibilidadTiempo) { this.disponibilidadTiempo = disponibilidadTiempo; }

    public int getNivelCansancio() { return nivelCansancio; }
    public void setNivelCansancio(int nivelCansancio) { this.nivelCansancio = nivelCansancio; }

    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Ejercicio> ejercicios) { this.ejercicios = ejercicios; }

    // --- Métodos de compatibilidad para Servicios y Controladores ---

    public Long getDeportistaId() {
        return (deportista != null) ? deportista.getId() : null;
    }

    public void setDeportistaId(Long deportistaId) {
        if (deportistaId == null) {
            this.deportista = null;
        }
    }

    public Long getCreadorId() {
        return (creador != null) ? creador.getId() : null;
    }

    public void setCreadorId(Long creadorId) {
        if (creadorId == null) {
            this.creador = null;
        }
    }
}