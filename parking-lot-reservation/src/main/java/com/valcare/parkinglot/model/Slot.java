package com.valcare.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;
    
    @NotBlank(message = "Slot number can't be blank")
    private String slotNumber;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_rate_id", nullable = false)
    private VehicleRate vehicleRate;
    
    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
    
    @Version // For optimistic locking
    private Integer version;
	
	
}
