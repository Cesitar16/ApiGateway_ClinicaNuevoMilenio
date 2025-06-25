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

        // CAMBIO 2: La ruta a reemplazar
        String originalPath = request.getRequestURI().replace("/api/proxy/pabellones", "");

        // CAMBIO 3: La URL del microservicio de destino (pabellones-api)
        String targetUrl = "http://localhost:8003/api/pabellones" + originalPath;

        HttpHeaders cleanHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                cleanHeaders.put(key, value);
            }
        });
        cleanHeaders.setContentType(MediaType.APPLICATION_JSON);

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