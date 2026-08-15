package cl.usach.fitcollab.dto;

import lombok.Data;

@Data
public class SolicitudModificacionRequest {
    private String tipo;
    private String motivo;
    private Long deportistaId;
    private Long especialistaId;
    private Long rutinaId;
}