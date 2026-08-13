package cl.usach.fitcollab.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "dietas")
public class Dieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comidas;
    private String porciones;
    private String horarios;

    @ElementCollection
    private List<String> alimentosExcluidos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "deportista_id")
    private Deportista deportista;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Nutricionista creador;

    private String sugerenciaAlimenticia;

    public Dieta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComidas() {
        return comidas;
    }

    public void setComidas(String comidas) {
        this.comidas = comidas;
    }

    public String getPorciones() {
        return porciones;
    }

    public void setPorciones(String porciones) {
        this.porciones = porciones;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public List<String> getAlimentosExcluidos() {
        return alimentosExcluidos;
    }

    public void setAlimentosExcluidos(List<String> alimentosExcluidos) {
        this.alimentosExcluidos = alimentosExcluidos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Nutricionista getCreador() {
        return creador;
    }

    public void setCreador(Nutricionista creador) {
        this.creador = creador;
    }

    public String getSugerenciaAlimenticia() {
        return sugerenciaAlimenticia;
    }

    public void setSugerenciaAlimenticia(String sugerenciaAlimenticia) {
        this.sugerenciaAlimenticia = sugerenciaAlimenticia;
    }

    public Deportista getDeportista() {
    return deportista;
    }

    public void setDeportista(Deportista deportista) {
        this.deportista = deportista;
    }
}
