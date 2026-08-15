package cl.usach.fitcollab.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.fitcollab.dto.LoginRequest;
import cl.usach.fitcollab.dto.LoginResponse;
import cl.usach.fitcollab.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest request) {

        Optional<LoginResponse> resultado = authService.iniciarSesion(
                request.getCorreo(),
                request.getContrasena()
        );

        if (resultado.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body("Correo o contraseña incorrectos");
        }

        return ResponseEntity.ok(resultado.get());
    }
}
