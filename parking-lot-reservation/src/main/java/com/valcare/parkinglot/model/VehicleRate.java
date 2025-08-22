package com.valcare.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRate {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vehicle_rate_id")
	private Long vehicleId;
	
	@NotBlank(message = "Vehicle type must not be blank")
	@Column(unique = true, nullable = false)
    private String vehicleType;
	
	@NotNull(message = "Hourly rate is required")
    @Min(value = 0, message = "Hourly rate must be zero or positive")
   	@Column(nullable = false)
    private int hourlyRate;
}
