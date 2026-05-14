package com.nexus.nexus.repository;

import com.nexus.nexus.entity.Evento;
import com.nexus.nexus.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // 1. Para listar los eventos propios en el Dashboard
    List<Evento> findByUsuario(Usuario usuario);

    // 2. EL QUE TE FALTA: Para buscar eventos propios por título (ERROR RESUELTO AQUÍ)
    List<Evento> findByUsuarioAndTituloContainingIgnoreCase(Usuario usuario, String titulo);

    // 3. Para el buscador global de la sección "Explorar"
    List<Evento> findByTituloContainingIgnoreCase(String titulo);

    // 4. Para las estadísticas
    long countByUsuario(Usuario usuario);
    }