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

@Service("customUserDetailsService") // Dale un nombre al bean si quieres ser específico
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
            // Puedes lanzar una excepción específica si quieres manejarla diferente,
            // o UsernameNotFoundException también funciona para indicar que el usuario no es válido para login.
            // throw new DisabledException("La cuenta del usuario está desactivada: " + usernameOrEmail);
            throw new UsernameNotFoundException("La cuenta del usuario está desactivada: " + usernameOrEmail);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Añadir Roles como authorities (Spring Security les antepone "ROLE_" por defecto)
        // Asegúrate que los nombres de los roles en la BD NO tengan "ROLE_" al principio
        // Ejemplo: "ADMINISTRADOR_SISTEMA", "GERENTE_TIENDA"
        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre())); // rol.getNombre() ya debería estar en el formato correcto
                                                                                    // si DataInitializer lo guarda así (ej: ADMINISTADOR_SISTEMA)
            // Si también quieres usar permisos directamente como authorities:
            // rol.getPermisos().forEach(permiso -> {
            //     authorities.add(new SimpleGrantedAuthority(permiso.getNombre())); // Asumimos que permiso.getNombre() también está formateado
            // });
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), // Usamos email como username para Spring Security UserDetails
                usuario.getPassword(), // Esta debe ser la contraseña HASHEADA de la BD
                usuario.isActivo(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}