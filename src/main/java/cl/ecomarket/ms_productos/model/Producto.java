package cl.ecomarket.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto") // Hibernate creará esta tabla
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado de long a Long para que pueda ser null si es necesario y es más estándar en JPA

    @NotBlank(message = "El código del producto no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres.")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres.")
    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT") // Para descripciones más largas
    private String descripcion;

    @Size(max = 100)
    @Column(length = 100)
    private String categoria;

    @NotNull(message = "El precio no puede ser nulo.")
    @Min(value = 0, message = "El precio no puede ser negativo.")
    @Column(nullable = false)
    private Double precio; // Cambiado de double a Double

    @NotNull(message = "El stock no puede ser nulo.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    @Column(nullable = false)
    private Integer stock; // Cambiado de int a Integer
}