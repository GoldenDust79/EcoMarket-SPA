package cl.ecomarket.ms_productos.repository;

import cl.ecomarket.ms_productos.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductoRepository {
    private List<Producto> listaProductos = new ArrayList<>();

    public ProductoRepository() {
        listaProductos.add(new Producto(1L, "PRD001", "Jabón Ecológico", "Jabón biodegradable hecho con aceites naturales", "Limpieza", 2500.0, 100));
        listaProductos.add(new Producto(2L, "PRD002", "Shampoo Natural", "Shampoo sin sulfatos ni parabenos", "Cosméticos", 4500.0, 50));
        listaProductos.add(new Producto(3L, "PRD003", "Café Orgánico", "Café cultivado sin pesticidas", "Alimentos", 6000.0, 200));
        
    }

    public List<Producto> findAll() {
        return listaProductos;
    }

    public Producto findById(Long id) {
    if (id == null) return null;
    return listaProductos.stream()
        .filter(p -> p.getId() == id)
        .findFirst()
        .orElse(null);
    }


    public Producto findByCodigo(String codigo) {
        return listaProductos.stream().filter(p -> p.getCodigo().equals(codigo)).findFirst().orElse(null);
    }

    public List<Producto> findByCategoria(String categoria) {
        List<Producto> result = new ArrayList<>();
        for (Producto p : listaProductos) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                result.add(p);
            }
        }
        return result;
    }

    public Producto save(Producto producto) {
        
        if (producto.getId() == 0) { 
            long nuevoId = listaProductos.stream()
                .mapToLong(Producto::getId)
                .max()
                .orElse(0) + 1;
            producto.setId(nuevoId);
            listaProductos.add(producto);
            return producto;
        } else {
            for (int i = 0; i < listaProductos.size(); i++) {
                if (listaProductos.get(i).getId() == producto.getId()) {
                    listaProductos.set(i, producto);
                    return producto;
                }
            }
            // Si no existía el id, agregarlo
            listaProductos.add(producto);
            return producto;
        }
    }

    public void deleteById(Long id) {
        if (id == null) return;
        listaProductos.removeIf(p -> p.getId() == id);
    }

}
