package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.enums.TipoSolicitud;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.NotificacionRepository;
import cl.usach.fitcollab.repository.RutinaRepository;
import cl.usach.fitcollab.repository.SolicitudModificacionRepository;

@Service
public class SolicitudModificacionService {

    private final SolicitudModificacionRepository solicitudModificacionRepository;
    private final RutinaRepository rutinaRepository;
    private final DeportistaRepository deportistaRepository;
    private final NotificacionRepository notificacionRepository;

    @Value("${app.tiempo-minimo-min}")
    private int tiempoMinimo;

    public SolicitudModificacionService(
            SolicitudModificacionRepository solicitudModificacionRepository,
            RutinaRepository rutinaRepository,
            DeportistaRepository deportistaRepository,
            NotificacionRepository notificacionRepository) {

        this.solicitudModificacionRepository = solicitudModificacionRepository;
        this.rutinaRepository = rutinaRepository;
        this.deportistaRepository = deportistaRepository;
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

    public List<SolicitudModificacion> obtenerPendientes() {
        return solicitudModificacionRepository
                .findByEstado(EstadoSolicitud.PENDIENTE);
    }

    /*
     * CU-10
     * Solicitar adaptación de rutina según tiempo disponible
     */
    public SolicitudModificacion solicitarAdaptacionTiempo(
            Long deportistaId,
            Long rutinaId,
            Integer tiempoDisponibleMin,
            String motivo) {

        Deportista deportista = deportistaRepository
                .findById(deportistaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El deportista indicado no existe"));

        Rutina rutina = rutinaRepository
                .findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La rutina indicada no existe"));

        /*
         * Validación adicional:
         * la rutina solicitada debe pertenecer al deportista.
         *
         * Si vuestra entidad Rutina no tiene getDeportista(),
         * esta validación habrá que adaptarla/eliminar.
         */
        if (rutina.getDeportista() != null
                && !rutina.getDeportista().getId().equals(deportistaId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La rutina indicada no pertenece al deportista");
        }

        if (tiempoDisponibleMin < tiempoMinimo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tiempo disponible debe ser de al menos "
                            + tiempoMinimo
                            + " minutos");
        }

        if (tiempoDisponibleMin >= rutina.getDuracionMinutos()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El tiempo disponible ya permite realizar la rutina completa ("
                            + rutina.getDuracionMinutos()
                            + " minutos)");
        }

        boolean yaExiste = solicitudModificacionRepository
                .existsByRutinaIdAndTipoAndEstado(
                        rutinaId,
                        TipoSolicitud.ADAPTACION_RUTINA_TIEMPO,
                        EstadoSolicitud.PENDIENTE);

        if (yaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una solicitud pendiente de adaptación para esta rutina");
        }

        SolicitudModificacion solicitud =
                new SolicitudModificacion();

        solicitud.setDeportista(deportista);
        solicitud.setRutina(rutina);

        solicitud.setTipo(
                TipoSolicitud.ADAPTACION_RUTINA_TIEMPO);

        solicitud.setEstado(
                EstadoSolicitud.PENDIENTE);

        solicitud.setTiempoDisponibleMin(
                tiempoDisponibleMin);

        solicitud.setMotivo(motivo);

        solicitud.setFechaHora(
                LocalDateTime.now());

        return solicitudModificacionRepository
                .save(solicitud);
    }

    /*
     * CU-12 / solicitudes relacionadas con dieta
     */
    public List<SolicitudModificacion>
            obtenerSolicitudesPendientesDieta() {

        return solicitudModificacionRepository
                .findByTipoAndEstado(
                        TipoSolicitud.MODIFICACION_DIETA,
                        EstadoSolicitud.PENDIENTE);
    }

    public List<SolicitudModificacion>
            obtenerSolicitudesPorDeportista(
                    Long deportistaId) {

        return solicitudModificacionRepository
                .findByDeportistaId(deportistaId);
    }

    /*
     * CU-12
     * Responder solicitud de modificación
     */
    public SolicitudModificacion responderSolicitud(
            Long id,
            String estado) {

        SolicitudModificacion solicitud =
                solicitudModificacionRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Solicitud no encontrada con id: " + id));

        if (solicitud.getEstado()
                != EstadoSolicitud.PENDIENTE) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue procesada");
        }

        EstadoSolicitud nuevoEstado;

        try {
            nuevoEstado = EstadoSolicitud.valueOf(
                    estado.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de solicitud no válido");
        }

        if (nuevoEstado != EstadoSolicitud.ACEPTADA
                && nuevoEstado != EstadoSolicitud.RECHAZADA) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud solo puede ser ACEPTADA o RECHAZADA");
        }

        solicitud.setEstado(nuevoEstado);

        SolicitudModificacion guardada =
                solicitudModificacionRepository
                        .save(solicitud);

        String mensaje =
                nuevoEstado == EstadoSolicitud.ACEPTADA
                        ? "Tu solicitud de modificación fue aprobada."
                        : "Tu solicitud de modificación fue rechazada.";

        enviarNotificacion(
                solicitud.getDeportista(),
                mensaje);

        return guardada;
    }

    /*
     * Crear solicitud de modificación de dieta
     */
    public SolicitudModificacion crearSolicitudDieta(
            SolicitudModificacion solicitud) {

        if (solicitud.getDeportista() == null
                || solicitud.getDeportista().getId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el deportista");
        }

        if (solicitud.getMotivo() == null
                || solicitud.getMotivo().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe completar el motivo de la solicitud");
        }

        Deportista deportista =
                deportistaRepository
                        .findById(
                                solicitud
                                        .getDeportista()
                                        .getId())
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Deportista no encontrado"));

        boolean existePendiente =
                solicitudModificacionRepository
                        .existsByDeportistaIdAndTipoAndEstado(
                                deportista.getId(),
                                TipoSolicitud.MODIFICACION_DIETA,
                                EstadoSolicitud.PENDIENTE);

        if (existePendiente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una solicitud de modificación de dieta pendiente");
        }

        solicitud.setDeportista(deportista);

        solicitud.setTipo(
                TipoSolicitud.MODIFICACION_DIETA);

        solicitud.setEstado(
                EstadoSolicitud.PENDIENTE);

        solicitud.setMotivo(
                solicitud.getMotivo().trim());

        solicitud.setFechaHora(
                LocalDateTime.now());

        return solicitudModificacionRepository
                .save(solicitud);
    }

    private void enviarNotificacion(
            Deportista destinatario,
            String mensaje) {

        Notificacion notificacion =
                new Notificacion();

        notificacion.setMensaje(mensaje);

        notificacion.setFechaHora(
                LocalDateTime.now());

        notificacion.setDestinatario(
                destinatario);

        notificacionRepository.save(
                notificacion);
    }
}