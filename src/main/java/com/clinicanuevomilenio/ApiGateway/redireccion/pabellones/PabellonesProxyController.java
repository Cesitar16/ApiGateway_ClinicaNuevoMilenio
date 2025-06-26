package com.clinicanuevomilenio.ApiGateway.redireccion.pabellones;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy/pabellones") // <-- CAMBIO 1: La nueva ruta base
@RequiredArgsConstructor
public class PabellonesProxyController {

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
    public ResponseEntity<?> proxyPabellones(HttpServletRequest request,
                                             @RequestBody(required = false) String body,
                                             @RequestHeader HttpHeaders headers) {

        // Obtener la ruta, ej: "/filtro"
        String originalPath = request.getRequestURI().replace("/api/proxy/pabellones", "");

        // --- CORRECCIÓN AQUÍ ---
        // Obtener la cadena de consulta, ej: "estadoId=1"
        String queryString = request.getQueryString();

        // Construir la URL de destino completa
        String targetUrl = "http://localhost:8003/api/pabellones" + originalPath;
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString; // Añadir los parámetros si existen
        }
        // --- FIN DE LA CORRECCIÓN ---


        HttpHeaders cleanHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                cleanHeaders.put(key, value);
            }
        });
        cleanHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, cleanHeaders);

        try {
            // Ahora la petición a restTemplate incluye la URL con los parámetros correctos
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.valueOf(request.getMethod()), entity, String.class);
            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Error inesperado en el API Gateway\", \"detalle\": \"" + ex.getMessage() + "\"}");
        }
    }
}