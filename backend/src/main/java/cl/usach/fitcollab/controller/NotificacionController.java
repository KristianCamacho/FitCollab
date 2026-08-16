package cl.usach.fitcollab.controller;

import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.repository.NotificacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerMisNotificaciones(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionRepository.findByDestinatarioId(usuarioId));
    }
}
