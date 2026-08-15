package cl.usach.fitcollab.services;

import cl.usach.fitcollab.dto.SolicitudModificacionRequest;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Especialista;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.enums.TipoSolicitud;
import cl.usach.fitcollab.repository.SolicitudModificacionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudModificacionService {

    @Autowired
    private SolicitudModificacionRepository solicitudRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<SolicitudModificacion> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    public SolicitudModificacion crear(SolicitudModificacionRequest request) {
        SolicitudModificacion solicitud = new SolicitudModificacion();

        if (request.getTipo() != null) {
            try {
                solicitud.setTipo(TipoSolicitud.valueOf(request.getTipo().toUpperCase()));
            } catch (Exception ignored) {}
        }

        solicitud.setMotivo(request.getMotivo());

        if (request.getDeportistaId() != null) {
            solicitud.setDeportista(entityManager.getReference(Deportista.class, request.getDeportistaId()));
        }

        if (request.getEspecialistaId() != null) {
            solicitud.setEspecialista(entityManager.getReference(Especialista.class, request.getEspecialistaId()));
        }

        if (request.getRutinaId() != null) {
            solicitud.setRutina(entityManager.getReference(Rutina.class, request.getRutinaId()));
        }

        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaHora(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    public SolicitudModificacion responder(Long id, String estado) {
        SolicitudModificacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        if (estado != null) {
            try {
                solicitud.setEstado(EstadoSolicitud.valueOf(estado.toUpperCase()));
            } catch (Exception ignored) {}
        }

        return solicitudRepository.save(solicitud);
    }
}