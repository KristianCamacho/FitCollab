package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.Rutina;
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
}
