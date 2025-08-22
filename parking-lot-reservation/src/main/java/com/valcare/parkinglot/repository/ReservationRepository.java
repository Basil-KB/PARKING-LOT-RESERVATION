package com.valcare.parkinglot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.valcare.parkinglot.model.Reservation;
import com.valcare.parkinglot.model.Slot;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	// Fetch reservations by slot and time window (for availability and conflict checks)
    List<Reservation> findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(
            Slot slot, LocalDateTime endTime, LocalDateTime startTime);
 // Find overlapping reservations for the given slot and time range (for availability check)
    List<Reservation> findBySlotAndEndTimeGreaterThanAndStartTimeLessThan(
            Slot slot, LocalDateTime startTime, LocalDateTime endTime);

}
