package cl.usach.fitcollab.controller;

import java.util.LinkedHashMap;
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
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.dto.SolicitudCambioAsignacionResponse;
import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.entities.Usuario;
import cl.usach.fitcollab.services.SolicitudCambioAsignacionService;

@RestController
@RequestMapping("/api/solicitudes-cambio")
@CrossOrigin(origins = "http://localhost:5173")
public class SolicitudCambioAsignacionController {

    private final SolicitudCambioAsignacionService solicitudService;

    public SolicitudCambioAsignacionController(
            SolicitudCambioAsignacionService solicitudService) {

        this.solicitudService = solicitudService;
    }

    // CU-13
    @PostMapping
    public ResponseEntity<SolicitudCambioAsignacionResponse> solicitarCambio(
            @RequestBody Map<String, String> payload) {

        Long deportistaId = parseLong(
                payload.get("deportistaId"),
                "El deportista es obligatorio"
        );

        String tipoEspecialista = payload.get("tipoEspecialista");
        String motivo = payload.get("motivo");

        SolicitudCambioAsignacion nuevaSolicitud =
                solicitudService.crearSolicitudCambio(
                        deportistaId,
                        tipoEspecialista,
                        motivo
                );

        SolicitudCambioAsignacionResponse respuesta =
                new SolicitudCambioAsignacionResponse(
                        nuevaSolicitud.getId(),
                        nuevaSolicitud.getDeportista().getId(),
                        nuevaSolicitud.getTipoEspecialista(),
                        nuevaSolicitud.getMotivo(),
                        nuevaSolicitud.getEstado().name(),
                        nuevaSolicitud.getFechaHora()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // CU-15
    @GetMapping("/pendientes")
    public ResponseEntity<List<Map<String, Object>>> listarPendientes() {

        List<Map<String, Object>> respuesta =
                solicitudService.obtenerSolicitudesPendientes()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/entrenadores")
    public ResponseEntity<List<Map<String, Object>>> listarEntrenadores() {

        List<Map<String, Object>> respuesta =
                solicitudService.obtenerEntrenadoresDisponibles()
                        .stream()
                        .map(this::toEspecialistaResponse)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nutricionistas")
    public ResponseEntity<List<Map<String, Object>>> listarNutricionistas() {

        List<Map<String, Object>> respuesta =
                solicitudService.obtenerNutricionistasDisponibles()
                        .stream()
                        .map(this::toEspecialistaResponse)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<Map<String, Object>> responderSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        Object aceptadaRaw = payload.get("aceptada");

        if (!(aceptadaRaw instanceof Boolean)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Se debe indicar si la solicitud fue aceptada o rechazada"
            );
        }

        boolean aceptada = (Boolean) aceptadaRaw;

        Long nuevoEspecialistaId =
                parseLongOpcional(payload.get("nuevoEspecialistaId"));

        String justificacion =
                payload.get("justificacionRechazo") == null
                        ? null
                        : payload.get("justificacionRechazo").toString();

        SolicitudCambioAsignacion procesada =
                solicitudService.responderSolicitudCambio(
                        id,
                        aceptada,
                        nuevoEspecialistaId,
                        justificacion
                );

        return ResponseEntity.ok(toResponse(procesada));
    }

    private Map<String, Object> toResponse(
            SolicitudCambioAsignacion solicitud) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("id", solicitud.getId());

        respuesta.put(
                "deportistaId",
                solicitud.getDeportista().getId()
        );

        respuesta.put(
                "deportistaNombre",
                solicitud.getDeportista().getNombre()
                        + " "
                        + solicitud.getDeportista().getApellido()
        );

        respuesta.put(
                "tipoEspecialista",
                solicitud.getTipoEspecialista()
        );

        respuesta.put("motivo", solicitud.getMotivo());
        respuesta.put("estado", solicitud.getEstado().name());
        respuesta.put("fechaHora", solicitud.getFechaHora());

        respuesta.put(
                "justificacionRechazo",
                solicitud.getJustificacionRechazo()
        );

        return respuesta;
    }

    private Map<String, Object> toEspecialistaResponse(
            Usuario especialista) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("id", especialista.getId());
        respuesta.put("nombre", especialista.getNombre());
        respuesta.put("apellido", especialista.getApellido());

        return respuesta;
    }

    private Long parseLong(String valor, String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    mensaje
            );
        }

        try {
            return Long.parseLong(valor);

        } catch (NumberFormatException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    mensaje
            );
        }
    }

    private Long parseLongOpcional(Object valor) {

        if (valor == null || valor.toString().isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(valor.toString());

        } catch (NumberFormatException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El especialista seleccionado no es válido"
            );
        }
    }
}