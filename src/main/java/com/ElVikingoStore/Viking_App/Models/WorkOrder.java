package com.ElVikingoStore.Viking_App.Models;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Schema(description = "Orden de trabajo", title = "WorkOrder")
@Entity
@Table(name = "work_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", updatable = false, nullable = false)
        private UUID id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "client_id", nullable = false)
        private User client;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "staff_id", nullable = false)
        private User staff;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "device_id", nullable = false)
        private Device device;

        @Column(name = "client_name", nullable = false)
        private String clientName;

        @Column(name = "client_dni", nullable = false)
        private Integer clientDni;

        @Column(name = "device_brand", nullable = false)
        private String deviceBrand;

        @Column(name = "device_model", nullable = false)
        private String deviceModel;

        @Column(name = "device_serial_number", nullable = false)
        private String deviceSerialNumber;

        @Column(name = "issue_description", nullable = false, length = 200)
        private String issueDescription;

        @Column(name = "repair_status", nullable = false)
        private String repairStatus;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private List<DiagnosticPoint> diagnosticPoints;

        @PrePersist
        protected void onCreate() {
                this.createdAt = LocalDateTime.now();
        }
}
