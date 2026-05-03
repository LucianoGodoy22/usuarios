package com.lafachada.usuarios.controller;

import com.lafachada.usuarios.dto.LoginRequest;
import com.lafachada.usuarios.dto.RegistroRequest;
import com.lafachada.usuarios.model.Usuario;
import com.lafachada.usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@RequestBody RegistroRequest request) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(request);
            return ResponseEntity.ok(Map.of(
                "message", "Usuario registrado exitosamente",
                "usuario", nuevoUsuario
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error al registrar usuario: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.autenticarUsuario(
            loginRequest.getEmail(), 
            loginRequest.getPassword()
        );
        
        if (usuario.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "usuario", usuario.get(),
                "token", "dummy-token-" + usuario.get().getIdUsuario()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Credenciales inválidas"
            ));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        return usuarioActualizado.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
