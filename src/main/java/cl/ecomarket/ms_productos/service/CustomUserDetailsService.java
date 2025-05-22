package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("customUserDetailsService") 
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> usuarioRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username o email: " + usernameOrEmail)));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("La cuenta del usuario está desactivada: " + usernameOrEmail);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();


        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre())); // rol.getNombre() 
                                                                                    // si DataInitializer lo guarda así (ej: ADMINISTADOR_SISTEMA)

        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), 
                usuario.getPassword(), 
                usuario.isActivo(),
                true, 
                true, 
                true, 
                authorities
        );
    }
}