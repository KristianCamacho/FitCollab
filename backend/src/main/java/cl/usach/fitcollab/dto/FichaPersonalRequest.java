package cl.usach.fitcollab.dto;

public class FichaPersonalRequest {

    private Integer edad;
    private Double peso;
    private Double altura;
    private String objetivo;
    private String restriccionesAlimenticias;

    public FichaPersonalRequest() {
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getRestriccionesAlimenticias() {
        return restriccionesAlimenticias;
    }

    public void setRestriccionesAlimenticias(String restriccionesAlimenticias) {
        this.restriccionesAlimenticias = restriccionesAlimenticias;
    }
}
