package com.valcare.parkinglot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.valcare.parkinglot.dto.VehicleRateDto;
import com.valcare.parkinglot.model.VehicleRate;
import com.valcare.parkinglot.repository.VehicleRateRepository;

@Service 
public class VehicleRateServiceImpl	implements VehicleRateService {

	    @Autowired
	    private VehicleRateRepository vehicleRateRepository;
	    
	    // Add new vehicle rate 
	    @Override
	    public VehicleRate createVehicleRate(VehicleRate vehicleRate) {
	        // Optional: Check if vehicleType already exists to avoid duplicates
	        vehicleRateRepository.findByVehicleType(vehicleRate.getVehicleType())
	                .ifPresent(vehicle -> {
	                    throw new IllegalArgumentException("Vehicle type already exists");
	                });
	        return vehicleRateRepository.save(vehicleRate);
	    }

	    //Update the existing Data
	    @Override
	    public VehicleRate updateVehicleRate(Long id, VehicleRateDto updatedVehicleRate) {
	        VehicleRate existing = vehicleRateRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Vehicle rate not found with id " + id));

	        existing.setVehicleType(updatedVehicleRate.getVehicleType());
	        existing.setHourlyRate(updatedVehicleRate.getHourlyRate());

	        return vehicleRateRepository.save(existing);
	    }
	}