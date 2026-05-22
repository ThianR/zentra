package com.zentra.middleware.api.config;

import com.zentra.middleware.api.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 * Define las rutas públicas y protegidas, y registra el filtro JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (API stateless)
            .csrf(csrf -> csrf.disable())
            // Sesiones stateless (JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos (login y assets)
                .requestMatchers(
                        "/",
                        "/login.html",
                        "/index.html",
                        "/superadmin.html",
                        "/aceptar-invitacion.html",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/api/v1/auth/**",
                        "/api/v1/superadmin/**",
                        "/api/v1/usuarios/invitacion/**",
                        "/api/v1/usuarios/aceptar-invitacion",
                        "/error"
                ).permitAll()
                // Proteger rutas de empresas (Solo ADMIN puede modificar o crear)
                .requestMatchers(HttpMethod.POST, "/api/v1/empresas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/empresas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/empresas/**").hasRole("ADMIN")
                // Proteger gestión de usuarios (Solo ADMIN puede invitar/listar todos)
                // Permitimos que GET /api/v1/usuarios sea ADMIN por ahora.
                .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                // Configuración e Inutilización también exclusivas de ADMIN
                .requestMatchers("/api/v1/eventos/inutilizacion/**").hasRole("ADMIN")
                // Todo lo demás requiere autenticación general (OPERADOR o ADMIN)
                .anyRequest().authenticated()
            )
            // Registrar filtro JWT antes del filtro de autenticación de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Retornar un manager vacío para evitar que Spring genere una password aleatoria en el log
        return new InMemoryUserDetailsManager();
    }
}
