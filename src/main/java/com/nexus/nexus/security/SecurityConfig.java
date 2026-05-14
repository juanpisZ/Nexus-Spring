package com.nexus.nexus.security;

import com.nexus.nexus.entity.Usuario;
import com.nexus.nexus.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/auth/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Usamos Authority para ser exactos
                        .requestMatchers("/dashboard/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            // Imprimimos en consola para ver qué está pasando realmente
                            var auths = authentication.getAuthorities();
                            System.out.println(">>> LOGIN EXITOSO! Permisos: " + auths);

                            boolean isAdmin = auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/dashboard");
                            }
                        })
                        .failureUrl("/auth/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repo) {
        return username -> {
            Usuario user = repo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

            // Forzamos que si es el usuario 'admin', tenga el rol ADMIN
            String rolNombre = "USER";
            if (user.getRol() != null && user.getRol().getNombreRol().toUpperCase().contains("ADMIN")
                    || user.getUsername().equals("admin")) {
                rolNombre = "ADMIN";
            }

            System.out.println(">>> CARGANDO: " + username + " CON ROL: ROLE_" + rolNombre);

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_" + rolNombre)
                    .build();
        };
    }
}