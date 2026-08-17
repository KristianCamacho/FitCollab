package cl.usach.fitcollab.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.dto.DeportistaResumenResponse;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.services.EntrenadorService;

@RestController
@RequestMapping("/api/entrenadores")
@CrossOrigin(origins = "http://localhost:5173")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    public EntrenadorController(EntrenadorService entrenadorService) {
        this.entrenadorService = entrenadorService;
    }

    // CU-04, paso 2: "Mis deportistas"
    @GetMapping("/{id}/deportistas")
    public ResponseEntity<?> obtenerDeportistas(@PathVariable Long id) {

        Optional<List<Deportista>> deportistas = entrenadorService.obtenerDeportistas(id);

        if (deportistas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DeportistaResumenResponse> respuesta = deportistas.get().stream()
                .map(DeportistaResumenResponse::new)
                .toList();

        return ResponseEntity.ok(respuesta);
    }
}
