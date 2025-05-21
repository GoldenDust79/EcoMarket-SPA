package cl.ecomarket.ms_productos.service;

import cl.ecomarket.ms_productos.model.Producto;
import cl.ecomarket.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getAll() {
        return productoRepository.findAll();
    }

    public Producto getById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto getByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(Producto producto) {
        return productoRepository.save(producto);
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    public int totalProductos() {
        return productoRepository.findAll().size();
    }

    public List<Producto> productosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Producto productoMasCaro() {
        return productoRepository.findAll()
                .stream()
                .max(Comparator.comparingDouble(Producto::getPrecio))
                .orElse(null);
    }

    public Producto productoMasBarato() {
        return productoRepository.findAll()
                .stream()
                .min(Comparator.comparingDouble(Producto::getPrecio))
                .orElse(null);
    }

    public List<Producto> productosOrdenadosPorPrecio() {
        return productoRepository.findAll()
                .stream()
                .sorted(Comparator.comparingDouble(Producto::getPrecio))
                .toList();
    }
}
