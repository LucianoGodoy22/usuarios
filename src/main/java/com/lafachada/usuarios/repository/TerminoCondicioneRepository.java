package com.lafachada.usuarios.repository;

import com.lafachada.usuarios.model.TerminoCondicione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TerminoCondicioneRepository extends JpaRepository<TerminoCondicione, Integer> {
}
