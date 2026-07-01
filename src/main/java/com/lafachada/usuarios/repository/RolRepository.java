package com.lafachada.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lafachada.usuarios.model.Rol;

@Repository

public interface RolRepository extends JpaRepository<Rol, Integer> {
}
