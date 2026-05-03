package com.lafachada.usuarios.config;

import com.lafachada.usuarios.model.Rol;
import com.lafachada.usuarios.model.TerminoCondicione;
import com.lafachada.usuarios.repository.RolRepository;
import com.lafachada.usuarios.repository.TerminoCondicioneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RolRepository rolRepository;
    private final TerminoCondicioneRepository terminoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (rolRepository.count() == 0) {
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            rolRepository.save(rolAdmin);
            
            Rol rolUser = new Rol();
            rolUser.setNombre("USER");
            rolRepository.save(rolUser);
        }
        
        if (terminoRepository.count() == 0) {
            TerminoCondicione termino = new TerminoCondicione();
            termino.setVersion("1.0");
            termino.setFechaPublicacion(LocalDateTime.now());
            terminoRepository.save(termino);
        }
    }
}
