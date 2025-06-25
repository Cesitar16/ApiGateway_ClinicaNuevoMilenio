package com.clinicanuevomilenio.ApiGateway.redireccion.solicitudes;

import jakarta.servlet.http.HttpServletRequest; //
import lombok.RequiredArgsConstructor; //
import org.springframework.http.*; //
import org.springframework.web.bind.annotation.*; //
import org.springframework.web.client.HttpClientErrorException; //
import org.springframework.web.client.HttpServerErrorException; //
import org.springframework.web.client.RestTemplate; //

import java.util.Map; //

@RestController
@RequestMapping("/api/proxy/solicitudes") // <-- Ruta base del proxy
@RequiredArgsConstructor
public class SolicitudesProxyController {

    private final RestTemplate restTemplate; //

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }) //
    public ResponseEntity<?> proxySolicitudes(HttpServletRequest request, //
                                              @RequestBody(required = false) String body) { //

        String originalPath = request.getRequestURI().replace("/api/proxy/solicitudes", ""); //

        // Apunta al puerto 8005 donde correrá la API de solicitud-servicio
        String targetUrl = "http://localhost:8005/api/solicitudes-servicio" + originalPath; //

        // Reenvío de cabeceras de autenticación (esencial para que los otros servicios sepan quién hace la petición)
        HttpHeaders headers = new HttpHeaders(); //
        if (request.getHeader("Authorization") != null) { //
            headers.add("Authorization", request.getHeader("Authorization")); //
        }
        if (request.getHeader("X-User-Id") != null) { //
            headers.add("X-User-Id", request.getHeader("X-User-Id")); //
        }
        headers.setContentType(MediaType.APPLICATION_JSON); //


        HttpEntity<String> entity = new HttpEntity<>(body, headers); //

        try {
            ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.valueOf(request.getMethod()), entity, String.class); //
            return ResponseEntity.status(response.getStatusCode()) //
                    .contentType(MediaType.APPLICATION_JSON) //
                    .body(response.getBody()); //
        } catch (HttpClientErrorException | HttpServerErrorException ex) { //
            return ResponseEntity.status(ex.getStatusCode()) //
                    .contentType(MediaType.APPLICATION_JSON) //
                    .body(ex.getResponseBodyAsString()); //
        } catch (Exception ex) { //
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //
                    .contentType(MediaType.APPLICATION_JSON) //
                    .body("{\"error\": \"Error inesperado en el API Gateway\", \"detalle\": \"" + ex.getMessage() + "\"}"); //
        }
    }
}