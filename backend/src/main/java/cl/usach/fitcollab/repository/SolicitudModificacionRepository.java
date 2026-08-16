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

    // CU-10: verifica si ya existe una solicitud pendiente para esa rutina
    boolean existsByRutinaIdAndTipoAndEstado(
            Long rutinaId, TipoSolicitud tipo, EstadoSolicitud estado);
}