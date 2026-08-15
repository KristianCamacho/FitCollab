package cl.usach.fitcollab.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.Usuario;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{
    List<Notificacion> findByDestinatario(Usuario destinatario);
    List<Notificacion> findByDestinatarioId(Long destinatarioId);
}
