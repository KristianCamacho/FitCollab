package cl.usach.fitcollab.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutinaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionMinutos;
    private Integer intensidad;
    private Long deportistaId;
    private String deportistaNombre;
    private Long creadorId;
    private List<EjercicioDTO> ejercicios;
}