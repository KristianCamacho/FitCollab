package cl.usach.fitcollab.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.services.SolicitudCambioAsignacionService;

@RestController
@RequestMapping("/api/solicitudes-cambio")
@CrossOrigin(origins = "*")
public class SolicitudCambioAsignacionController {

    private final SolicitudCambioAsignacionService solicitudService;

    public SolicitudCambioAsignacionController(SolicitudCambioAsignacionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<?> solicitarCambio(@RequestBody Map<String, String> payload) {
        try {
            Long deportistaId = Long.parseLong(payload.get("deportistaId"));
            String tipoEspecialista = payload.get("tipoEspecialista");
            String motivo = payload.get("motivo");

            SolicitudCambioAsignacion nuevaSolicitud = solicitudService.crearSolicitudCambio(deportistaId, tipoEspecialista, motivo);

            return ResponseEntity.ok(nuevaSolicitud);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // ERROR 400 PARA SOLICITUD YA EXISTENTE O FORMULARIO VACIO
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // ERROR 500 PARA OTROS
            return ResponseEntity.internalServerError().body("Ocurrió un error al procesar la solicitud.");
        }
    }
}