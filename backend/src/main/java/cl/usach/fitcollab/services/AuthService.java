package cl.usach.fitcollab.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.usach.fitcollab.dto.LoginResponse;
import cl.usach.fitcollab.entities.Administrador;
import cl.usach.fitcollab.entities.Deportista;
import cl.usach.fitcollab.entities.Entrenador;
import cl.usach.fitcollab.entities.Nutricionista;
import cl.usach.fitcollab.entities.Usuario;
import cl.usach.fitcollab.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<LoginResponse> iniciarSesion(String correo, String contrasena) {

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(correo);

        if (usuarioEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioEncontrado.get();

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return Optional.empty();
        }

        String rol = obtenerRol(usuario);

        LoginResponse respuesta = new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                rol
        );

        return Optional.of(respuesta);
    }

    private String obtenerRol(Usuario usuario) {

        if (usuario instanceof Deportista) {
            return "DEPORTISTA";
        }

        if (usuario instanceof Entrenador) {
            return "ENTRENADOR";
        }

        if (usuario instanceof Nutricionista) {
            return "NUTRICIONISTA";
        }

        if (usuario instanceof Administrador) {
            return "ADMINISTRADOR";
        }

        return "DESCONOCIDO";
    }
}