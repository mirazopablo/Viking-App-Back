package com.ElVikingoStore.Viking_App.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Schema(description = "Modelo de dispositivo", title = "Device")
@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "id", updatable = false, nullable = false)
        private UUID id;

        @Column(name = "device_type", nullable = false)
        private String type;

        @Column(name = "device_brand", nullable = false)
        private String brand;

        @Column(name = "device_model", nullable = false)
        private String model;

        @Column(name = "serial_number", nullable = false)
        private String serialNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        @JsonIgnore
        private User user;

        // Snapshot
        @Column(name = "user_name", nullable = false)
        private String userName;

        @Column(name = "user_dni", nullable = false)
        private Integer userDni;

        public Device(UUID id) {
                this.id = id;
        }
}
