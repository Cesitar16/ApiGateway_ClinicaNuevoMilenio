package com.clinicanuevomilenio.ApiGateway.jwt.security;

import com.clinicanuevomilenio.ApiGateway.jwt.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

   @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String nombreRol = usuario.getRol().getNombre(); // <-- extrae el nombre del objeto Rol
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + nombreRol.toUpperCase()) // ROLE_CIRUJANO, etc.
        );
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Aquí podrías agregar lógica si quieres manejar expiración
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Aquí podrías agregar lógica si quieres bloquear cuentas
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Aquí podrías agregar lógica si quieres manejar expiración de credenciales
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getEstado());
    }


    // Opcional: Exponer idUsuario
    public Integer getIdUsuario() {
        return usuario.getIdUsuario();
    }
}
