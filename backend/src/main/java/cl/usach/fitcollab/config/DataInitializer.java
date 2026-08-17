package cl.usach.fitcollab.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.usach.fitcollab.entities.CatalogoEjercicio;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.repository.CatalogoEjercicioRepository;
import cl.usach.fitcollab.repository.DeportistaRepository;
import cl.usach.fitcollab.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner crearUsuariosPrueba(
            UsuarioRepository usuarioRepository,
            DeportistaRepository deportistaRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String correoDeportista = "deportista@fitcollab.cl";
            String correoEntrenador = "entrenador@fitcollab.cl";

            Deportista deportista;

            if (!usuarioRepository.existsByCorreo(correoDeportista)) {
                deportista = new Deportista(
                        "Usuario", "Prueba", correoDeportista,
                        passwordEncoder.encode("123456"));
                usuarioRepository.save(deportista);
                System.out.println("Deportista de prueba creado");
            } else {
                deportista = (Deportista) usuarioRepository
                        .findByCorreo(correoDeportista).get();
            }

            Entrenador entrenador;

            if (!usuarioRepository.existsByCorreo(correoEntrenador)) {
                entrenador = new Entrenador(
                        "Coach", "Prueba", correoEntrenador,
                        passwordEncoder.encode("123456"));
                usuarioRepository.save(entrenador);
                System.out.println("Entrenador de prueba creado");
            } else {
                entrenador = (Entrenador) usuarioRepository
                        .findByCorreo(correoEntrenador).get();
            }


            boolean yaAsignado = deportistaRepository.findByEntrenadorId(entrenador.getId())
                    .stream()
                    .anyMatch(d -> d.getId().equals(deportista.getId()));

            if (!yaAsignado) {
                deportista.setEntrenador(entrenador);
                deportistaRepository.save(deportista);
                System.out.println("Deportista de prueba asignado al entrenador de prueba");
            }
        };
    }

  
    @Bean
    CommandLineRunner crearCatalogoEjercicios(
            CatalogoEjercicioRepository catalogoEjercicioRepository) {

        return args -> {

            if (catalogoEjercicioRepository.count() > 0) {
                return;
            }

            List<CatalogoEjercicio> catalogo = List.of(
                    new CatalogoEjercicio("Sentadilla", "Piernas",
                            "Mantén la espalda recta y las rodillas alineadas con los pies"),
                    new CatalogoEjercicio("Peso muerto", "Piernas / Espalda",
                            "Espalda neutra durante todo el movimiento"),
                    new CatalogoEjercicio("Press banca", "Pecho",
                            "Escápulas retraídas, barra hasta el pecho de forma controlada"),
                    new CatalogoEjercicio("Press militar", "Hombros",
                            "Core activado, evitar hiperextender la zona lumbar"),
                    new CatalogoEjercicio("Dominadas", "Espalda",
                            "Rango completo, evitar impulso con las piernas"),
                    new CatalogoEjercicio("Remo con barra", "Espalda",
                            "Torso inclinado y fijo, tirar hacia el abdomen"),
                    new CatalogoEjercicio("Curl de bíceps", "Brazos",
                            "Codos pegados al torso durante todo el recorrido"),
                    new CatalogoEjercicio("Extensión de tríceps", "Brazos",
                            "Codos fijos, mover solo el antebrazo"),
                    new CatalogoEjercicio("Zancadas", "Piernas",
                            "Rodilla delantera no debe sobrepasar la punta del pie"),
                    new CatalogoEjercicio("Plancha abdominal", "Core",
                            "Cuerpo alineado de cabeza a talones, sin elevar la cadera"),
                    new CatalogoEjercicio("Elevaciones laterales", "Hombros",
                            "Movimiento controlado, evitar usar impulso"),
                    new CatalogoEjercicio("Hip thrust", "Glúteos",
                            "Contraer glúteos en la parte alta del movimiento"),
                    new CatalogoEjercicio("Jalón al pecho", "Espalda",
                            "Tirar la barra hacia la parte alta del pecho, sin balancear el cuerpo"),
                    new CatalogoEjercicio("Abdominales crunch", "Core",
                            "Movimiento corto y controlado, sin tirar del cuello"),
                    new CatalogoEjercicio("Trote / cinta", "Cardio",
                            "Mantener una postura erguida y respiración constante")
            );

            catalogoEjercicioRepository.saveAll(catalogo);
            System.out.println("Catálogo de ejercicios inicial creado (" + catalogo.size() + " ejercicios)");
        };
    }
}