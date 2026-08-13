package cl.usach.fitcollab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Entrenador;

public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
}