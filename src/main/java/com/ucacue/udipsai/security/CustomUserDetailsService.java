package com.ucacue.udipsai.security;

import com.ucacue.udipsai.modules.especialistas.Especialista;
import com.ucacue.udipsai.modules.especialistas.EspecialistaRepositorio;
import com.ucacue.udipsai.modules.pasante.Pasante;
import com.ucacue.udipsai.modules.pasante.PasanteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;

    @Autowired
    private PasanteRepositorio pasanteRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primero busca en Especialistas
        // Asumiendo que 'cedula' o 'email' (si existe) es el username. Usaremos 'cedula' basado en la entidad.
        Optional<Especialista> especialista = especialistaRepositorio.findByCedula(username);
        if (especialista.isPresent()) {
            return new User(especialista.get().getCedula(), especialista.get().getContrasenia(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESPECIALISTA")));
        }

        // Si no en Pasantes
        Optional<Pasante> pasante = pasanteRepositorio.findByCedula(username);
        if (pasante.isPresent()) {
            return new User(pasante.get().getCedula(), pasante.get().getContrasenia(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASANTE")));
        }

        throw new UsernameNotFoundException("Usuario no encontrado con cédula: " + username);
    }
}
