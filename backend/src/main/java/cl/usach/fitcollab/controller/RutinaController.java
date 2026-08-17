package cl.usach.fitcollab.controller;

import java.util.List;

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

import cl.usach.fitcollab.dto.CrearRutinaRequest;
import cl.usach.fitcollab.dto.ProponerRutinaRequest;
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

    @PostMapping("/entrenador/{entrenadorId}")
    public ResponseEntity<List<RutinaResponse>> crear(
            @PathVariable Long entrenadorId,
            @Valid @RequestBody CrearRutinaRequest request) {

        List<Rutina> rutinas = rutinaService.crearRutina(entrenadorId, request);

        List<RutinaResponse> respuesta = rutinas.stream()
                .map(RutinaResponse::new)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/entrenador/{entrenadorId}")
    public List<RutinaResponse> listarPorEntrenador(@PathVariable Long entrenadorId) {
        return rutinaService.obtenerPorEntrenador(entrenadorId)
                .stream()
                .map(RutinaResponse::new)
                .toList();
    }

    @GetMapping("/entrenador/{entrenadorId}/propuestas")
    public List<RutinaResponse> listarPropuestas(@PathVariable Long entrenadorId) {
        return rutinaService.obtenerPropuestasPendientes(entrenadorId)
                .stream()
                .map(RutinaResponse::new)
                .toList();
    }

    @GetMapping("/deportista/{deportistaId}")
    public List<RutinaResponse> listarPorDeportista(@PathVariable Long deportistaId) {
        return rutinaService.obtenerPorDeportista(deportistaId)
                .stream()
                .map(RutinaResponse::new)
                .toList();
    }

    @PostMapping("/deportista/{deportistaId}/proponer")
    public ResponseEntity<RutinaResponse> proponer(
            @PathVariable Long deportistaId,
            @Valid @RequestBody ProponerRutinaRequest request) {

        Rutina rutina = rutinaService.proponerRutina(deportistaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RutinaResponse(rutina));
    }

    @PutMapping("/{rutinaId}/aceptar")
    public ResponseEntity<RutinaResponse> aceptar(@PathVariable Long rutinaId) {
        Rutina rutina = rutinaService.responderPropuesta(rutinaId, true);
        return ResponseEntity.ok(new RutinaResponse(rutina));
    }

    @PutMapping("/{rutinaId}/rechazar")
    public ResponseEntity<RutinaResponse> rechazar(@PathVariable Long rutinaId) {
        Rutina rutina = rutinaService.responderPropuesta(rutinaId, false);
        return ResponseEntity.ok(new RutinaResponse(rutina));
    }
}