package cl.usach.fitcollab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Deportista;

public interface DeportistaRepository extends JpaRepository<Deportista, Long> {
}