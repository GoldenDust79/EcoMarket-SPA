package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Producto;
import cl.ecomarket.ms_productos.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProductoByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    @Transactional
    public Producto createProducto(Producto producto) {
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + producto.getCodigo());
        }
       
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto updateProducto(Long id, Producto productoDetails) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        
        if (!producto.getCodigo().equals(productoDetails.getCodigo()) &&
            productoRepository.existsByCodigo(productoDetails.getCodigo())) {
            throw new IllegalArgumentException("El nuevo código '" + productoDetails.getCodigo() + "' ya está en uso por otro producto.");
        }

        producto.setCodigo(productoDetails.getCodigo());
        producto.setNombre(productoDetails.getNombre());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setCategoria(productoDetails.getCategoria());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setStock(productoDetails.getStock());
        

        return productoRepository.save(producto);
    }

    @Transactional
    public void deleteProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductosByCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    
    @Transactional
    public Producto ajustarStock(String codigoProducto, int cantidad) {
        Producto producto = productoRepository.findByCodigo(codigoProducto)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con código: " + codigoProducto));

        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }
}