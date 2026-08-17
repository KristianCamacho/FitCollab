package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.EntrenadorRepository;

@Service
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;
    private final DeportistaRepository deportistaRepository;

    public EntrenadorService(
            EntrenadorRepository entrenadorRepository,
            DeportistaRepository deportistaRepository) {
        this.entrenadorRepository = entrenadorRepository;
        this.deportistaRepository = deportistaRepository;
    }

    public Optional<Entrenador> obtenerPorId(Long id) {
        return entrenadorRepository.findById(id);
    }

 
    @Transactional(readOnly = true)
    public Optional<List<Deportista>> obtenerDeportistas(Long entrenadorId) {
        if (!entrenadorRepository.existsById(entrenadorId)) {
            return Optional.empty();
        }
        return Optional.of(deportistaRepository.findByEntrenadorId(entrenadorId));
    }
}