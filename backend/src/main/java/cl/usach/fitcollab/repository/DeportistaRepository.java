package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Deportista;

public interface DeportistaRepository
        extends JpaRepository<Deportista, Long> {

    List<Deportista> findByEntrenadorId(Long entrenadorId);

    List<Deportista> findByNutricionistaId(Long nutricionistaId);
}