package cl.usach.fitcollab.controller;

import cl.usach.fitcollab.dto.RutinaRequest;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.services.RutinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutinas")
public class RutinaController {

    @Autowired
    private RutinaService rutinaService;

    @GetMapping
    public ResponseEntity<List<Rutina>> obtenerTodas() {
        return ResponseEntity.ok(rutinaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rutina> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rutinaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Rutina> crear(@RequestBody RutinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutinaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rutina> actualizar(@PathVariable Long id, @RequestBody RutinaRequest request) {
        return ResponseEntity.ok(rutinaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}