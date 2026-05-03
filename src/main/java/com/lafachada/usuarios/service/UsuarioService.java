package com.lafachada.usuarios.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lafachada.usuarios.dto.RegistroRequest;
import com.lafachada.usuarios.model.Rol;
import com.lafachada.usuarios.model.TerminoCondicione;
import com.lafachada.usuarios.model.Usuario;
import com.lafachada.usuarios.model.UsuarioTermino;
import com.lafachada.usuarios.repository.RolRepository;
import com.lafachada.usuarios.repository.TerminoCondicioneRepository;
import com.lafachada.usuarios.repository.UsuarioRepository;
import com.lafachada.usuarios.repository.UsuarioTerminoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Transactional
    public boolean eliminarUsuario(Integer id) {
    if (usuarioRepository.existsById(id)) {

        usuarioTerminoRepository.deleteByUsuarioId(id);
        
        usuarioRepository.deleteById(id);
        return true;
    }
    return false;
}

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TerminoCondicioneRepository terminoCondicioneRepository;
    private final UsuarioTerminoRepository usuarioTerminoRepository;
    
    @Transactional 
    public Usuario registrarUsuario(RegistroRequest request) {
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }
        

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                
        TerminoCondicione termino = terminoCondicioneRepository.findById(request.getIdTermino())
                .orElseThrow(() -> new RuntimeException("Términos y condiciones no encontrados"));
        
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(rol);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        UsuarioTermino aceptacion = new UsuarioTermino();
        aceptacion.setUsuario(usuarioGuardado);
        aceptacion.setTermino(termino);
        aceptacion.setFechaAceptacion(LocalDateTime.now());
        
        usuarioTerminoRepository.save(aceptacion);
        
        return usuarioGuardado;
    }
    
    public Optional<Usuario> autenticarUsuario(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(usuario -> usuario.getPassword().equals(password));
    }
    
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioActualizado.getNombre());
                    usuario.setEmail(usuarioActualizado.getEmail());
                    if (usuarioActualizado.getPassword() != null) {
                        usuario.setPassword(usuarioActualizado.getPassword());
                    }
                    return usuarioRepository.save(usuario);
                });
    }
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}