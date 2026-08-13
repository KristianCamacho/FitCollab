package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;

public interface SolicitudCambioAsignacionRepository
        extends JpaRepository<SolicitudCambioAsignacion, Long> {

    List<SolicitudCambioAsignacion> findByDeportistaId(Long deportistaId);

    List<SolicitudCambioAsignacion> findByEstado(EstadoSolicitud estado);
}