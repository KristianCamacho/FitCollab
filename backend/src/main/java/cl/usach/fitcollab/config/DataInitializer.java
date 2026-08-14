package cl.usach.fitcollab.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner crearUsuarioPrueba(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String correo = "deportista@fitcollab.cl";

            if (!usuarioRepository.existsByCorreo(correo)) {

                Deportista deportista = new Deportista(
                        "Usuario",
                        "Prueba",
                        correo,
                        passwordEncoder.encode("123456")
                );

                usuarioRepository.save(deportista);

                System.out.println("Usuario de prueba creado");
            }
        };
    }
}