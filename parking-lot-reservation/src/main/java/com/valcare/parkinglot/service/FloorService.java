package com.valcare.parkinglot.service;

import com.valcare.parkinglot.model.Floor;

import jakarta.validation.Valid;

public interface FloorService {
	
	Floor createFloor(@Valid Floor floor);
	Floor getFloor(Long id);
	Floor updateFloor(Long id, @Valid Floor floorDetails);
	
}
