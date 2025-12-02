package com.ucacue.udipsai.modules.seguridad.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "access-token-udipsai";
    private static final String TOKEN = "nOsr9FprhOWfLEti5KGJUK6RJWL8R0Ek";

    // Rutas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Verificar si la ruta es pública (no requiere autenticación)
        if (isPublicPath(requestPath)) {
            System.out.println("Public path accessed: " + requestPath);
            chain.doFilter(request, response);
            return;
        }

        System.out.println("JwtAuthenticationFilter is processing the request: " + requestPath);

        String token = request.getHeader(AUTH_HEADER);

        if (token == null || !TOKEN.equals(token)) {
            System.out.println("Invalid or missing token.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Token inválido, rechazar
            return;
        }

        System.out.println("Token is valid.");

        // Crear una autenticación forzada con un usuario "dummy" y rol "USER"
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Establecer la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    /**
     * Verifica si la ruta solicitada es pública
     */
    private boolean isPublicPath(String requestPath) {
        return PUBLIC_PATHS.stream()
                .anyMatch(requestPath::startsWith);
    }
}

