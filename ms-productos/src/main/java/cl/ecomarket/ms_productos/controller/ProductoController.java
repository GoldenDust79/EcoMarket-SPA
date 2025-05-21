package cl.ecomarket.ms_productos.controller;

import cl.ecomarket.ms_productos.model.Producto;
import cl.ecomarket.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> getAll() {
        return productoService.getAll();
    }

    @GetMapping("/{id}")
    public Producto getById(@PathVariable Long id) {
        return productoService.getById(id);
    }

    @PostMapping
    public Producto save(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    @GetMapping("/total")
    public int totalProductos() {
        return productoService.totalProductos();
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> porCategoria(@PathVariable String categoria) {
        return productoService.productosPorCategoria(categoria);
    }

    @GetMapping("/masCaro")
    public Producto productoMasCaro() {
        return productoService.productoMasCaro();
    }

    @GetMapping("/masBarato")
    public Producto productoMasBarato() {
        return productoService.productoMasBarato();
    }

    @GetMapping("/ordenados")
    public List<Producto> productosOrdenados() {
        return productoService.productosOrdenadosPorPrecio();
    }
}
