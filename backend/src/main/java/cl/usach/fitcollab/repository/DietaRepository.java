package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Dieta;

public interface DietaRepository extends JpaRepository<Dieta, Long> {

    List<Dieta> findByDeportistaId(Long deportistaId);

    List<Dieta> findByCreadorId(Long nutricionistaId);
}