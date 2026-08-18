package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.enums.TipoSolicitud;

public interface SolicitudModificacionRepository
        extends JpaRepository<SolicitudModificacion, Long> {

    List<SolicitudModificacion> findByDeportistaId(Long deportistaId);

    List<SolicitudModificacion> findByEspecialistaId(Long especialistaId);

    List<SolicitudModificacion> findByEstado(EstadoSolicitud estado);

    // CU-10: verifica solicitud pendiente para una rutina
    boolean existsByRutinaIdAndTipoAndEstado(
            Long rutinaId,
            TipoSolicitud tipo,
            EstadoSolicitud estado
    );

    // CU-05 / CU-12: verifica solicitudes pendientes
    // de un deportista para un tipo determinado
    boolean existsByDeportistaIdAndTipoAndEstado(
            Long deportistaId,
            TipoSolicitud tipo,
            EstadoSolicitud estado
    );

    // CU-05 / CU-12: obtener solicitudes
    // por tipo y estado
    List<SolicitudModificacion> findByTipoAndEstado(
            TipoSolicitud tipo,
            EstadoSolicitud estado
    );
}