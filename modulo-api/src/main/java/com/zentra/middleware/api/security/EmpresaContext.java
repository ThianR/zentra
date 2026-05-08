package com.zentra.middleware.api.security;

/**
 * Contexto de empresa activa para el request actual.
 * Se establece desde el JwtAuthFilter al decodificar el token.
 * Permite a los controladores obtener la empresa activa sin parámetros adicionales.
 */
public class EmpresaContext {

    private static final ThreadLocal<String> currentEmpresaId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentClienteId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static void setEmpresaId(String empresaId) { currentEmpresaId.set(empresaId); }
    public static String getEmpresaId() { return currentEmpresaId.get(); }

    public static void setClienteId(String clienteId) { currentClienteId.set(clienteId); }
    public static String getClienteId() { return currentClienteId.get(); }

    public static void setUsername(String username) { currentUsername.set(username); }
    public static String getUsername() { return currentUsername.get(); }

    /** Limpia todos los valores del ThreadLocal al finalizar el request */
    public static void clear() {
        currentEmpresaId.remove();
        currentClienteId.remove();
        currentUsername.remove();
    }
}
