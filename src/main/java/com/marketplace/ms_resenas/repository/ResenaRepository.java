package com.marketplace.ms_resenas.repository;
import com.marketplace.ms_resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface ResenaRepository extends JpaRepository<Resena,Long> {
    List<Resena> findByProductoIdAndActivaTrue(Long productoId);
    List<Resena> findByUsuarioId(Long usuarioId);
    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);
    @Query("SELECT AVG(r.rating) FROM Resena r WHERE r.productoId=:productoId AND r.activa=true") Double calcularPromedio(Long productoId);
}
