package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.enums.TipoSolicitud;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.repository.NotificacionRepository;
import cl.usach.fitcollab.repository.SolicitudModificacionRepository;

@Service
public class SolicitudModificacionService {

    private final SolicitudModificacionRepository solicitudModificacionRepository;
    private final NotificacionRepository notificacionRepository;

    public SolicitudModificacionService(
            SolicitudModificacionRepository solicitudModificacionRepository,
            NotificacionRepository notificacionRepository) {
        this.solicitudModificacionRepository = solicitudModificacionRepository;
        this.notificacionRepository = notificacionRepository;
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

    public List<SolicitudModificacion> obtenerSolicitudesPendientesDieta(){
        return solicitudModificacionRepository.findByTipoAndEstado(
                "MODIFICACION_DIETA", EstadoSolicitud.PENDIENTE);
    }

    public List<SolicitudModificacion> obtenerSolicitudesPorDeportista(Long deportistaId){
        return solicitudModificacionRepository.findByDeportistaId(deportistaId);
    }

    public SolicitudModificacion responderSolicitud(Long id, String estado) {
        SolicitudModificacion solicitud = solicitudModificacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));

        solicitud.setEstado(EstadoSolicitud.valueOf(estado));
        SolicitudModificacion guardada = solicitudModificacionRepository.save(solicitud);

        //notificar al deportista
        String mensaje = estado.equals("ACEPTADA")
                ? "Tu solicitud de modificacion de dieta fue aprobada."
                : "Tu solicitud de modificacion de dieta fue rechazada.";

        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje(mensaje);
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setDestinatario(solicitud.getDeportista());
        notificacionRepository.save(notificacion);

        return guardada;
    }

    public SolicitudModificacion crearSolicitudDieta(SolicitudModificacion solicitud) {
        //ya existe solicitud pendiente
        boolean existePendiente = solicitudModificacionRepository
                .existsByDeportistaIdAndTipoAndEstado(
                        solicitud.getDeportista().getId(),
                        "MODIFICACION_DIETA",
                        EstadoSolicitud.PENDIENTE);

        if (existePendiente) {
            throw new IllegalStateException("Ya existe una solicitud de modificacion de dieta pendiente.");
        }

        solicitud.setTipo(TipoSolicitud.MODIFICACION_DIETA);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaHora(LocalDateTime.now());

        return solicitudModificacionRepository.save(solicitud);
    }

}
