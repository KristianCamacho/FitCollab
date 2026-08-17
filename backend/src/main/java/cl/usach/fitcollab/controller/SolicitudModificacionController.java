package cl.usach.fitcollab.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.dto.SolicitarAdaptacionRequest;
import cl.usach.fitcollab.dto.SolicitudResponse;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.services.SolicitudModificacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes-modificacion")
@CrossOrigin(origins = "http://localhost:5173")
public class SolicitudModificacionController {

    private final SolicitudModificacionService solicitudService;

    public SolicitudModificacionController(SolicitudModificacionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // CU-10: crear solicitud de adaptacion por tiempo
    @PostMapping("/adaptacion-tiempo")
    public ResponseEntity<SolicitudResponse> solicitarAdaptacion(
            @Valid @RequestBody SolicitarAdaptacionRequest request) {

        SolicitudModificacion solicitud = solicitudService.solicitarAdaptacionTiempo(
                request.getDeportistaId(),
                request.getRutinaId(),
                request.getTiempoDisponibleMin(),
                request.getMotivo());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SolicitudResponse(solicitud));
    }

    // Bandeja de solicitudes pendientes
    @GetMapping("/pendientes")
    public List<SolicitudResponse> listarPendientes() {
        return solicitudService.obtenerPendientes()
                .stream()
                .map(SolicitudResponse::new)
                .toList();
    }
}