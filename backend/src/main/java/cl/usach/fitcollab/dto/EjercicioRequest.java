package cl.usach.fitcollab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class EjercicioRequest {

    @NotNull(message = "Debes seleccionar un ejercicio del catálogo")
    private Long catalogoEjercicioId;

    @NotNull(message = "Las series son obligatorias")
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    private Integer series;

    @NotNull(message = "Las repeticiones son obligatorias")
    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    private Integer repeticiones;

    private String notaTecnica;

    public EjercicioRequest() {
    }

    public Long getCatalogoEjercicioId() {
        return catalogoEjercicioId;
    }

    public void setCatalogoEjercicioId(Long catalogoEjercicioId) {
        this.catalogoEjercicioId = catalogoEjercicioId;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Integer getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(Integer repeticiones) {
        this.repeticiones = repeticiones;
    }

    public String getNotaTecnica() {
        return notaTecnica;
    }

    public void setNotaTecnica(String notaTecnica) {
        this.notaTecnica = notaTecnica;
    }
}