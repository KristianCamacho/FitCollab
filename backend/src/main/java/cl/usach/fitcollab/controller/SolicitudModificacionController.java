package cl.usach.fitcollab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    public SolicitudModificacionController(
            SolicitudModificacionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // CU-10: crear solicitud de adaptación por tiempo
    @PostMapping("/adaptacion-tiempo")
    public ResponseEntity<SolicitudResponse> solicitarAdaptacion(
            @Valid @RequestBody SolicitarAdaptacionRequest request) {

        SolicitudModificacion solicitud =
                solicitudService.solicitarAdaptacionTiempo(
                        request.getDeportistaId(),
                        request.getRutinaId(),
                        request.getTiempoDisponibleMin(),
                        request.getMotivo());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SolicitudResponse(solicitud));
    }

    // Solicitudes pendientes generales
    @GetMapping("/pendientes")
    public List<SolicitudResponse> listarPendientes() {
        return solicitudService.obtenerPendientes()
                .stream()
                .map(SolicitudResponse::new)
                .toList();
    }

    // CU-12: solicitudes pendientes de dieta
    @GetMapping("/dieta/pendientes")
    public ResponseEntity<List<SolicitudResponse>>
            obtenerSolicitudesPendientesDieta() {

        List<SolicitudResponse> respuesta =
                solicitudService
                        .obtenerSolicitudesPendientesDieta()
                        .stream()
                        .map(SolicitudResponse::new)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    // Solicitudes realizadas por un deportista
    @GetMapping("/deportista/{deportistaId}")
    public ResponseEntity<List<SolicitudResponse>>
            obtenerSolicitudesPorDeportista(
                    @PathVariable Long deportistaId) {

        List<SolicitudResponse> respuesta =
                solicitudService
                        .obtenerSolicitudesPorDeportista(
                                deportistaId)
                        .stream()
                        .map(SolicitudResponse::new)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    // CU-12: responder una solicitud de modificación
    @PutMapping("/{id}/responder")
    public ResponseEntity<SolicitudResponse>
            responderSolicitud(
                    @PathVariable Long id,
                    @RequestBody Map<String, String> payload) {

        String estado = payload.get("estado");

        if (estado == null || estado.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SolicitudModificacion respondida =
                solicitudService.responderSolicitud(
                        id,
                        estado);

        return ResponseEntity.ok(
                new SolicitudResponse(respondida));
    }

    // Crear solicitud de modificación de dieta
    @PostMapping("/dieta")
    public ResponseEntity<SolicitudResponse>
            crearSolicitudDieta(
                    @RequestBody SolicitudModificacion solicitud) {

        if (solicitud.getMotivo() == null
                || solicitud.getMotivo().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }

        SolicitudModificacion nueva =
                solicitudService
                        .crearSolicitudDieta(solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SolicitudResponse(nueva));
    }
}