package com.ucacue.udipsai.infrastructure.security;

import com.ucacue.udipsai.modules.especialistas.domain.Especialista;
import com.ucacue.udipsai.modules.especialistas.repository.EspecialistaRepository;
import com.ucacue.udipsai.modules.pasante.domain.Pasante;
import com.ucacue.udipsai.modules.pasante.repository.PasanteRepository;
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
    private EspecialistaRepository especialistaRepository;

    @Autowired
    private PasanteRepository pasanteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Especialista> especialista = especialistaRepository.findByCedula(username);
        if (especialista.isPresent()) {
            return new User(especialista.get().getCedula(), especialista.get().getContrasenia(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESPECIALISTA")));
        }

        Optional<Pasante> pasante = pasanteRepository.findByCedula(username);
        if (pasante.isPresent()) {
            return new User(pasante.get().getCedula(), pasante.get().getContrasenia(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASANTE")));
        }

        throw new UsernameNotFoundException("Usuario no encontrado con cédula: " + username);
    }
}
