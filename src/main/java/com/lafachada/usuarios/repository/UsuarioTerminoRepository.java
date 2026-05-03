package com.lafachada.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lafachada.usuarios.model.UsuarioTermino;

@Repository
public interface UsuarioTerminoRepository extends JpaRepository<UsuarioTermino, Integer> {

    @Modifying
    @Query("DELETE FROM UsuarioTermino ut WHERE ut.usuario.idUsuario = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Integer usuarioId);

}