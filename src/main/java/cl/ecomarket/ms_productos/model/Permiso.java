package cl.ecomarket.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permisos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String nombre; // Ejemplo: "USUARIOS_CREAR", "USUARIOS_LEER", "PRODUCTOS_GESTIONAR"

    // @Column
    // private String descripcion;
}