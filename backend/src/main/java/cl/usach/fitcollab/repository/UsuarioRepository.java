package cl.usach.fitcollab.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}