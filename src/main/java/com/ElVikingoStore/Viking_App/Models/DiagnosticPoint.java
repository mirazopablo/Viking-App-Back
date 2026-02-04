package com.ElVikingoStore.Viking_App.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Schema(description = "Punto de diagnóstico", title = "DiagnosticPoint")
@Entity
@Table(name = "diagnostic_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticPoint {
        @Schema(description = "Identificador único del punto de diagnóstico", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", updatable = false, nullable = false)
        private UUID id;
        @Schema(description = "Orden de trabajo asociada", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
        // Relación con la tabla de WorkOrder
        @ManyToOne
        @JoinColumn(name = "work_order_id", nullable = false)
        @JsonBackReference
        private WorkOrder workOrder; // Asegura la relación con la entidad WorkOrder modificada

        @Schema(description = "Fecha y hora del punto de diagnóstico", example = "2021-12-31T23:59:59.999Z", requiredMode = Schema.RequiredMode.REQUIRED)
        @Column(name = "timestamp", nullable = false)
        private LocalDateTime timestamp;
        @Schema(description = "Descripción del punto de diagnóstico", example = "Pantalla rota", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 200)
        @Column(name = "description", nullable = false)
        private String description;
        @Schema(description = "Foto del punto de diagnóstico", example = "https://www.example.com/image.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        // Archivos multimedia asociados con este punto de diagnóstico
        @ElementCollection(fetch = FetchType.EAGER)
        @Column(name = "multimedia_files")
        @Builder.Default
        private List<String> multimediaFiles = new ArrayList<>();
        @Schema(description = "Notas del punto de diagnóstico", example = "Se requiere reemplazo de pantalla", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 200)
        @Column(name = "notes")
        private String notes;

        @PrePersist
        protected void onCreate() {
                if (this.timestamp == null) {
                        this.timestamp = LocalDateTime.now();
                }
        }
}
