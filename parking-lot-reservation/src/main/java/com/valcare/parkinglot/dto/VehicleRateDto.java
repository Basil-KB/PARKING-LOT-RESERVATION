package com.valcare.parkinglot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRateDto {
	
	@NotBlank(message = "Vehicle type must not be blank")
	private String vehicleType;
	
	@NotNull(message = "Hourly rate is required")
    @Min(value = 0, message = "Hourly rate must be zero or positive")
    private int hourlyRate;
}
