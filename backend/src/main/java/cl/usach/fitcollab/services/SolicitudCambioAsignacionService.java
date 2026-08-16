package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.*;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.repository.*;

@Service
public class SolicitudCambioAsignacionService {

    private final SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository;
    private final DeportistaRepository deportistaRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final NutricionistaRepository nutricionistaRepository;
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudCambioAsignacionService(
            SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository,
            DeportistaRepository deportistaRepository,
            EntrenadorRepository entrenadorRepository,
            NutricionistaRepository nutricionistaRepository,
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudCambioAsignacionRepository = solicitudCambioAsignacionRepository;
        this.deportistaRepository = deportistaRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.nutricionistaRepository = nutricionistaRepository;
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
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


    //ESTO PARA EL CU-13
    public SolicitudCambioAsignacion crearSolicitudCambio(Long deportistaId, String tipoEspecialista, String motivo) {

        // PARA LA EXCEPCION 2 DEL CU; FORMULARIO VACIO
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Se debe completar el motivo de cambio.");
        }

        // PARA LA EXCEPCION 1 DEL CU; SOLICITUD PENDIENTE DE UN ESPECIALISTA DETERMINADO
        if (solicitudCambioAsignacionRepository.existsByDeportistaIdAndTipoEspecialistaAndEstado(
                deportistaId, tipoEspecialista, EstadoSolicitud.PENDIENTE)) {
            throw new IllegalStateException("Ya existe una solicitud de cambio pendiente para este especialista.");
        }

        Deportista deportista = deportistaRepository.findById(deportistaId)
                .orElseThrow(() -> new RuntimeException("Deportista no encontrado"));

        // PARA CREAR LA SOLICITUD
        SolicitudCambioAsignacion nuevaSolicitud = new SolicitudCambioAsignacion();
        nuevaSolicitud.setDeportista(deportista);
        nuevaSolicitud.setTipoEspecialista(tipoEspecialista);
        nuevaSolicitud.setMotivo(motivo);
        nuevaSolicitud.setEstado(EstadoSolicitud.PENDIENTE);
        nuevaSolicitud.setFechaHora(LocalDateTime.now()); //SE REGISTRA LA FECHA EN QUE SE HIZO

        // SE GUARDA EN LA BD
        SolicitudCambioAsignacion guardada = solicitudCambioAsignacionRepository.save(nuevaSolicitud);

        //NOTIFICACION AL ADMIN
        usuarioRepository.findAll().stream()
                .filter(u -> u instanceof Administrador)
                .findFirst()
                .ifPresent(admin -> enviarNotificacion(admin, "Nueva solicitud de cambio de " + tipoEspecialista.toLowerCase() + " del deportista " + deportista.getNombre() + "."));

        return guardada;
    }

    //AQUI EMPIEZA EL CU-15

    public List<SolicitudCambioAsignacion> obtenerSolicitudesPendientes() {
        return solicitudCambioAsignacionRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    public SolicitudCambioAsignacion responderSolicitudCambio(Long solicitudId, boolean aceptada, Long nuevoEspecialistaId, String justificacionRechazo) {

        SolicitudCambioAsignacion solicitud = solicitudCambioAsignacionRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Deportista deportista = solicitud.getDeportista();
        Usuario especialistaAntiguo = solicitud.getTipoEspecialista().equalsIgnoreCase("ENTRENADOR") ? deportista.getEntrenador() : deportista.getNutricionista();

        if (aceptada) {
            solicitud.setEstado(EstadoSolicitud.ACEPTADA);
            Usuario nuevoEspecialista = null;

           //ACA SE CAMBIA AL ESPECIALISTA
            if (solicitud.getTipoEspecialista().equalsIgnoreCase("ENTRENADOR")) {
                Entrenador nuevoEntrenador = entrenadorRepository.findById(nuevoEspecialistaId).orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
                deportista.setEntrenador(nuevoEntrenador);
                nuevoEspecialista = nuevoEntrenador;
            } else {
                Nutricionista nuevoNutricionista = nutricionistaRepository.findById(nuevoEspecialistaId).orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));
                deportista.setNutricionista(nuevoNutricionista);
                nuevoEspecialista = nuevoNutricionista;
            }
            deportistaRepository.save(deportista);

            enviarNotificacion(deportista, "Tu solicitud fue aceptada. Tu nuevo " + solicitud.getTipoEspecialista().toLowerCase() + " es " + nuevoEspecialista.getNombre() + ".");
            enviarNotificacion(nuevoEspecialista, "Tienes un nuevo deportista asignado: " + deportista.getNombre() + ".");
            if (especialistaAntiguo != null) {
                enviarNotificacion(especialistaAntiguo, "El deportista " + deportista.getNombre() + " fue reasignado.");
            }

        } else {
            if (justificacionRechazo == null || justificacionRechazo.trim().isEmpty()) {
                throw new IllegalArgumentException("Se debe ingresar una justificación para el rechazo.");
            }

            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitud.setJustificacionRechazo(justificacionRechazo);

            enviarNotificacion(deportista, "Tu solicitud de cambio fue rechazada. Motivo: " + justificacionRechazo);
        }

        return solicitudCambioAsignacionRepository.save(solicitud);
    }

    private void enviarNotificacion(Usuario destinatario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje(mensaje);
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setDestinatario(destinatario);
        notificacionRepository.save(notificacion);
    }
    
    public List<Entrenador> obtenerEntrenadoresDisponibles() { return entrenadorRepository.findAll(); }
    public List<Nutricionista> obtenerNutricionistasDisponibles() { return nutricionistaRepository.findAll(); }
}

