package cl.usach.fitcollab.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.usach.fitcollab.entities.*;
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

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudCambioAsignacion>> listarPendientes() {
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesPendientes());
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<List<Entrenador>> listarEntrenadores() {
        return ResponseEntity.ok(solicitudService.obtenerEntrenadoresDisponibles());
    }

    @GetMapping("/nutricionistas")
    public ResponseEntity<List<Nutricionista>> listarNutricionistas() {
        return ResponseEntity.ok(solicitudService.obtenerNutricionistasDisponibles());
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<?> responderSolicitud(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            boolean aceptada = (Boolean) payload.get("aceptada");
            Long nuevoEspecialistaId = payload.containsKey("nuevoEspecialistaId") && payload.get("nuevoEspecialistaId") != null
                    ? Long.parseLong(payload.get("nuevoEspecialistaId").toString()) : null;
            String justificacion = (String) payload.get("justificacionRechazo");

            SolicitudCambioAsignacion procesada = solicitudService.responderSolicitudCambio(id, aceptada, nuevoEspecialistaId, justificacion);
            return ResponseEntity.ok(procesada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ocurrió un error al procesar la respuesta.");
        }
    }
}
