package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String verPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "auth/perfil"; // Crearemos este archivo en templates/auth/perfil.html
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioEditado,
                                   @RequestParam(value = "nuevaPassword", required = false) String nuevaPassword,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioDb = usuarioRepository.findByUsername(userDetails.getUsername()).get();

        // Actualizamos campos básicos
        usuarioDb.setNombres(usuarioEditado.getNombres());
        usuarioDb.setApellidos(usuarioEditado.getApellidos());
        usuarioDb.setEmail(usuarioEditado.getEmail());

        // Si el usuario escribió una nueva contraseña, la encriptamos y guardamos
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            usuarioDb.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        usuarioRepository.save(usuarioDb);
        return "redirect:/perfil?exito";
    }
}