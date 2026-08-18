package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Dieta;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.entities.Nutricionista;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.DietaRepository;
import cl.usach.fitcollab.repository.NotificacionRepository;
import cl.usach.fitcollab.repository.NutricionistaRepository;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;
    private final DeportistaRepository deportistaRepository;
    private final NutricionistaRepository nutricionistaRepository;
    private final NotificacionRepository notificacionRepository;

    public DietaService(
            DietaRepository dietaRepository,
            DeportistaRepository deportistaRepository,
            NutricionistaRepository nutricionistaRepository,
            NotificacionRepository notificacionRepository) {

        this.dietaRepository = dietaRepository;
        this.deportistaRepository = deportistaRepository;
        this.nutricionistaRepository = nutricionistaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    public List<Dieta> obtenerTodas() {
        return dietaRepository.findAll();
    }

    public Optional<Dieta> obtenerPorId(Long id) {
        return dietaRepository.findById(id);
    }

    public Dieta guardar(Dieta dieta) {
        return dietaRepository.save(dieta);
    }

    public List<Deportista> obtenerDeportistasPorNutricionista(
            Long nutricionistaId) {

        return deportistaRepository
                .findByNutricionistaId(nutricionistaId);
    }

    public Dieta crearDieta(
            Dieta dieta,
            Long nutricionistaId,
            Long deportistaId) {

        Nutricionista nutricionista =
                nutricionistaRepository
                        .findById(nutricionistaId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Nutricionista no encontrado"));

        Deportista deportista =
                deportistaRepository
                        .findById(deportistaId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Deportista no encontrado"));

        /*
         * El nutricionista solo puede crear un plan
         * para uno de sus deportistas asignados.
         */
        if (deportista.getNutricionista() == null
                || !deportista
                        .getNutricionista()
                        .getId()
                        .equals(nutricionistaId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El deportista no está asignado a este nutricionista");
        }

        dieta.setCreador(nutricionista);
        dieta.setDeportista(deportista);
        dieta.setFechaCreacion(LocalDateTime.now());

        Dieta guardada =
                dietaRepository.save(dieta);

        /*
         * Avisar al deportista de que recibió
         * un nuevo plan alimenticio.
         */
        Notificacion notificacion =
                new Notificacion();

        notificacion.setMensaje(
                "Tu nutricionista te ha asignado un nuevo plan alimenticio.");

        notificacion.setFechaHora(
                LocalDateTime.now());

        notificacion.setDestinatario(
                deportista);

        notificacionRepository.save(
                notificacion);

        return guardada;
    }

    public List<Dieta> obtenerDietaPorNutricionista(
            Long nutricionistaId) {

        return dietaRepository
                .findByCreadorId(nutricionistaId);
    }

    public List<Dieta> obtenerDietaPorDeportista(
            Long deportistaId) {

        return dietaRepository
                .findByDeportistaId(deportistaId);
    }

    public Dieta editarDieta(
            Long id,
            Dieta dietaActualizada) {

        Dieta dieta =
                dietaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Dieta no encontrada con id: " + id));

        dieta.setComidas(
                dietaActualizada.getComidas());

        dieta.setPorciones(
                dietaActualizada.getPorciones());

        dieta.setHorarios(
                dietaActualizada.getHorarios());

        dieta.setAlimentosExcluidos(
                dietaActualizada.getAlimentosExcluidos());

        dieta.setSugerenciaAlimenticia(
                dietaActualizada.getSugerenciaAlimenticia());

        Dieta dietaGuardada =
                dietaRepository.save(dieta);

        Deportista deportista =
                dieta.getDeportista();

        if (deportista != null) {
            Notificacion notificacion =
                    new Notificacion();

            notificacion.setMensaje(
                    "Tu plan alimenticio ha sido actualizado por tu nutricionista.");

            notificacion.setFechaHora(
                    LocalDateTime.now());

            notificacion.setDestinatario(
                    deportista);

            notificacionRepository.save(
                    notificacion);
        }

        return dietaGuardada;
    }

    public void eliminarDieta(Long id) {

        if (!dietaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dieta no encontrada con id: " + id);
        }

        dietaRepository.deleteById(id);
    }
}
