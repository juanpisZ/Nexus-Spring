package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Evento;
import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.repository.EventoRepository;
import com.nexus.nexus.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <--- ESTA ES LA IMPORTACIÓN QUE FALTABA

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        model.addAttribute("evento", new Evento());

        return "evento/CrearEvento";
    }


    @PostMapping("/guardar")
    public String guardarEvento(@Valid @ModelAttribute("evento") Evento evento,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails) {

        // Si hay errores de validación (ej. fecha pasada), regresamos al formulario
        if (result.hasErrors()) {
            return "evento/CrearEvento";
        }

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        evento.setUsuario(usuario);
        eventoRepository.save(evento);

        // IMPORTANTE: Usa redirect: para evitar el 404 al refrescar
        return "redirect:/dashboard?exito_evento";
    }

    @GetMapping("/explorar")
    public String explorarEventos(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(value = "buscar", required = false) String buscar) {

        // Cambio a orElseThrow para mayor seguridad
        Usuario usuarioActual = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Evento> todosLosEventos;

        if (buscar != null && !buscar.isEmpty()) {
            todosLosEventos = eventoRepository.findByTituloContainingIgnoreCase(buscar);
        } else {
            todosLosEventos = eventoRepository.findAll();
        }

        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("eventos", todosLosEventos);
        return "evento/explorar-eventos";
    }


    // --- EDITAR EVENTO ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Seguridad: Verificar que el usuario logueado es el creador
        if (!evento.getUsuario().getUsername().equals(userDetails.getUsername())) {
            return "redirect:/dashboard?error=No+tienes+permiso";
        }

        model.addAttribute("evento", evento);
        return "evento/CrearEvento"; // Reutilizamos el mismo formulario
    }

    // --- ELIMINAR EVENTO ---
    @GetMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Seguridad: Solo el dueño elimina
        if (evento.getUsuario().getUsername().equals(userDetails.getUsername())) {
            eventoRepository.delete(evento);
        }

        return "redirect:/dashboard?eliminado";
    }
}