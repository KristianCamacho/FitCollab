package cl.usach.fitcollab.controller;

import cl.usach.fitcollab.dto.SolicitudModificacionRequest;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.services.SolicitudModificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudModificacionController {

    @Autowired
    private SolicitudModificacionService solicitudService;

    @GetMapping
    public ResponseEntity<List<SolicitudModificacion>> obtenerTodas() {
        return ResponseEntity.ok(solicitudService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<SolicitudModificacion> crear(@RequestBody SolicitudModificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crear(request));
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<SolicitudModificacion> responder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.getOrDefault("estado", "ACEPTADA");
        return ResponseEntity.ok(solicitudService.responder(id, estado));
    }
}