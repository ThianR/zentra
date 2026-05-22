package com.zentra.middleware.api.interceptor;

import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class SubscriptionInterceptor implements HandlerInterceptor {



    private final ClienteRepository clienteRepository;

    public SubscriptionInterceptor(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, @org.springframework.lang.NonNull Object handler) throws Exception {
        
        String uri = request.getRequestURI();
        
        // Solo interceptar peticiones a la API de emision
        if (!uri.startsWith("/api/v1/emision")) {
            return true;
        }

        String clienteId = EmpresaContext.getClienteId();
        if (clienteId == null) {
            return true; // No autenticado, dejar que Spring Security lo maneje
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return true;
        }

        Cliente cliente = clienteOpt.get();
        String estado = cliente.getEstadoSuscripcion();

        if ("MES_1_AVISO".equals(estado)) {
            response.setHeader("X-Zentra-Warning", "Suscripcion vencida, por favor regularice su pago");
        } else if ("MES_2_LIMITADO".equals(estado)) {

            // Por simplicidad, y según el requerimiento, devolvemos 429 si se excedió.
            // Si no tenemos el conteo exacto a mano, devolvemos 429 como advertencia limitante.
            response.setHeader("X-Zentra-Warning", "Suscripcion limitada por atraso de pago");
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("{\"error\": \"Límite de suscripción alcanzado. Regularice su pago.\"}");
            response.setContentType("application/json");
            return false;
        } else if ("MES_3_BLOQUEADO".equals(estado)) {
            response.setHeader("X-Zentra-Warning", "Suscripcion bloqueada por falta de pago");
            response.setStatus(402); // Payment Required
            response.getWriter().write("{\"error\": \"Suscripción bloqueada. Pago requerido para continuar.\"}");
            response.setContentType("application/json");
            return false;
        }

        return true;
    }
}
