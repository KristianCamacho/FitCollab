package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.SolicitudCambioAsignacion;
import cl.usach.fitcollab.enums.EstadoSolicitud;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.SolicitudCambioAsignacionRepository;

@Service
public class SolicitudCambioAsignacionService {

    private final SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository;

    private final DeportistaRepository deportistaRepository;

    public SolicitudCambioAsignacionService(
            SolicitudCambioAsignacionRepository solicitudCambioAsignacionRepository,
            DeportistaRepository deportistaRepository) {
        this.solicitudCambioAsignacionRepository = solicitudCambioAsignacionRepository;
        this.deportistaRepository = deportistaRepository;
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

        /// /////////////////////////////
        //FALTA LA NOTIFICACION AL ADMIN
        /// /////////////////////////////

        return guardada;
    }
}
