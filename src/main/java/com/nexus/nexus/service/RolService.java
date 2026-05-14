package com.nexus.nexus.service;

import com.nexus.nexus.entity.Rol;
import com.nexus.nexus.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombreRol(nombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombre));
    }
}