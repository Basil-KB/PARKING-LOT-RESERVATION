package com.valcare.parkinglot.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;
	
	@NotBlank
    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$",
             message = "Vehicle number must follow 'XX00XX0000' format")
    private String vehicleNumber;
	
	@ManyToOne
    @JoinColumn(name = "vehicle_rate_id", nullable = false)
    private VehicleRate vehicleRate;
	
	@NotNull
    private LocalDateTime startTime;
	
    @NotNull
    private LocalDateTime endTime;
    
    @Min(0)
    private long totalFee;
    @Version // For optimistic locking
    private Integer version;
}
