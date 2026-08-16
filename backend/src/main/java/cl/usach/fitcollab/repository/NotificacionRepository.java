package cl.usach.fitcollab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.usach.fitcollab.entities.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
