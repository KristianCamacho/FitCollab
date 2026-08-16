package cl.usach.fitcollab.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.dto.FichaPersonalRequest;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.FichaPersonal;
import cl.usach.fitcollab.services.DeportistaService;

@RestController
@RequestMapping("/api/deportistas")
public class DeportistaController {

    private final DeportistaService deportistaService;

    public DeportistaController(DeportistaService deportistaService) {
        this.deportistaService = deportistaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDeportista(@PathVariable Long id) {

        Optional<Deportista> deportista =
                deportistaService.obtenerPorId(id);

        if (deportista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(deportista.get());
    }
    @GetMapping("/{id}/ficha-personal")
    public ResponseEntity<?> obtenerFichaPersonal(@PathVariable Long id) {

        Optional<FichaPersonal> ficha =
                deportistaService.obtenerFichaPersonal(id);

        if (ficha.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ficha.get());
    }

    @PutMapping("/{id}/ficha-personal")
    public ResponseEntity<?> completarFichaPersonal(
            @PathVariable Long id,
            @RequestBody FichaPersonalRequest request) {

        Optional<FichaPersonal> ficha =
                deportistaService.completarFichaPersonal(id, request);

        if (ficha.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ficha.get());
    }
}
