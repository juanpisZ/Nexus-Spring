package com.nexus.nexus.repository;

import com.nexus.nexus.entity.Foro;
import com.nexus.nexus.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForoRepository extends JpaRepository<Foro, Integer> {

    // Spring busca la propiedad 'usuario' dentro de la clase Foro
    List<Foro> findByUsuario(Usuario usuario);

    // Debe coincidir exactamente con: findBy + Atributo + And + Atributo + Containing...
    List<Foro> findByUsuarioAndTituloContainingIgnoreCase(Usuario usuario, String titulo);

    long countByUsuario(Usuario usuario);
}