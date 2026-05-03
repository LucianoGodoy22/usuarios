package com.lafachada.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {
    private String nombre;
    private String email;
    private String password;
    @JsonProperty("id_rol")
    private Integer idRol;
    @JsonProperty("id_termino")
    private Integer idTermino;
}
