package com.lafachada.usuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_terminos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioTermino {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_termino")
    private TerminoCondicione termino;
    
    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;
}
