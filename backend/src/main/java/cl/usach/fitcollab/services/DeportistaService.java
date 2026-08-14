package cl.usach.fitcollab.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.usach.fitcollab.dto.FichaPersonalRequest;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.FichaPersonal;
import cl.usach.fitcollab.repository.DeportistaRepository;

@Service
public class DeportistaService {

    private final DeportistaRepository deportistaRepository;

    public DeportistaService(DeportistaRepository deportistaRepository) {
        this.deportistaRepository = deportistaRepository;
    }

    public Optional<Deportista> obtenerPorId(Long id) {
        return deportistaRepository.findById(id);
    }

    public Optional<FichaPersonal> obtenerFichaPersonal(Long deportistaId) {

        Optional<Deportista> deportistaEncontrado =
                deportistaRepository.findById(deportistaId);

        if (deportistaEncontrado.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                deportistaEncontrado.get().getFichaPersonal()
        );
    }

    public Optional<FichaPersonal> completarFichaPersonal(
            Long deportistaId,
            FichaPersonalRequest request) {

        Optional<Deportista> deportistaEncontrado =
                deportistaRepository.findById(deportistaId);

        if (deportistaEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Deportista deportista = deportistaEncontrado.get();

        FichaPersonal ficha = deportista.getFichaPersonal();

        if (ficha == null) {
            ficha = new FichaPersonal();
        }

        ficha.setEdad(request.getEdad());
        ficha.setPeso(request.getPeso());
        ficha.setAltura(request.getAltura());
        ficha.setObjetivo(request.getObjetivo());
        ficha.setRestriccionesAlimenticias(
                request.getRestriccionesAlimenticias()
        );

        ficha.setFechaActualizacion(LocalDateTime.now());

        deportista.setFichaPersonal(ficha);

        deportistaRepository.save(deportista);

        return Optional.of(ficha);
    }
}
