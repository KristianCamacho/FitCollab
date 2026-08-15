package cl.usach.fitcollab.services;

import cl.usach.fitcollab.dto.RutinaRequest;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.entities.Rutina;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.EntrenadorRepository;
import cl.usach.fitcollab.repository.RutinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RutinaService {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private DeportistaRepository deportistaRepository;

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    public List<Rutina> obtenerTodas() {
        return rutinaRepository.findAll();
    }

    public Rutina obtenerPorId(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada con id: " + id));
    }

    public Rutina crear(RutinaRequest request) {
        Rutina rutina = new Rutina();
        rutina.setFechaCreacion(LocalDateTime.now());
        mapearRequestARutina(request, rutina);
        return rutinaRepository.save(rutina);
    }

    public Rutina actualizar(Long id, RutinaRequest request) {
        Rutina rutina = obtenerPorId(id);
        mapearRequestARutina(request, rutina);
        return rutinaRepository.save(rutina);
    }

    public void eliminar(Long id) {
        rutinaRepository.deleteById(id);
    }

    private void mapearRequestARutina(RutinaRequest request, Rutina rutina) {
        rutina.setNombre(request.getNombre());
        rutina.setDescripcion(request.getDescripcion());
        rutina.setDuracionMinutos(request.getDuracionMinutos());
        rutina.setIntensidad(request.getIntensidad());

        if (request.getDeportistaId() != null) {
            Deportista deportista = deportistaRepository.findById(request.getDeportistaId())
                    .orElse(null);
            rutina.setDeportista(deportista);
        }

        if (request.getCreadorId() != null) {
            Entrenador creador = entrenadorRepository.findById(request.getCreadorId())
                    .orElse(null);
            rutina.setCreador(creador);
        }
    }
}