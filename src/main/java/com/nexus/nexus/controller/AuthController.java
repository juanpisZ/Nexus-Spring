package com.nexus.nexus.controller;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.service.AuthService;
import com.nexus.nexus.repository.UsuarioRepository;
import jakarta.validation.Valid; // Necesario para @Valid
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Necesario para capturar errores
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository; // Añadido para validar duplicados

    // Constructor unificado
    public AuthController(AuthService authService, UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                               BindingResult result,
                               Model model) {

        // 1. Validaciones de anotaciones en la Entidad (@Email, @NotBlank, @Size)
        if (result.hasErrors()) {
            return "auth/register";
        }

        // 2. Validar que las contraseñas coincidan
        // Comparamos el campo password con el campo confirmPassword de la entidad
        if (usuario.getPassword() != null && !usuario.getPassword().equals(usuario.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.usuario", "Las contraseñas no coinciden");
            return "auth/register";
        }

        // 3. Validar si el username ya existe en la DB
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            result.rejectValue("username", "error.usuario", "Este nombre de usuario ya está en uso");
            return "auth/register";
        }

        // 4. Validar si el email ya existe en la DB
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            result.rejectValue("email", "error.usuario", "Este correo ya está registrado");
            return "auth/register";
        }

        // Si todo está bien, guardamos
        try {
            authService.registrarUsuario(usuario);
            return "redirect:/auth/login?success";
        }catch (Exception e) {
        e.printStackTrace(); // <--- ESTO te mostrará el error real en rojo en la consola
        model.addAttribute("error", "Error: " + e.getMessage());
        return "auth/register";
    }
    }
}

