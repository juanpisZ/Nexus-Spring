package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.entity.Evento;
import com.nexus.nexus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/dashboard") // La base es /dashboard
public class UserDashboardController {

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping // Esto mapea a GET /dashboard
    public String verDashboard(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(value = "buscar", required = false) String buscar,
                               Model model) {

        if (userDetails == null) return "redirect:/auth/login";

        String username = userDetails.getUsername();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Evento> misEventos;
        if (buscar != null && !buscar.isEmpty()) {
            misEventos = eventoRepository.findByUsuarioAndTituloContainingIgnoreCase(usuario, buscar);
        } else {
            misEventos = eventoRepository.findByUsuario(usuario);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("misEventos", misEventos);

        // ASEGÚRATE que este HTML exista en src/main/resources/templates/auth/dashboard.html
        return "auth/dashboard";
    }

    @PostMapping("/evento/eliminar/{id}") // Mapea a POST /dashboard/evento/eliminar/{id}
    public String eliminarEvento(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        if (evento.getUsuario().getUsername().equals(userDetails.getUsername())) {
            eventoRepository.deleteById(id);
        }

        return "redirect:/dashboard";
    }
}