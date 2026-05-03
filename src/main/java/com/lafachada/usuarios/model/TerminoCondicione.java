package com.lafachada.usuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "terminos_condiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerminoCondicione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_termino")
    private Integer idTermino;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;
}
