package com.nexus.nexus;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.entity.Rol;
import com.nexus.nexus.entity.Foro;
import com.nexus.nexus.repository.ForoRepository;
import com.nexus.nexus.repository.UsuarioRepository;
import com.nexus.nexus.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(UsuarioRepository uRepo,
                                   RolRepository rRepo,
                                   ForoRepository fRepo,
                                   PasswordEncoder encoder) {
        return args -> {
            // 1. Asegurar el Rol
            Rol adminRol = rRepo.findByNombreRol("ADMIN").orElseGet(() -> {
                Rol r = new Rol();
                r.setNombreRol("ADMIN");
                return rRepo.saveAndFlush(r);
            });

            // 2. Buscar o Crear Admin
            Usuario admin = uRepo.findByUsername("admin").orElse(null);

            if (admin == null) {
                admin = new Usuario();
                admin.setNombres("Admin");
                admin.setApellidos("Nexus");
                admin.setEmail("admin@nexus.com");
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol(adminRol);
                admin.setEstado("ACTIVO");
                // Forzamos el guardado físico en la DB
                admin = uRepo.saveAndFlush(admin);
                System.out.println(">>> Usuario ADMIN registrado físicamente.");
            }

            // 3. Crear Foros solo si la tabla está vacía y el admin es válido
            if (fRepo.count() == 0 && admin != null && admin.getIdUsuario() != null) {
                try {
                    Foro f1 = new Foro();
                    f1.setTitulo("Bienvenidos a Nexus");
                    f1.setDescripcion("Punto de encuentro inicial.");
                    f1.setUsuario(admin); // Asociación directa
                    fRepo.save(f1);

                    Foro f2 = new Foro();
                    f2.setTitulo("Reglas de la comunidad");
                    f2.setDescripcion("Normas importantes.");
                    f2.setUsuario(admin);
                    fRepo.save(f2);

                    fRepo.flush();
                    System.out.println(">>> FOROS DE PRUEBA CREADOS.");
                } catch (Exception e) {
                    System.err.println("Error al crear foros: " + e.getMessage());
                }
            }
        };
    }
}