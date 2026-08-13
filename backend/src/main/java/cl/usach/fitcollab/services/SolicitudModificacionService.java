package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.repository.SolicitudModificacionRepository;

@Service
public class SolicitudModificacionService {

    private final SolicitudModificacionRepository solicitudModificacionRepository;

    public SolicitudModificacionService(
            SolicitudModificacionRepository solicitudModificacionRepository) {
        this.solicitudModificacionRepository = solicitudModificacionRepository;
    }

    public List<SolicitudModificacion> obtenerTodas() {
        return solicitudModificacionRepository.findAll();
    }

    public Optional<SolicitudModificacion> obtenerPorId(Long id) {
        return solicitudModificacionRepository.findById(id);
    }

    public SolicitudModificacion guardar(SolicitudModificacion solicitud) {
        return solicitudModificacionRepository.save(solicitud);
    }
}
