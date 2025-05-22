package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.Producto;
import cl.ecomarket.ms_productos.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos") // Ruta base para los endpoints de productos
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {
        List<Producto> productos;
        if (nombre != null && !nombre.isEmpty()) {
            productos = productoService.searchProductosByNombre(nombre);
        } else if (categoria != null && !categoria.isEmpty()) {
            productos = productoService.getProductosByCategoria(categoria);
        } else {
            productos = productoService.getAllProductos();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        return productoService.getProductoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> getProductoByCodigo(@PathVariable String codigo) {
        return productoService.getProductoByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.createProducto(producto);
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @Valid @RequestBody Producto productoDetails) {
        try {
            Producto productoActualizado = productoService.updateProducto(id, productoDetails);
            return ResponseEntity.ok(productoActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        try {
            productoService.deleteProducto(id);
            return ResponseEntity.noContent().build(); // 204 No Content es estándar para delete exitoso
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para ajustar stock
    @PatchMapping("/{codigoProducto}/stock")
    public ResponseEntity<?> ajustarStock(
            @PathVariable String codigoProducto,
            @RequestParam int cantidad // cantidad puede ser positiva (añadir) o negativa (reducir)
    ) {
        try {
            Producto productoActualizado = productoService.ajustarStock(codigoProducto, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}