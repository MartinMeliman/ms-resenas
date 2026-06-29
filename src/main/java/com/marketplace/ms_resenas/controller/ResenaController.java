package com.marketplace.ms_resenas.controller;

import com.marketplace.ms_resenas.model.Resena;
import com.marketplace.ms_resenas.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Reseñas", description = "Reseñas y calificaciones de productos del marketplace EcoTrade")
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(summary = "Listar reseñas de un producto",
               description = "Retorna todas las reseñas activas de un producto específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida correctamente")
    })
    @GetMapping("/producto/{pid}")
    public List<Resena> porProducto(
            @Parameter(description = "ID del producto") @PathVariable Long pid) {
        return resenaService.obtenerPorProducto(pid);
    }

    @Operation(summary = "Listar reseñas de un usuario",
               description = "Retorna todas las reseñas activas hechas por un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida correctamente")
    })
    @GetMapping("/usuario/{uid}")
    public List<Resena> porUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long uid) {
        return resenaService.obtenerPorUsuario(uid);
    }

    @Operation(summary = "Calcular promedio de rating de un producto",
               description = "Calcula el promedio de calificaciones de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto sin reseñas")
    })
    @GetMapping("/producto/{pid}/promedio")
    public ResponseEntity<Double> promedio(
            @Parameter(description = "ID del producto") @PathVariable Long pid) {
        return resenaService.calcularPromedio(pid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nueva reseña",
               description = "Crea una reseña para un producto. Solo se permite una reseña por usuario/producto.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Ya existe una reseña del usuario para este producto"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping
    public ResponseEntity<Resena> crear(@Valid @RequestBody Resena r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.guardar(r));
    }

    @Operation(summary = "Eliminar reseña (soft delete)",
               description = "Marca la reseña como inactiva sin eliminarla de la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reseña eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la reseña a eliminar") @PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}