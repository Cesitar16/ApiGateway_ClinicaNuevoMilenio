package com.clinicanuevomilenio.ApiGateway.redireccion.pabellones;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy/imagenes")
@RequiredArgsConstructor
public class FileProxyController {

    private final RestTemplate restTemplate;

    @GetMapping(value = "/**") // Las imágenes solo se obtienen con GET
    public ResponseEntity<?> proxyImagenes(HttpServletRequest request) {

        // Construye la ruta de destino en la pabellones-api
        String originalPath = request.getRequestURI().replace("/api/proxy/imagenes", "");
        String targetUrl = "http://localhost:8003/imagenes" + originalPath; // Apunta al FileController de pabellones-api

        try {
            // --- CAMBIO IMPORTANTE AQUÍ ---
            // Le pedimos a RestTemplate que nos devuelva la respuesta como un array de bytes (byte[]),
            // que es la forma de manejar datos binarios como las imágenes.
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    targetUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY, // No hay cuerpo en una petición GET
                    byte[].class
            );

            // Reenviamos la respuesta completa al cliente, incluyendo las cabeceras originales
            // (como Content-Type: image/jpeg) y el cuerpo (los bytes de la imagen).
            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Reenviar el error si el microservicio de pabellones devuelve uno (ej. 404 Not Found)
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsByteArray());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error en el Gateway al procesar la imagen.");
        }
    }
}