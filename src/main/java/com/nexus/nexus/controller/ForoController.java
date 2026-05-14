package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Foro;
import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.entity.Comentario;
import com.nexus.nexus.repository.ForoRepository;
import com.nexus.nexus.repository.UsuarioRepository;
import com.nexus.nexus.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/foros")
public class ForoController {

    @Autowired
    private ForoRepository foroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    // LISTAR Y BUSCAR
    @GetMapping
    public String listarForos(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(value = "buscar", required = false) String buscar,
                              Model model) {

        // Cambio: Uso de orElseThrow para evitar errores de puntero nulo
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Foro> misForos;

        if (buscar != null && !buscar.isEmpty()) {
            misForos = foroRepository.findByUsuarioAndTituloContainingIgnoreCase(usuario, buscar);
        } else {
            misForos = foroRepository.findByUsuario(usuario);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("misForos", misForos);
        return "foro/foro-lista";
    }

    @GetMapping("/crear")
    public String formularioCrear(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("foro", new Foro());
        return "foro/foro-form";
    }

    @PostMapping("/guardar")
    public String guardarForo(@ModelAttribute("foro") Foro foro, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Importante: Vinculamos el foro con el usuario de la sesión actual
        foro.setUsuario(usuario);
        foroRepository.save(foro);

        return "redirect:/foros";
    }

    @GetMapping("/ver/{id}")
    public String verDetalleForo(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Foro foro = foroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).get();

        // Buscamos los comentarios de este foro
        List<Comentario> comentarios = comentarioRepository.findByForoOrderByFechaDesc(foro);

        model.addAttribute("foro", foro);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("usuario", usuario); // Para mostrar quién está comentando

        return "foro/foro-detalle";
    }

    @PostMapping("/comentar/{idForo}")
    public String guardarComentario(@PathVariable Integer idForo,
                                    @RequestParam("contenido") String contenido,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Buscamos el foro y el usuario
        Foro foro = foroRepository.findById(idForo)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Creamos y configuramos el comentario
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setForo(foro);
        nuevoComentario.setUsuario(usuario);
        // La fecha se asigna sola si la pusiste en la entidad, si no:
        // nuevoComentario.setFecha(LocalDateTime.now());

        // 3. Guardamos
        comentarioRepository.save(nuevoComentario);

        // 4. Redirigimos de vuelta a la página del foro para ver el comentario nuevo
        return "redirect:/foros/ver/" + idForo;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarForo(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        // 1. Buscamos el foro
        Foro foro = foroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

        // 2. Seguridad: Verificamos que el que intenta borrar sea el dueño
        if (foro.getUsuario().getUsername().equals(userDetails.getUsername())) {
            foroRepository.delete(foro);
        }

        // 3. Volvemos a la lista
        return "redirect:/foros";
    }
}