package cl.ecomarket.ms_productos.config;

import cl.ecomarket.ms_productos.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true) // Habilita diferentes tipos de anotaciones de seguridad
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs RESTful sin estado
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Para APIs sin estado (JWT)
            .authorizeHttpRequests(authz -> authz
                // Permitir acceso público a Actuator health y Swagger/OpenAPI si lo usas
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Reglas para PRODUCTOS
                // Cualquiera autenticado puede leer productos
                .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/**").authenticated()
                // Solo ciertos roles pueden crear, actualizar o eliminar productos
                .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasAnyRole("ADMINISTRADOR_SISTEMA", "GERENTE_TIENDA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasAnyRole("ADMINISTRADOR_SISTEMA", "GERENTE_TIENDA")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/**").hasAnyRole("ADMINISTRADOR_SISTEMA", "GERENTE_TIENDA") // Ejemplo: para actualizar stock
                .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMINISTRADOR_SISTEMA")

                // Reglas para USUARIOS, ROLES, PERMISOS (solo el administrador del sistema)
                .requestMatchers("/api/v1/usuarios/**").hasRole("ADMINISTRADOR_SISTEMA")
                .requestMatchers("/api/v1/roles/**").hasRole("ADMINISTRADOR_SISTEMA")
                .requestMatchers("/api/v1/permisos/**").hasRole("ADMINISTRADOR_SISTEMA")
                // Excepción: permitir que un usuario se registre (si tienes un endpoint de registro público)
                // .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/registro").permitAll()


                // Si tienes un endpoint de autenticación para generar tokens JWT
                // .requestMatchers("/api/v1/auth/**").permitAll()

                // Cualquier otra solicitud debe estar autenticada
                .anyRequest().authenticated()
            )
            // Configura la autenticación básica HTTP. Esto es bueno para pruebas iniciales.
            // Para producción, probablemente usarás un filtro para JWT.
            .httpBasic(Customizer.withDefaults());
            // Si quieres usar formLogin (más para apps web tradicionales):
            // .formLogin(Customizer.withDefaults());

        return http.build();
    }
}