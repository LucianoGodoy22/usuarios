package com.lafachada.usuarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lafachada.usuarios.dto.RegistroRequest;
import com.lafachada.usuarios.model.*;
import com.lafachada.usuarios.repository.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private TerminoCondicioneRepository terminoCondicioneRepository;
    @Mock private UsuarioTerminoRepository usuarioTerminoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioService usuarioService;

    private RegistroRequest registroRequest;
    private Usuario usuario;
    private Rol rol;
    private TerminoCondicione termino;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroRequest("Juan", "juan@test.com", "12345", 1, 1);
        rol = new Rol(1, "ROLE_USER");
        termino = new TerminoCondicione(1, "v1.0", null);
        usuario = new Usuario(1, "Juan", "juan@test.com", "encodedPassword", rol);
    }

    // --- Pruebas de Registro ---

    @Test
    void registrarUsuario_Exito() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Optional.of(rol));
        when(terminoCondicioneRepository.findById(anyInt())).thenReturn(Optional.of(termino));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(registroRequest);

        assertNotNull(resultado);
        assertEquals("juan@test.com", resultado.getEmail());
        verify(usuarioTerminoRepository, times(1)).save(any(UsuarioTermino.class));
    }

    @Test
    void registrarUsuario_FallaPorEmailYaRegistrado() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(registroRequest);
        });

        assertEquals("Email ya registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_FallaPorRolNoEncontrado() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(registroRequest);
        });

        assertEquals("Rol no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_FallaPorTerminosNoEncontrados() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(anyInt())).thenReturn(Optional.of(rol));
        when(terminoCondicioneRepository.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(registroRequest);
        });

        assertEquals("Términos y condiciones no encontrados", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // --- Pruebas de Autenticación ---

    @Test
    void autenticarUsuario_Exito() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("12345", "encodedPassword")).thenReturn(true);

        Optional<Usuario> resultado = usuarioService.autenticarUsuario("juan@test.com", "12345");

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void autenticarUsuario_FallaPorPasswordIncorrecto() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        Optional<Usuario> resultado = usuarioService.autenticarUsuario("juan@test.com", "wrongPass");

        assertFalse(resultado.isPresent());
    }

    @Test
    void autenticarUsuario_FallaPorEmailNoEncontrado() {
        when(usuarioRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.autenticarUsuario("notfound@test.com", "12345");

        assertFalse(resultado.isPresent());
    }

    // --- Pruebas de Consultas (Read) ---

    @Test
    void obtenerUsuarioPorId_Exito() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void obtenerUsuarioPorId_NoEncontrado() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(99);

        assertFalse(resultado.isPresent());
    }

    @Test
    void obtenerTodosLosUsuarios_Exito() {
        Usuario usuario2 = new Usuario(2, "Maria", "maria@test.com", "encoded", rol);
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertEquals(2, resultado.size());
    }

    // --- Pruebas de Actualización (Update) ---

    @Test
    void actualizarUsuario_Exito() {
        Usuario usuarioActualizadoInfo = new Usuario();
        usuarioActualizadoInfo.setNombre("Juan Modificado");
        usuarioActualizadoInfo.setEmail("juan.mod@test.com");
        usuarioActualizadoInfo.setPassword("newPassword");

        Usuario usuarioGuardado = new Usuario(1, "Juan Modificado", "juan.mod@test.com", "encodedNewPassword", rol);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Optional<Usuario> resultado = usuarioService.actualizarUsuario(1, usuarioActualizadoInfo);

        assertTrue(resultado.isPresent());
        assertEquals("Juan Modificado", resultado.get().getNombre());
        assertEquals("juan.mod@test.com", resultado.get().getEmail());
        assertEquals("encodedNewPassword", resultado.get().getPassword());
    }

    // --- Pruebas de Eliminación (Delete) ---

    @Test
    void eliminarUsuario_Exito() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(usuarioTerminoRepository).deleteByUsuarioId(1);
        doNothing().when(usuarioRepository).deleteById(1);

        boolean resultado = usuarioService.eliminarUsuario(1);

        assertTrue(resultado);
        verify(usuarioTerminoRepository, times(1)).deleteByUsuarioId(1);
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarUsuario_NoEncontrado() {
        when(usuarioRepository.existsById(99)).thenReturn(false);

        boolean resultado = usuarioService.eliminarUsuario(99);

        assertFalse(resultado);
        verify(usuarioTerminoRepository, never()).deleteByUsuarioId(anyInt());
        verify(usuarioRepository, never()).deleteById(anyInt());
    }
}