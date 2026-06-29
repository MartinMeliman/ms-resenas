package com.marketplace.ms_resenas.controller;
import com.marketplace.ms_resenas.model.Resena;
import com.marketplace.ms_resenas.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/resenas") @RequiredArgsConstructor
public class ResenaController {
    private final ResenaService resenaService;
    @GetMapping("/producto/{pid}") public List<Resena> porProducto(@PathVariable Long pid){ return resenaService.obtenerPorProducto(pid); }
    @GetMapping("/usuario/{uid}") public List<Resena> porUsuario(@PathVariable Long uid){ return resenaService.obtenerPorUsuario(uid); }
    @GetMapping("/producto/{pid}/promedio") public ResponseEntity<Double> promedio(@PathVariable Long pid){ return resenaService.calcularPromedio(pid).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
    @PostMapping public ResponseEntity<Resena> crear(@Valid @RequestBody Resena r){ return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.guardar(r)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ resenaService.eliminar(id); return ResponseEntity.noContent().build(); }
}
