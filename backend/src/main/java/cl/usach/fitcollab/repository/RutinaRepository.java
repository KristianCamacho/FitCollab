package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Rutina;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    List<Rutina> findByDeportistaId(Long deportistaId);

    List<Rutina> findByCreadorId(Long entrenadorId);
}