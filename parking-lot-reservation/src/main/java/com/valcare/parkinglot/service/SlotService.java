package com.valcare.parkinglot.service;

import com.valcare.parkinglot.dto.SlotDto;
import com.valcare.parkinglot.model.Slot;

import jakarta.validation.Valid;

public interface SlotService {
	Slot createSlot(Long floorId, @Valid Slot slot);
    Slot getSlot(Long id);
    Slot updateSlot(Long id, @Valid SlotDto slotDtoDetails);
}
