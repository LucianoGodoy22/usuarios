package com.lafachada.usuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lafachada.usuarios.dto.LoginRequest;
import com.lafachada.usuarios.dto.RegistroRequest;
import com.lafachada.usuarios.model.Rol;
import com.lafachada.usuarios.model.Usuario;
import com.lafachada.usuarios.security.JwtService;
import com.lafachada.usuarios.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock private UsuarioService usuarioService;
    @Mock private JwtService jwtService;

    @InjectMocks private UsuarioController usuarioController;

    private ObjectMapper objectMapper;
    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();
        usuarioPrueba = new Usuario(1, "Juan", "juan@test.com", "encoded", new Rol(1, "ROLE_USER"));
    }

    // --- Pruebas de Login ---

    @Test
    void login_Exito() throws Exception {
        LoginRequest loginRequest = new LoginRequest("juan@test.com", "12345");

        when(usuarioService.autenticarUsuario(anyString(), anyString())).thenReturn(Optional.of(usuarioPrueba));
        when(jwtService.generateToken(anyString(), anyInt(), anyString())).thenReturn("token-falso-123");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-falso-123"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.usuario.email").value("juan@test.com"));
    }

    @Test
    void login_FallaCredencialesInvalidas() throws Exception {
        LoginRequest loginRequest = new LoginRequest("juan@test.com", "wrongPass");

        when(usuarioService.autenticarUsuario(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    // --- Pruebas de Registro ---

    @Test
    void registrarUsuario_Exito() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest("Juan", "juan@test.com", "12345", 1, 1);

        when(usuarioService.registrarUsuario(any(RegistroRequest.class))).thenReturn(usuarioPrueba);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"))
                .andExpect(jsonPath("$.usuario.email").value("juan@test.com"));
    }

    @Test
    void registrarUsuario_FallaException() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest("Juan", "juan@test.com", "12345", 1, 1);

        when(usuarioService.registrarUsuario(any(RegistroRequest.class)))
                .thenThrow(new RuntimeException("Email ya registrado"));

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error al registrar usuario: Email ya registrado"));
    }

    // --- Pruebas de Búsqueda (GET) ---

    @Test
    void obtenerUsuario_Exito() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(1)).thenReturn(Optional.of(usuarioPrueba));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void obtenerUsuario_NoEncontrado() throws Exception {
        when(usuarioService.obtenerUsuarioPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    // --- Pruebas de Actualización (PUT) ---

    @Test
    void actualizarUsuario_Exito() throws Exception {
        Usuario usuarioActualizado = new Usuario(1, "Juan Modificado", "juan.mod@test.com", "encoded", new Rol(1, "ROLE_USER"));
        
        when(usuarioService.actualizarUsuario(eq(1), any(Usuario.class))).thenReturn(Optional.of(usuarioActualizado));

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioPrueba)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Modificado"));
    }

    @Test
    void actualizarUsuario_NoEncontrado() throws Exception {
        when(usuarioService.actualizarUsuario(eq(99), any(Usuario.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioPrueba)))
                .andExpect(status().isNotFound());
    }

    // --- Pruebas de Eliminación (DELETE) ---

    @Test
    void eliminarUsuario_Exito() throws Exception {
        when(usuarioService.eliminarUsuario(1)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado exitosamente"));
    }

    @Test
    void eliminarUsuario_NoEncontrado() throws Exception {
        when(usuarioService.eliminarUsuario(99)).thenReturn(false);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}