package cl.ecomarket.ms_productos.repository;

import cl.ecomarket.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Métodos de consulta personalizados (Spring Data JPA se implementan automáticamente)

    /**
     * Encuentra un producto por su código único.
     * @param codigo El código del producto.
     * @return Un Optional que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<Producto> findByCodigo(String codigo);

    /**
     * Encuentra productos por categoría.
     * @param categoria La categoría a buscar.
     * @return Una lista de productos que pertenecen a la categoría especificada.
     */
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    /**
     * Encuentra productos cuyo nombre contenga la cadena especificada (ignorando mayúsculas/minúsculas).
     * @param nombre El término de búsqueda para el nombre del producto.
     * @return Una lista de productos que coinciden con el criterio de búsqueda.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe un producto con el código dado.
     * @param codigo El código del producto a verificar.
     * @return true si existe un producto con ese código, false de lo contrario.
     */
    boolean existsByCodigo(String codigo);
}