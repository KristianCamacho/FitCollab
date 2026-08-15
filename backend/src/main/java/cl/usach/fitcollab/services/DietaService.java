package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.repository.DeportistaRepository;
import org.springframework.stereotype.Service;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Dieta;
import cl.usach.fitcollab.entities.Notificacion;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.DietaRepository;
import cl.usach.fitcollab.repository.NotificacionRepository;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;
    private final DeportistaRepository deportistaRepository;
    private final NotificacionRepository notificacionRepository;

    public DietaService(DietaRepository dietaRepository,
                        DeportistaRepository deportistaRepository,
                        NotificacionRepository notificacionRepository) {
        this.dietaRepository = dietaRepository;
        this.deportistaRepository = deportistaRepository;
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

    //yo
    public Dieta crearDieta(Dieta dieta){
        dieta.setFechaCreacion(LocalDateTime.now());
        return dietaRepository.save(dieta);
    }

    public List<Dieta> obtenerDietaPorNutricionista(Long nutricionistaId){
        return dietaRepository.findByCreadorId(nutricionistaId);
    }

    public List<Dieta> obtenerDietaPorDeportista(Long deportistaId){
        return dietaRepository.findByDeportistaId(deportistaId);
    }

    public Dieta editarDieta(Long id, Dieta dietaActualizada, Long deportistaId) {
        Dieta dieta = dietaRepository.findById(id).orElseThrow(() -> new RuntimeException("Dieta no encontrada con id: " + id));

        dieta.setComidas(dietaActualizada.getComidas());
        dieta.setPorciones(dietaActualizada.getPorciones());
        dieta.setHorarios(dietaActualizada.getHorarios());
        dieta.setAlimentosExcluidos(dietaActualizada.getAlimentosExcluidos());
        dieta.setSugerenciaAlimenticia(dietaActualizada.getSugerenciaAlimenticia());

        Dieta dietaGuardada = dietaRepository.save(dieta);

        //notifica al o a la deportista
        Deportista deportista = deportistaRepository.findById(deportistaId).orElseThrow(() -> new RuntimeException("Deportista no encontrado/a."));

        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje("Tu plan alimenticio ha sido actualizado por tu nutricionista.");
        notificacion.setFechaHora(LocalDateTime.now());
        notificacion.setDestinatario(deportista);
        notificacionRepository.save(notificacion);


        return dietaGuardada;
    }

    public void eliminarDieta(Long id){
        if (!dietaRepository.existsById(id)){
            throw new RuntimeException("Dieta no encontrada con id: " + id);
        }
        dietaRepository.deleteById(id);
    }

}
