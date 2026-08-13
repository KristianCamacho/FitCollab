package cl.usach.fitcollab.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.entities.Dieta;
import cl.usach.fitcollab.repository.DietaRepository;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;

    public DietaService(DietaRepository dietaRepository) {
        this.dietaRepository = dietaRepository;
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
}
