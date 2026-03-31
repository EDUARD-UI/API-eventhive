package com.example.demo.security.Users;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + correo);
        }

        // Convertir el rol a formato Spring Security
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        String rolNombre = usuario.getRol().getNombre(); // "organizador", "administrador", "cliente"
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rolNombre.toUpperCase()));

        return new User(
            usuario.getCorreo(),
            usuario.getClave(),
            authorities
        );
    }
}