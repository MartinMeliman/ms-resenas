package com.marketplace.ms_resenas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marketplace.ms_resenas.client.ProductoClient;
import com.marketplace.ms_resenas.model.Resena;
import com.marketplace.ms_resenas.repository.ResenaRepository;
import com.marketplace.ms_resenas.service.ResenaService;

import feign.FeignException;

/**
 * Pruebas unitarias para ResenaService.
 * El FeignClient (ProductoClient) se simula con @Mock.
 * Patrón Given/When/Then con Mockito (sin BD ni red real).
 */
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock private ResenaRepository resenaRepository;
    @Mock private ProductoClient productoClient;
    @InjectMocks private ResenaService resenaService;

    private Resena resena;

    @BeforeEach
    void setUp() {
        resena = new Resena();
        resena.setId(1L);
        resena.setProductoId(5L);
        resena.setUsuarioId(1L);
        resena.setRating(4);
        resena.setComentario("Buen producto");
        resena.setActiva(true);
    }

    @Test
    @DisplayName("obtenerPorProducto: debería retornar reseñas activas del producto")
    void shouldReturnReviewsByProduct() {
        // GIVEN
        when(resenaRepository.findByProductoIdAndActivaTrue(5L)).thenReturn(List.of(resena));
        // WHEN
        List<Resena> resultado = resenaService.obtenerPorProducto(5L);
        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(4, resultado.get(0).getRating());
    }

    @Test
    @DisplayName("calcularPromedio: debería retornar el promedio de rating del producto")
    void shouldCalculateAverageRating() {
        // GIVEN
        when(resenaRepository.calcularPromedio(5L)).thenReturn(4.5);
        // WHEN
        Optional<Double> resultado = resenaService.calcularPromedio(5L);
        // THEN
        assertTrue(resultado.isPresent());
        assertEquals(4.5, resultado.get());
    }

    @Test
    @DisplayName("guardar: debería crear la reseña cuando el producto existe y no hay duplicado")
    void shouldSaveReviewSuccessfully() {
        // GIVEN — no existe reseña previa, producto existe via Feign
        when(resenaRepository.existsByProductoIdAndUsuarioId(5L, 1L)).thenReturn(false);
        when(productoClient.obtenerPorId(5L)).thenReturn(Map.of("id", 5L));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        // WHEN
        Resena resultado = resenaService.guardar(resena);
        // THEN
        assertNotNull(resultado);
        assertEquals(4, resultado.getRating());
        verify(resenaRepository, times(1)).save(resena);
    }

    @Test
    @DisplayName("guardar: debería lanzar excepción cuando ya existe reseña del usuario")
    void shouldThrowWhenReviewDuplicated() {
        // GIVEN — regla de negocio: una reseña por usuario/producto
        when(resenaRepository.existsByProductoIdAndUsuarioId(5L, 1L)).thenReturn(true);
        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.guardar(resena));
        assertTrue(ex.getMessage().contains("Ya existe una resena"));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    @DisplayName("guardar: debería lanzar excepción cuando el producto no existe")
    void shouldThrowWhenProductNotFound() {
        // GIVEN — no hay reseña previa pero producto lanza 404 via Feign
        when(resenaRepository.existsByProductoIdAndUsuarioId(5L, 1L)).thenReturn(false);
        when(productoClient.obtenerPorId(5L)).thenThrow(FeignException.NotFound.class);
        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> resenaService.guardar(resena));
        assertTrue(ex.getMessage().contains("no existe"));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminar: debería marcar la reseña como inactiva (soft delete)")
    void shouldDeactivateReview() {
        // GIVEN
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        // WHEN
        resenaService.eliminar(1L);
        // THEN
        assertFalse(resena.isActiva());
        verify(resenaRepository, times(1)).save(resena);
    }
}
