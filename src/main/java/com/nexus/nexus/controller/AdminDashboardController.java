package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.repository.EventoRepository;
import com.nexus.nexus.repository.ForoRepository;
import com.nexus.nexus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ForoRepository foroRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        try {
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("eventos", eventoRepository.findAll());
            model.addAttribute("foros", foroRepository.findAll());
            return "admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/usuario/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/evento/eliminar/{id}")
    public String eliminarEvento(@PathVariable Integer id) {
        eventoRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/foro/eliminar/{id}")
    public String eliminarForo(@PathVariable Integer id) {
        foroRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}