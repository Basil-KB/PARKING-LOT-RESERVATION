package com.valcare.parkinglot.service;

import java.time.LocalDateTime;

import com.valcare.parkinglot.model.Reservation;

import jakarta.validation.Valid;

public interface ReservationService {
	Reservation reserveSlot(@Valid Reservation reservation);
    Reservation getReservation(Long id);
    void cancelReservation(Long id);
    Reservation rescheduleReservation(Long reservationId, LocalDateTime newStartTime, LocalDateTime newEndTime);
}
