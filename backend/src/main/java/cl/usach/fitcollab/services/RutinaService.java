package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.dto.CrearRutinaRequest;
import cl.usach.fitcollab.dto.EjercicioRequest;
import cl.usach.fitcollab.dto.ProponerRutinaRequest;
import cl.usach.fitcollab.entities.CatalogoEjercicio;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Ejercicio;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.entities.Historial;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.enums.EstadoRutina;
import cl.usach.fitcollab.repository.CatalogoEjercicioRepository;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.EntrenadorRepository;
import cl.usach.fitcollab.repository.NotificacionRepository;
import cl.usach.fitcollab.repository.RutinaRepository;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final DeportistaRepository deportistaRepository;
    private final NotificacionRepository notificacionRepository;
    private final CatalogoEjercicioRepository catalogoEjercicioRepository;

    public RutinaService(
            RutinaRepository rutinaRepository,
            EntrenadorRepository entrenadorRepository,
            DeportistaRepository deportistaRepository,
            NotificacionRepository notificacionRepository,
            CatalogoEjercicioRepository catalogoEjercicioRepository) {
        this.rutinaRepository = rutinaRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.deportistaRepository = deportistaRepository;
        this.notificacionRepository = notificacionRepository;
        this.catalogoEjercicioRepository = catalogoEjercicioRepository;
    }

    public List<Rutina> obtenerTodas() {
        return rutinaRepository.findAll();
    }

    public Optional<Rutina> obtenerPorId(Long id) {
        return rutinaRepository.findById(id);
    }

    public Rutina guardar(Rutina rutina) {
        return rutinaRepository.save(rutina);
    }

    // CU-06: Calificar rutina
    @Transactional
    public Rutina calificarRutina(Long rutinaId, Integer calificacion) {

        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La rutina indicada no existe"));

        if (rutina.getEstado() != EstadoRutina.REALIZADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden calificar rutinas realizadas");
        }

        if (rutina.getCalificacion() != 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta rutina ya fue calificada");
        }

        rutina.setCalificacion(calificacion);

        return rutinaRepository.save(rutina);
    }

    public List<Rutina> obtenerPorDeportista(Long deportistaId) {
        return rutinaRepository.findByDeportistaId(deportistaId);
    }

    public List<Rutina> obtenerPorEntrenador(Long entrenadorId) {
        return rutinaRepository.findByCreadorId(entrenadorId);
    }

    
    public List<Rutina> obtenerPropuestasPendientes(Long entrenadorId) {
        return rutinaRepository.findByCreadorId(entrenadorId).stream()
                .filter(r -> r.getEstado() == EstadoRutina.PENDIENTE_VALIDACION)
                .toList();
    }

   
    @Transactional
    public Rutina responderPropuesta(Long rutinaId, boolean aceptar) {

        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La rutina indicada no existe"));

        if (rutina.getEstado() != EstadoRutina.PENDIENTE_VALIDACION) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Esta rutina ya fue respondida anteriormente");
        }

        rutina.setEstado(aceptar ? EstadoRutina.ACEPTADA : EstadoRutina.RECHAZADA);
        rutina = rutinaRepository.save(rutina);

        if (rutina.getDeportista() != null) {
            notificar(
                    rutina.getDeportista(),
                    aceptar
                            ? "Tu entrenador aceptó tu rutina propuesta: " + rutina.getNombre()
                            : "Tu entrenador rechazó tu rutina propuesta: " + rutina.getNombre());
        }

        return rutina;
    }

 
    @Transactional
    public List<Rutina> crearRutina(Long entrenadorId, CrearRutinaRequest request) {

        Entrenador entrenador = entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El entrenador indicado no existe"));

     
        if (entrenador.getDeportistas() == null || entrenador.getDeportistas().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No tienes deportistas asignados");
        }

  
        if (request.getEjercicios() == null || request.getEjercicios().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La rutina debe tener al menos un ejercicio");
        }

        List<Rutina> rutinasCreadas = new ArrayList<>();

        for (Long deportistaId : request.getDeportistaIds()) {

            Deportista deportista = deportistaRepository.findById(deportistaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "El deportista " + deportistaId + " no existe"));

            boolean asignadoAEsteEntrenador = entrenador.getDeportistas().stream()
                    .anyMatch(d -> d.getId().equals(deportistaId));

            if (!asignadoAEsteEntrenador) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El deportista " + deportistaId + " no está asignado a este entrenador");
            }

            Rutina rutina = new Rutina();
            rutina.setNombre(request.getNombre());
            rutina.setDescripcion(request.getDescripcion());
            rutina.setDuracionMinutos(request.getDuracionMinutos());
            rutina.setIntensidad(request.getIntensidad());
            rutina.setEstado(EstadoRutina.ASIGNADA);
            rutina.setFechaCreacion(LocalDateTime.now());
            rutina.setCreador(entrenador);
            rutina.setEjercicios(mapearEjercicios(request.getEjercicios()));

            deportista.agregarRutina(rutina);

            rutina = rutinaRepository.save(rutina);

            registrarEnHistorialDeportista(deportista, rutina);

            deportistaRepository.save(deportista);

            notificar(
                    deportista,
                    "Tu entrenador te asignó una nueva rutina: " + rutina.getNombre());

            rutinasCreadas.add(rutina);
        }

        registrarEnHistorialEntrenador(entrenador, rutinasCreadas);
        entrenadorRepository.save(entrenador);

        return rutinasCreadas;
    }

    
    @Transactional
    public Rutina proponerRutina(Long deportistaId, ProponerRutinaRequest request) {

        Deportista deportista = deportistaRepository.findById(deportistaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El deportista indicado no existe"));

        if (deportista.getEntrenador() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No tienes un entrenador asignado");
        }

      
        boolean tienePropuestaPendiente = rutinaRepository.findByDeportistaId(deportistaId)
                .stream()
                .anyMatch(r -> r.getEstado() == EstadoRutina.PENDIENTE_VALIDACION);

        if (tienePropuestaPendiente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya tienes una rutina propuesta pendiente de validación");
        }

        if (request.getEjercicios() == null || request.getEjercicios().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La rutina debe tener al menos un ejercicio");
        }

        Rutina rutina = new Rutina();
        rutina.setNombre(request.getNombre());
        rutina.setDescripcion(request.getDescripcion());
        rutina.setDuracionMinutos(request.getDuracionMinutos());
        rutina.setIntensidad(request.getIntensidad());
        rutina.setEstado(EstadoRutina.PENDIENTE_VALIDACION);
        rutina.setFechaCreacion(LocalDateTime.now());
        rutina.setCreador(deportista.getEntrenador());
        rutina.setEjercicios(mapearEjercicios(request.getEjercicios()));

        deportista.agregarRutina(rutina);

        rutina = rutinaRepository.save(rutina);

        deportistaRepository.save(deportista);

        notificar(
                deportista.getEntrenador(),
                deportista.getNombre() + " " + deportista.getApellido()
                        + " te propuso una nueva rutina: " + rutina.getNombre());

        return rutina;
    }

 
    private List<Ejercicio> mapearEjercicios(List<EjercicioRequest> ejerciciosRequest) {
        List<Ejercicio> ejercicios = new ArrayList<>();

        for (EjercicioRequest er : ejerciciosRequest) {

            CatalogoEjercicio catalogoEjercicio = catalogoEjercicioRepository
                    .findById(er.getCatalogoEjercicioId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "El ejercicio del catálogo indicado no existe"));

            String notaTecnica = (er.getNotaTecnica() != null && !er.getNotaTecnica().isBlank())
                    ? er.getNotaTecnica()
                    : catalogoEjercicio.getNotaTecnicaSugerida();

            ejercicios.add(new Ejercicio(
                    catalogoEjercicio,
                    er.getSeries(),
                    er.getRepeticiones(),
                    notaTecnica));
        }

        return ejercicios;
    }

    private void registrarEnHistorialDeportista(Deportista deportista, Rutina rutina) {
        Historial historial = deportista.getHistorial();
        if (historial == null) {
            historial = new Historial();
            deportista.setHistorial(historial);
        }
        historial.agregarRutina(rutina);
    }

    private void registrarEnHistorialEntrenador(Entrenador entrenador, List<Rutina> rutinas) {
        Historial historial = entrenador.getHistorial();
        if (historial == null) {
            historial = new Historial();
            entrenador.setHistorial(historial);
        }
        for (Rutina rutina : rutinas) {
            historial.agregarRutina(rutina);
        }
    }

    private void notificar(cl.usach.fitcollab.entities.Usuario destinatario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje(mensaje);
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setDestinatario(destinatario);
        notificacionRepository.save(notificacion);
    }

    @Transactional
    public Rutina marcarComoRealizada(Long rutinaId) {

        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rutina no encontrada"));

        if (rutina.getEstado() != EstadoRutina.ACEPTADA
                && rutina.getEstado() != EstadoRutina.ASIGNADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo una rutina asignada o aceptada puede marcarse como realizada");
        }

        rutina.setEstado(EstadoRutina.REALIZADA);

        return rutinaRepository.save(rutina);
    }
}