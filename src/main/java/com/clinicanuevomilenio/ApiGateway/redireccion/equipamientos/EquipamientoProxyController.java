package com.clinicanuevomilenio.ApiGateway.redireccion.equipamientos;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy/equipamiento") // <-- CAMBIO 1: La nueva ruta base del proxy
@RequiredArgsConstructor
public class EquipamientoProxyController {

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
    public ResponseEntity<?> proxyEquipamiento(HttpServletRequest request,
                                               @RequestBody(required = false) String body,
                                               @RequestHeader HttpHeaders headers) {

        // CAMBIO 2: La ruta a reemplazar
        String originalPath = request.getRequestURI().replace("/api/proxy/equipamiento", "");

        // CAMBIO 3: La URL del microservicio de destino (equipamiento-api en el puerto 8083)
        String targetUrl = "http://localhost:8004/api/equipamiento" + originalPath;

        // El resto de la lógica es idéntica a tus otros proxies
        HttpHeaders cleanHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                cleanHeaders.put(key, value);
            }
        });
        cleanHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Reenvío de cabeceras de usuario si existen
        if (request.getHeader("X-User-Username") != null) {
            cleanHeaders.add("X-User-Username", request.getHeader("X-User-Username"));
        }
        if (request.getHeader("X-User-Roles") != null) {
            cleanHeaders.add("X-User-Roles", request.getHeader("X-User-Roles"));
        }
        if (request.getHeader("X-User-Id") != null) {
            cleanHeaders.add("X-User-Id", request.getHeader("X-User-Id"));
        }

        HttpEntity<String> entity = new HttpEntity<>(body, cleanHeaders);

        try {
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