package cl.usach.fitcollab.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.dto.CalificarRutinaRequest;
import cl.usach.fitcollab.dto.RutinaResponse;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.services.RutinaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rutinas")
@CrossOrigin(origins = "http://localhost:5173")
public class RutinaController {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    @GetMapping
    public List<RutinaResponse> listar() {
        return rutinaService.obtenerTodas()
                .stream()
                .map(RutinaResponse::new)
                .toList();
    }

    @PostMapping("/{id}/calificacion")
    public ResponseEntity<RutinaResponse> calificar(
            @PathVariable Long id,
            @Valid @RequestBody CalificarRutinaRequest request) {

        Rutina rutinaCalificada = rutinaService.calificarRutina(id, request.getCalificacion());
        return ResponseEntity.ok(new RutinaResponse(rutinaCalificada));
    }
}