package com.zentra.middleware.api.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que intercepta cada request HTTP para validar el token JWT.
 * Establece el SecurityContext y el EmpresaContext para el request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
                                    @org.springframework.lang.NonNull HttpServletResponse response,
                                    @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtService.isTokenValid(token)) {
                    Claims claims = jwtService.extractClaims(token);
                    String username = claims.getSubject();
                    String rol = claims.get("rol", String.class);
                    String clienteId = claims.get("clienteId", String.class);
                    String empresaId = claims.get("empresaId", String.class);

                    // Establecer contexto de seguridad de Spring
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                    var authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Establecer contexto de tenant para este request
                    EmpresaContext.setUsername(username);
                    EmpresaContext.setClienteId(clienteId);
                    if (empresaId != null) {
                        EmpresaContext.setEmpresaId(empresaId);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // Limpiar ThreadLocal al finalizar el request
            EmpresaContext.clear();
        }
    }
}
