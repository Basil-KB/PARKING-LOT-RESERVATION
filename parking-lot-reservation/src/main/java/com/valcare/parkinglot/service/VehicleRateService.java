package com.valcare.parkinglot.service;

import com.valcare.parkinglot.dto.VehicleRateDto;
import com.valcare.parkinglot.model.VehicleRate;
import jakarta.validation.Valid;

public interface VehicleRateService {
	 	VehicleRate createVehicleRate(@Valid VehicleRate vehicleRate);
	    VehicleRate updateVehicleRate(Long id, @Valid VehicleRateDto updatedVehicleRateDto);
}
