package com.marketplace.ms_resenas.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Entity
@Table(name="resenas", uniqueConstraints=@UniqueConstraint(columnNames={"usuario_id","producto_id"}))
public class Resena {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotNull @Column(name="producto_id", nullable=false) private Long productoId;
    @NotNull @Column(name="usuario_id", nullable=false) private Long usuarioId;
    @NotNull @Min(1) @Max(5) @Column(nullable=false) private Integer rating;
    @Size(max=1000) @Column(length=1000) private String comentario;
    @Column(nullable=false) private boolean activa=true;
    @Column(name="creado_en", updatable=false) private LocalDateTime creadoEn;
    @PrePersist public void pre(){ creadoEn=LocalDateTime.now(); }
}
