package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.entities.SolicitudModificacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.enums.TipoSolicitud;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.RutinaRepository;
import cl.usach.fitcollab.repository.SolicitudModificacionRepository;

@Service
public class SolicitudModificacionService {

    private final SolicitudModificacionRepository solicitudModificacionRepository;
    private final RutinaRepository rutinaRepository;
    private final DeportistaRepository deportistaRepository;

    @Value("${app.tiempo-minimo-min}")
    private int tiempoMinimo;

    public SolicitudModificacionService(
            SolicitudModificacionRepository solicitudModificacionRepository,
            RutinaRepository rutinaRepository,
            DeportistaRepository deportistaRepository) {
        this.solicitudModificacionRepository = solicitudModificacionRepository;
        this.rutinaRepository = rutinaRepository;
        this.deportistaRepository = deportistaRepository;
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
        return solicitudModificacionRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    // CU-10: Solicitar adaptacion de rutina segun tiempo disponible
    public SolicitudModificacion solicitarAdaptacionTiempo(
            Long deportistaId, Long rutinaId, Integer tiempoDisponibleMin, String motivo) {

        // Excepcion 1: el deportista no existe
        Deportista deportista = deportistaRepository.findById(deportistaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El deportista indicado no existe"));

        // Excepcion 2: la rutina no existe
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La rutina indicada no existe"));

        // Excepcion 3: el tiempo declarado es menor al minimo permitido
        if (tiempoDisponibleMin < tiempoMinimo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tiempo disponible debe ser de al menos " + tiempoMinimo + " minutos");
        }

        // Excepcion 4: el tiempo declarado no justifica una adaptacion
        if (tiempoDisponibleMin >= rutina.getDuracionMinutos()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El tiempo disponible ya permite realizar la rutina completa ("
                            + rutina.getDuracionMinutos() + " minutos)");
        }

        // Excepcion 5: ya existe una solicitud pendiente para esta rutina
        boolean yaExiste = solicitudModificacionRepository.existsByRutinaIdAndTipoAndEstado(
                rutinaId, TipoSolicitud.ADAPTACION_RUTINA_TIEMPO, EstadoSolicitud.PENDIENTE);

        if (yaExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una solicitud pendiente de adaptacion para esta rutina");
        }

        SolicitudModificacion solicitud = new SolicitudModificacion();
        solicitud.setDeportista(deportista);
        solicitud.setRutina(rutina);
        solicitud.setTipo(TipoSolicitud.ADAPTACION_RUTINA_TIEMPO);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setTiempoDisponibleMin(tiempoDisponibleMin);
        solicitud.setMotivo(motivo);
        solicitud.setFechaHora(LocalDateTime.now());

        return solicitudModificacionRepository.save(solicitud);
    }
}