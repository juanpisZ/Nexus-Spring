package com.nexus.nexus.repository;

import com.nexus.nexus.entity.Comentario;
import com.nexus.nexus.entity.Foro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    // Esto nos traerá los comentarios del más reciente al más antiguo
    List<Comentario> findByForoOrderByFechaDesc(Foro foro);
}