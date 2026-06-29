package com.marketplace.ms_resenas.service;
import com.marketplace.ms_resenas.client.ProductoClient;
import com.marketplace.ms_resenas.model.Resena;
import com.marketplace.ms_resenas.repository.ResenaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
@Slf4j @Service @RequiredArgsConstructor
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final ProductoClient productoClient;
    public List<Resena> obtenerPorProducto(Long pid){ return resenaRepository.findByProductoIdAndActivaTrue(pid); }
    public List<Resena> obtenerPorUsuario(Long uid){ return resenaRepository.findByUsuarioId(uid); }
    public Optional<Double> calcularPromedio(Long pid){ return Optional.ofNullable(resenaRepository.calcularPromedio(pid)); }
    public Resena guardar(Resena r){
        if(resenaRepository.existsByProductoIdAndUsuarioId(r.getProductoId(),r.getUsuarioId())) throw new RuntimeException("Ya existe una resena de este usuario para este producto");
        validarProducto(r.getProductoId());
        log.info("Guardando resena. Producto: {}, Rating: {}", r.getProductoId(), r.getRating());
        return resenaRepository.save(r);
    }
    public void eliminar(Long id){ resenaRepository.findById(id).ifPresent(r->{ r.setActiva(false); resenaRepository.save(r); }); }
    private void validarProducto(Long pid){
        try{ productoClient.obtenerPorId(pid); }
        catch(FeignException.NotFound e){ throw new RuntimeException("Producto ID "+pid+" no existe en ms-productos"); }
        catch(FeignException e){ throw new RuntimeException("No se puede conectar con ms-productos"); }
    }
}
