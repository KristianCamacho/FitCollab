package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.entities.Administrador;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.Nutricionista;
import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.entities.Usuario;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.EntrenadorRepository;
import cl.usach.fitcollab.repository.NotificacionRepository;
import cl.usach.fitcollab.repository.NutricionistaRepository;
import cl.usach.fitcollab.repository.SolicitudCambioAsignacionRepository;
import cl.usach.fitcollab.repository.UsuarioRepository;

@Service
public class SolicitudCambioAsignacionService {

    private static final String ENTRENADOR = "ENTRENADOR";
    private static final String NUTRICIONISTA = "NUTRICIONISTA";

    private final SolicitudCambioAsignacionRepository
            solicitudCambioAsignacionRepository;

    private final DeportistaRepository deportistaRepository;

    private final EntrenadorRepository entrenadorRepository;

    private final NutricionistaRepository nutricionistaRepository;

    private final NotificacionRepository notificacionRepository;

    private final UsuarioRepository usuarioRepository;

    public SolicitudCambioAsignacionService(
            SolicitudCambioAsignacionRepository
                    solicitudCambioAsignacionRepository,
            DeportistaRepository deportistaRepository,
            EntrenadorRepository entrenadorRepository,
            NutricionistaRepository nutricionistaRepository,
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository) {

        this.solicitudCambioAsignacionRepository =
                solicitudCambioAsignacionRepository;

        this.deportistaRepository = deportistaRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.nutricionistaRepository = nutricionistaRepository;
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<SolicitudCambioAsignacion> obtenerTodas() {
        return solicitudCambioAsignacionRepository.findAll();
    }

    public Optional<SolicitudCambioAsignacion> obtenerPorId(
            Long id) {

        return solicitudCambioAsignacionRepository.findById(id);
    }

    public SolicitudCambioAsignacion guardar(
            SolicitudCambioAsignacion solicitud) {

        return solicitudCambioAsignacionRepository.save(solicitud);
    }

    // CU-13
    @Transactional
    public SolicitudCambioAsignacion crearSolicitudCambio(
            Long deportistaId,
            String tipoEspecialista,
            String motivo) {

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Se debe completar el motivo de cambio"
            );
        }

        if (motivo.trim().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El motivo no puede superar los 500 caracteres"
            );
        }

        String tipoNormalizado =
                normalizarTipoEspecialista(tipoEspecialista);

        Deportista deportista =
                deportistaRepository.findById(deportistaId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Deportista no encontrado"
                                )
                        );

        validarEspecialistaAsignado(
                deportista,
                tipoNormalizado
        );

        boolean existePendiente =
                solicitudCambioAsignacionRepository
                        .existsByDeportistaIdAndTipoEspecialistaAndEstado(
                                deportistaId,
                                tipoNormalizado,
                                EstadoSolicitud.PENDIENTE
                        );

        if (existePendiente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una solicitud de cambio pendiente para este especialista"
            );
        }

        SolicitudCambioAsignacion nuevaSolicitud =
                new SolicitudCambioAsignacion();

        nuevaSolicitud.setDeportista(deportista);

        nuevaSolicitud.setTipoEspecialista(
                tipoNormalizado
        );

        nuevaSolicitud.setMotivo(
                motivo.trim()
        );

        nuevaSolicitud.setEstado(
                EstadoSolicitud.PENDIENTE
        );

        nuevaSolicitud.setFechaHora(
                LocalDateTime.now()
        );

        SolicitudCambioAsignacion guardada =
                solicitudCambioAsignacionRepository
                        .save(nuevaSolicitud);

        // Notificar a los administradores
        usuarioRepository.findAll()
                .stream()
                .filter(Administrador.class::isInstance)
                .forEach(
                        admin -> enviarNotificacion(
                                admin,
                                "Nueva solicitud de cambio de "
                                        + tipoNormalizado.toLowerCase()
                                        + " del deportista "
                                        + deportista.getNombre()
                                        + " "
                                        + deportista.getApellido()
                                        + "."
                        )
                );

        return guardada;
    }

    // CU-15
    public List<SolicitudCambioAsignacion>
            obtenerSolicitudesPendientes() {

        return solicitudCambioAsignacionRepository
                .findByEstadoOrderByFechaHoraAsc(
                        EstadoSolicitud.PENDIENTE
                );
    }

    @Transactional
    public SolicitudCambioAsignacion responderSolicitudCambio(
            Long solicitudId,
            boolean aceptada,
            Long nuevoEspecialistaId,
            String justificacionRechazo) {

        SolicitudCambioAsignacion solicitud =
                solicitudCambioAsignacionRepository
                        .findById(solicitudId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Solicitud no encontrada"
                                )
                        );

        if (solicitud.getEstado()
                != EstadoSolicitud.PENDIENTE) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue procesada"
            );
        }

        Deportista deportista =
                solicitud.getDeportista();

        String tipo =
                normalizarTipoEspecialista(
                        solicitud.getTipoEspecialista()
                );

        if (aceptada) {

            if (nuevoEspecialistaId == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debe seleccionar un nuevo especialista"
                );
            }

            Usuario especialistaAntiguo;
            Usuario nuevoEspecialista;

            if (ENTRENADOR.equals(tipo)) {

                especialistaAntiguo =
                        deportista.getEntrenador();

                if (especialistaAntiguo != null
                        && especialistaAntiguo
                                .getId()
                                .equals(nuevoEspecialistaId)) {

                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Debe seleccionar un entrenador diferente al actual"
                    );
                }

                Entrenador nuevoEntrenador =
                        entrenadorRepository
                                .findById(nuevoEspecialistaId)
                                .orElseThrow(
                                        () -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Entrenador no encontrado"
                                        )
                                );

                deportista.setEntrenador(
                        nuevoEntrenador
                );

                nuevoEspecialista =
                        nuevoEntrenador;

            } else {

                especialistaAntiguo =
                        deportista.getNutricionista();

                if (especialistaAntiguo != null
                        && especialistaAntiguo
                                .getId()
                                .equals(nuevoEspecialistaId)) {

                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Debe seleccionar un nutricionista diferente al actual"
                    );
                }

                Nutricionista nuevoNutricionista =
                        nutricionistaRepository
                                .findById(nuevoEspecialistaId)
                                .orElseThrow(
                                        () -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Nutricionista no encontrado"
                                        )
                                );

                deportista.setNutricionista(
                        nuevoNutricionista
                );

                nuevoEspecialista =
                        nuevoNutricionista;
            }

            deportistaRepository.save(
                    deportista
            );

            solicitud.setEstado(
                    EstadoSolicitud.ACEPTADA
            );

            solicitud.setJustificacionRechazo(
                    null
            );

            enviarNotificacion(
                    deportista,
                    "Tu solicitud fue aceptada. Tu nuevo "
                            + tipo.toLowerCase()
                            + " es "
                            + nuevoEspecialista.getNombre()
                            + " "
                            + nuevoEspecialista.getApellido()
                            + "."
            );

            enviarNotificacion(
                    nuevoEspecialista,
                    "Tienes un nuevo deportista asignado: "
                            + deportista.getNombre()
                            + " "
                            + deportista.getApellido()
                            + "."
            );

            if (especialistaAntiguo != null) {

                enviarNotificacion(
                        especialistaAntiguo,
                        "El deportista "
                                + deportista.getNombre()
                                + " "
                                + deportista.getApellido()
                                + " fue reasignado."
                );
            }

        } else {

            if (justificacionRechazo == null
                    || justificacionRechazo
                            .trim()
                            .isEmpty()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Se debe ingresar una justificación para el rechazo"
                );
            }

            if (justificacionRechazo
                    .trim()
                    .length() > 500) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La justificación no puede superar los 500 caracteres"
                );
            }

            solicitud.setEstado(
                    EstadoSolicitud.RECHAZADA
            );

            solicitud.setJustificacionRechazo(
                    justificacionRechazo.trim()
            );

            enviarNotificacion(
                    deportista,
                    "Tu solicitud de cambio fue rechazada. Motivo: "
                            + justificacionRechazo.trim()
            );
        }

        return solicitudCambioAsignacionRepository
                .save(solicitud);
    }

    public List<Entrenador>
            obtenerEntrenadoresDisponibles() {

        return entrenadorRepository.findAll();
    }

    public List<Nutricionista>
            obtenerNutricionistasDisponibles() {

        return nutricionistaRepository.findAll();
    }

    private String normalizarTipoEspecialista(
            String tipoEspecialista) {

        if (tipoEspecialista == null
                || tipoEspecialista.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de especialista es obligatorio"
            );
        }

        String tipo =
                tipoEspecialista
                        .trim()
                        .toUpperCase();

        if (!ENTRENADOR.equals(tipo)
                && !NUTRICIONISTA.equals(tipo)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de especialista debe ser ENTRENADOR o NUTRICIONISTA"
            );
        }

        return tipo;
    }

    private void validarEspecialistaAsignado(
            Deportista deportista,
            String tipoEspecialista) {

        if (ENTRENADOR.equals(tipoEspecialista)
                && deportista.getEntrenador() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El deportista no tiene un entrenador asignado"
            );
        }

        if (NUTRICIONISTA.equals(tipoEspecialista)
                && deportista.getNutricionista() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El deportista no tiene un nutricionista asignado"
            );
        }
    }

    private void enviarNotificacion(
            Usuario destinatario,
            String mensaje) {

        Notificacion notificacion =
                new Notificacion();

        notificacion.setMensaje(mensaje);

        notificacion.setFechaHora(
                LocalDateTime.now()
        );

        notificacion.setDestinatario(
                destinatario
        );

        notificacionRepository.save(
                notificacion
        );
    }
}