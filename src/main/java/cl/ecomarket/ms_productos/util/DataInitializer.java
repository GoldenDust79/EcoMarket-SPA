package cl.ecomarket.ms_productos.util;

import cl.ecomarket.ms_productos.model.Permiso;
import cl.ecomarket.ms_productos.model.Rol;
import cl.ecomarket.ms_productos.model.Usuario;
import cl.ecomarket.ms_productos.repository.PermisoRepository;
import cl.ecomarket.ms_productos.repository.RolRepository;
import cl.ecomarket.ms_productos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           PermisoRepository permisoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Ejecutando DataInitializer para cargar datos iniciales...");

        // Crear Permisos (ejemplos)
        Permiso pLeerProductos = crearPermisoSiNoExiste("PRODUCTOS_LEER");
        Permiso pCrearProductos = crearPermisoSiNoExiste("PRODUCTOS_CREAR");
        Permiso pEditarProductos = crearPermisoSiNoExiste("PRODUCTOS_EDITAR");
        Permiso pEliminarProductos = crearPermisoSiNoExiste("PRODUCTOS_ELIMINAR");

        Permiso pGestionarUsuarios = crearPermisoSiNoExiste("USUARIOS_GESTIONAR");
        // ... (otros permisos si los tienes definidos en el Rol.java)

        // Crear Roles y asignarles permisos
        // Asegúrate que los nombres de rol aquí sean los que usarás en SecurityConfig
        // y en CustomUserDetailsService al crear SimpleGrantedAuthority (ej: ROLE_ADMINISTRADOR_SISTEMA)
        Rol adminRol = crearRolSiNoExiste("ADMINISTRADOR_SISTEMA",
                new HashSet<>(Arrays.asList(
                        pLeerProductos, pCrearProductos, pEditarProductos, pEliminarProductos,
                        pGestionarUsuarios // ... y otros permisos para admin
                )));

        Rol gerenteRol = crearRolSiNoExiste("GERENTE_TIENDA",
                new HashSet<>(Arrays.asList(
                        pLeerProductos, pCrearProductos, pEditarProductos
                )));

        Rol empleadoRol = crearRolSiNoExiste("EMPLEADO_VENTAS",
                new HashSet<>(Arrays.asList(
                        pLeerProductos
                )));
        
        Rol logisticaRol = crearRolSiNoExiste("LOGISTICA",
                 new HashSet<>(Arrays.asList(
                        pLeerProductos 
                )));


        // Crear Usuario Administrador
        crearUsuarioSiNoExiste("admin", "Administrador del Sistema", "admin@ecomarket.cl", "admin123", adminRol);
        crearUsuarioSiNoExiste("gerente01", "Gerente Ejemplo Uno", "gerente01@ecomarket.cl", "gerente123", gerenteRol);
        crearUsuarioSiNoExiste("empleado01", "Empleado Ejemplo Uno", "empleado01@ecomarket.cl", "empleado123", empleadoRol);
        crearUsuarioSiNoExiste("logistica01", "Logistica Ejemplo Uno", "logistica01@ecomarket.cl", "logistica123", logisticaRol);


        log.info("DataInitializer finalizado.");
    }

    private void crearUsuarioSiNoExiste(String username, String nombreCompleto, String email, String password, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario user = new Usuario();
            user.setUsername(username);
            user.setNombreCompleto(nombreCompleto);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setActivo(true);
            user.setRoles(new HashSet<>(Set.of(rol))); // Set.of para crear un Set inmutable
            usuarioRepository.save(user);
            log.info("Usuario '{}' creado.", username);
        } else {
            log.info("Usuario '{}' ya existe.", username);
        }
    }

    private Rol crearRolSiNoExiste(String nombreRol, Set<Permiso> permisos) {
        // Los nombres de rol se guardan en mayúsculas para consistencia
        String nombreRolUpper = nombreRol.toUpperCase().replace(" ", "_");
        Optional<Rol> rolOpt = rolRepository.findByNombre(nombreRolUpper);
        if (rolOpt.isEmpty()) {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombreRolUpper);
            if (permisos != null && !permisos.isEmpty()) {
                nuevoRol.setPermisos(new HashSet<>(permisos)); // Asegurar que sea mutable si se modifica después
            } else {
                nuevoRol.setPermisos(new HashSet<>());
            }
            rolRepository.save(nuevoRol);
            Rol rolGuardado = rolRepository.save(nuevoRol); // Guardar y obtener la entidad con ID
            log.info("Rol '{}' creado con ID: {}.", rolGuardado.getNombre(), rolGuardado.getId());
            return rolGuardado;
        }
        Rol rolExistente = rolOpt.get();
        // Opcional: actualizar permisos si el rol ya existe y los permisos son diferentes
        // if (permisos != null && !rolExistente.getPermisos().equals(permisos)) {
        //     rolExistente.setPermisos(new HashSet<>(permisos));
        //     rolRepository.save(rolExistente);
        //     log.info("Permisos actualizados para el rol '{}'.", nombreRolUpper);
        // }
        return rolExistente;
    }

    private Permiso crearPermisoSiNoExiste(String nombrePermiso) {
        // Los nombres de permiso se guardan en mayúsculas para consistencia
        String nombrePermisoUpper = nombrePermiso.toUpperCase().replace(" ", "_");
        Optional<Permiso> permisoOpt = permisoRepository.findByNombre(nombrePermisoUpper);
        if (permisoOpt.isEmpty()) {
            Permiso nuevoPermiso = new Permiso();
            nuevoPermiso.setNombre(nombrePermisoUpper);
            permisoRepository.save(nuevoPermiso);
            log.info("Permiso '{}' creado.", nombrePermisoUpper);
            return nuevoPermiso;
        }
        return permisoOpt.get();
    }
}