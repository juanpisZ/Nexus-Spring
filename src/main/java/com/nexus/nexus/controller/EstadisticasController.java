package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.repository.ForoRepository;
import com.nexus.nexus.repository.EventoRepository;
import com.nexus.nexus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mis-estadisticas")
public class EstadisticasController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ForoRepository foroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String verEstadisticas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos los conteos reales de la base de datos
        long totalEventos = eventoRepository.countByUsuario(usuario);
        long totalForos = foroRepository.countByUsuario(usuario);

        // Simulación de otros datos (puedes implementarlos luego con lógica real)
        int totalAsistentes = (int) (totalEventos * 12); // Ejemplo: promedio de 12 por evento
        double popularidad = totalForos > 0 ? 85.5 : 0.0;

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalEventos", totalEventos);
        model.addAttribute("totalForos", totalForos);
        model.addAttribute("totalAsistentes", totalAsistentes);
        model.addAttribute("popularidad", popularidad);

        return "auth/estadisticas";
    }
}