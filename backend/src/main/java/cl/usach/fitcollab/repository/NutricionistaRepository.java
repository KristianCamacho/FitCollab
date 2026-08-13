package cl.usach.fitcollab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Nutricionista;

public interface NutricionistaRepository extends JpaRepository<Nutricionista, Long> {
}