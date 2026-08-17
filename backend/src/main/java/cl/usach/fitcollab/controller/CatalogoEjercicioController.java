package cl.usach.fitcollab.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.entities.CatalogoEjercicio;
import cl.usach.fitcollab.repository.CatalogoEjercicioRepository;


@RestController
@RequestMapping("/api/catalogo-ejercicios")
@CrossOrigin(origins = "http://localhost:5173")
public class CatalogoEjercicioController {

    private final CatalogoEjercicioRepository catalogoEjercicioRepository;

    public CatalogoEjercicioController(CatalogoEjercicioRepository catalogoEjercicioRepository) {
        this.catalogoEjercicioRepository = catalogoEjercicioRepository;
    }

    @GetMapping
    public List<CatalogoEjercicio> listar() {
        return catalogoEjercicioRepository.findAll();
    }
}