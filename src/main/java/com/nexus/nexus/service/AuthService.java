package com.nexus.nexus.service;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.entity.Rol;
import com.nexus.nexus.repository.UsuarioRepository;
import com.nexus.nexus.repository.RolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(Usuario usuario) {
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Cambiamos 2L por 2 (Integer)
        Rol rolUser = rolRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Error: El rol USER (ID 2) no existe en la base de datos."));

        usuario.setRol(rolUser);
        usuarioRepository.save(usuario);    }
}