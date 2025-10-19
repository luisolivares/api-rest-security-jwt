package com.olivares.api_rest_security_jwt.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase de utilidades generales para el formateo de fechas,
 * serialización de objetos a JSON y construcción de URLs.
 * <p>
 * Esta clase contiene métodos estáticos y no requiere ser instanciada.
 */
public class Utils {

    /**
     * Formatea una fecha en formato corto: {@code yyyy-MM-dd'T'HH:mm:ss}.
     *
     * @param fecha Fecha a formatear.
     * @return Cadena con la fecha formateada.
     */
    public static String formatDateShort(LocalDateTime fecha) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return fecha.format(dateFormat);
    }

    /**
     * Formatea una fecha en formato largo con milisegundos y sufijo 'Z':
     * {@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}.
     *
     * @param fecha Fecha a formatear.
     * @return Cadena con la fecha formateada.
     */
    public static String formatDateLong(LocalDateTime fecha) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return fecha.format(dateFormat);
    }

    /**
     * Convierte un objeto Java a su representación JSON con formato legible.
     *
     * @param object Objeto a convertir.
     * @return Cadena JSON representando el objeto.
     * @throws JsonProcessingException Si ocurre un error durante la serialización.
     */
    public static String objectJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * Construye una URL combinando una URL base y un endpoint.
     * <p>
     * Ejemplo: base {@code https://api.ejemplo.com}, endpoint {@code /usuarios}
     * → {@code https://api.ejemplo.com/usuarios}
     *
     * @param url      URL base del servicio (e.g. "https://api.example.com").
     * @param endpoint Endpoint a agregar (e.g. "/usuarios").
     * @return Objeto {@link UriComponentsBuilder} con la URL construida.
     */
    public static UriComponentsBuilder obtenerUrl(String url, String endpoint) {
        return UriComponentsBuilder.fromHttpUrl(url).path(endpoint);
    }
}
