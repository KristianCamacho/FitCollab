package cl.usach.fitcollab.dto;

import lombok.Data;

@Data
public class EjercicioDTO {
    private Long id;
    private String nombre;
    private Integer series;
    private Integer repeticiones;
    private String notaTecnica;
}