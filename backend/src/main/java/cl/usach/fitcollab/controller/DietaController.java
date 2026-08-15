package cl.usach.fitcollab.controller;

import java.util.List;

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

import cl.usach.fitcollab.entities.Dieta;
import cl.usach.fitcollab.services.DietaService;

@RestController
@RequestMapping("/api/dietas")
@CrossOrigin(origins = "*")

public class DietaController {
    private final DietaService dietaService;
    public DietaController(DietaService dietaService){
    this.dietaService = dietaService;
}

//creo el plan alimenticio
@PostMapping
public ResponseEntity<?> crearDieta(@RequestBody Dieta dieta){
    try {
        if (dieta.getComidas() == null || dieta.getComidas().trim().isEmpty()){
            return ResponseEntity.badRequest().body("El campo comidas no puede estar vacio.");
        }
        if (dieta.getPorciones() == null || dieta.getPorciones().trim().isEmpty()){
            return ResponseEntity.badRequest().body("El campo porciones no puede estar vacio.");
        }
        if (dieta.getHorarios() == null || dieta.getHorarios().trim().isEmpty()){
            return ResponseEntity.badRequest().body("El campo horarios no puede estar vacio.");
        }
        Dieta nueva = dietaService.crearDieta(dieta);
        return ResponseEntity.ok(nueva);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error al crear el plan alimenticio.");
    }
}

//ver los planes del nutricionista

@GetMapping("/nutricionista/{nutricionistaId}")
public ResponseEntity<?> obtenerDietasPorNutricionista(@PathVariable Long nutricionistaId){
    try {
        List<Dieta> dietas = dietaService.obtenerDietaPorNutricionista(nutricionistaId);
        return ResponseEntity.ok(dietas);
    } catch (Exception e){
        return ResponseEntity.internalServerError().body("Error al obtener los planes.");
    }
}

//ver los planes del deportista
@GetMapping("/deportista/{deportistaId}")
public ResponseEntity<?> obtenerDietasPorDeportista(@PathVariable Long deportistaId){
    try {
        List<Dieta> dietas = dietaService.obtenerDietaPorDeportista(deportistaId);
        return ResponseEntity.ok(dietas);
    } catch (Exception e){
        return ResponseEntity.internalServerError().body("Error al obtener los planes.");
    }
}

//editar plan alimenticio
@PutMapping("/{id}")
public ResponseEntity<?> editarDieta(@PathVariable Long id, @RequestBody Dieta dieta){
    try {
        if (dieta.getComidas() == null || dieta.getComidas().trim().isEmpty()){
            return ResponseEntity.badRequest().body("El campo comidas no puede estar vacio.");
        }
        Long deportistaId = dieta.getDeportista().getId();
        if (deportistaId == null){
            return ResponseEntity.badRequest().body("Se debe indicar el deportista.");
        }
        Dieta actualizada = dietaService.editarDieta(id, dieta, deportistaId);
        return ResponseEntity.ok(actualizada);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e){
        return ResponseEntity.internalServerError().body("Error al editar el plan alimenticio.");
    }
}

//elimininar el plan alimenticio
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminarDieta(@PathVariable Long id){
    try {
        dietaService.eliminarDieta(id);
        return ResponseEntity.ok("Plan alimenticio eliminado correctamente.");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error al eliminar el plan alimenticio.");
    }
}
}