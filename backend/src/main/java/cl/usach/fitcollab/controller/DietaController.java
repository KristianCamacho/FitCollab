package cl.usach.fitcollab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.usach.fitcollab.dto.DietaResponse;
import cl.usach.fitcollab.entities.Dieta;
import cl.usach.fitcollab.services.DietaService;

@RestController
@RequestMapping("/api/dietas")
@CrossOrigin(origins = "http://localhost:5173")
public class DietaController {

    private final DietaService dietaService;

    public DietaController(
            DietaService dietaService) {

        this.dietaService = dietaService;
    }

    /*
     * Obtener deportistas asignados
     * a un nutricionista.
     */
    @GetMapping("/nutricionista/{nutricionistaId}/deportistas")
    public ResponseEntity<List<Map<String, Object>>>
            obtenerDeportistasNutricionista(
                    @PathVariable Long nutricionistaId) {

        List<Map<String, Object>> respuesta =
                dietaService
                        .obtenerDeportistasPorNutricionista(
                                nutricionistaId)
                        .stream()
                        .map(deportista -> Map.<String, Object>of(
                                "id", deportista.getId(),
                                "nombre", deportista.getNombre(),
                                "apellido", deportista.getApellido()
                        ))
                        .toList();

        return ResponseEntity.ok(respuesta);
    }

    /*
     * CU-05: crear plan alimenticio.
     */
    @PostMapping
    public ResponseEntity<?> crearDieta(
            @RequestBody Map<String, Object> payload) {

        String comidas =
                (String) payload.get("comidas");

        String porciones =
                (String) payload.get("porciones");

        String horarios =
                (String) payload.get("horarios");

        String sugerencia =
                (String) payload.get("sugerenciaAlimenticia");

        Number creadorId =
                (Number) payload.get("creadorId");

        Number deportistaId =
                (Number) payload.get("deportistaId");

        if (comidas == null
                || comidas.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo comidas no puede estar vacío.");
        }

        if (porciones == null
                || porciones.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo porciones no puede estar vacío.");
        }

        if (horarios == null
                || horarios.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo horarios no puede estar vacío.");
        }

        if (creadorId == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Debe indicar el nutricionista.");
        }

        if (deportistaId == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Debe seleccionar un deportista.");
        }

        Dieta dieta = new Dieta();

        dieta.setComidas(
                comidas.trim());

        dieta.setPorciones(
                porciones.trim());

        dieta.setHorarios(
                horarios.trim());

        dieta.setSugerenciaAlimenticia(
                sugerencia != null
                        ? sugerencia.trim()
                        : null);

        Dieta nueva =
                dietaService.crearDieta(
                        dieta,
                        creadorId.longValue(),
                        deportistaId.longValue());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DietaResponse(nueva));
    }

        @GetMapping("/nutricionista/{nutricionistaId}")
        public ResponseEntity<List<DietaResponse>>
                obtenerDietasPorNutricionista(
                        @PathVariable Long nutricionistaId) {

        List<DietaResponse> respuesta =
                dietaService
                        .obtenerDietaPorNutricionista(nutricionistaId)
                        .stream()
                        .map(DietaResponse::new)
                        .toList();

        return ResponseEntity.ok(respuesta);
        }

        @GetMapping("/deportista/{deportistaId}")
        public ResponseEntity<List<DietaResponse>>
                obtenerDietasPorDeportista(
                        @PathVariable Long deportistaId) {

        List<DietaResponse> respuesta =
                dietaService
                        .obtenerDietaPorDeportista(deportistaId)
                        .stream()
                        .map(DietaResponse::new)
                        .toList();

        return ResponseEntity.ok(respuesta);
        }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarDieta(
            @PathVariable Long id,
            @RequestBody Dieta dieta) {

        if (dieta.getComidas() == null
                || dieta.getComidas().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo comidas no puede estar vacío.");
        }

        if (dieta.getPorciones() == null
                || dieta.getPorciones().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo porciones no puede estar vacío.");
        }

        if (dieta.getHorarios() == null
                || dieta.getHorarios().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("El campo horarios no puede estar vacío.");
        }

        Dieta actualizada =
                dietaService.editarDieta(
                        id,
                        dieta);

        return ResponseEntity.ok(
                new DietaResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
            eliminarDieta(
                    @PathVariable Long id) {

        dietaService.eliminarDieta(id);

        return ResponseEntity.ok(
                "Plan alimenticio eliminado correctamente.");
    }
}
