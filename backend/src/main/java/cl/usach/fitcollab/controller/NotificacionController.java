package cl.usach.fitcollab.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.repository.NotificacionRepository;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerMisNotificaciones(
            @PathVariable Long usuarioId) {

        List<Map<String, Object>> respuesta = notificacionRepository
                .findByDestinatarioIdOrderByFechaHoraDesc(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    private Map<String, Object> toResponse(Notificacion notificacion) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("id", notificacion.getId());
        respuesta.put("mensaje", notificacion.getMensaje());
        respuesta.put("fechaHora", notificacion.getFechaHora());

        return respuesta;
    }
}