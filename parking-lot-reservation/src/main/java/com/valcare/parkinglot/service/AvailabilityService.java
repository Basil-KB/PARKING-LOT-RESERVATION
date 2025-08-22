package com.valcare.parkinglot.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import com.valcare.parkinglot.model.Slot;

public interface AvailabilityService {
	 Page<Slot> getAvailableSlots(LocalDateTime startTime, LocalDateTime endTime, int page, int size, String sortBy);
}
