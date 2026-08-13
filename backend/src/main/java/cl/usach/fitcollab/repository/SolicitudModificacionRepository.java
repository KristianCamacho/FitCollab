package cl.usach.fitcollab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;

public interface SolicitudModificacionRepository
        extends JpaRepository<SolicitudModificacion, Long> {

    List<SolicitudModificacion> findByDeportistaId(Long deportistaId);

    List<SolicitudModificacion> findByEspecialistaId(Long especialistaId);

    List<SolicitudModificacion> findByEstado(EstadoSolicitud estado);
}