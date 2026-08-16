package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.enums.EstadoRutina;
import cl.usach.fitcollab.repository.RutinaRepository;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;

    public RutinaService(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
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
    public Rutina calificarRutina(Long rutinaId, Integer calificacion) {

        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La rutina indicada no existe"));

        if (rutina.getEstado() != EstadoRutina.REALIZADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se pueden calificar rutinas realizadas");
        }

        if (rutina.getCalificacion() != 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Esta rutina ya fue calificada");
        }

        rutina.setCalificacion(calificacion);
        return rutinaRepository.save(rutina);
    }
}