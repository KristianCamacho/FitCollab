package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.repository.SolicitudCambioAsignacionRepository;

@Service
public class SolicitudCambioAsignacionService {

    private final SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository;

    public SolicitudCambioAsignacionService(
            SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository) {
        this.solicitudCambioAsignacionRepository = solicitudCambioAsignacionRepository;
    }

    public List<SolicitudCambioAsignacion> obtenerTodas() {
        return solicitudCambioAsignacionRepository.findAll();
    }

    public Optional<SolicitudCambioAsignacion> obtenerPorId(Long id) {
        return solicitudCambioAsignacionRepository.findById(id);
    }

    public SolicitudCambioAsignacion guardar(SolicitudCambioAsignacion solicitud) {
        return solicitudCambioAsignacionRepository.save(solicitud);
    }
}
