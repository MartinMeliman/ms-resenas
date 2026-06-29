package com.marketplace.ms_resenas.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
// Verifica que el producto exista antes de guardar la resena
@FeignClient(name="ms-productos", url="${ms.productos.url}")
public interface ProductoClient {
    @GetMapping("/api/productos/{id}") Map<String,Object> obtenerPorId(@PathVariable Long id);
}
