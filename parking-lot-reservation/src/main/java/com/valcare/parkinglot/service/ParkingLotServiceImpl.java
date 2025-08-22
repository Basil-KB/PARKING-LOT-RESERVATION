package com.valcare.parkinglot.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.valcare.parkinglot.dto.SlotDto;
import com.valcare.parkinglot.model.Floor;
import com.valcare.parkinglot.model.Reservation;
import com.valcare.parkinglot.model.Slot;
import com.valcare.parkinglot.model.VehicleRate;
import com.valcare.parkinglot.repository.FloorRepository;
import com.valcare.parkinglot.repository.ReservationRepository;
import com.valcare.parkinglot.repository.SlotRepository;
import com.valcare.parkinglot.repository.VehicleRateRepository;

import jakarta.validation.Valid;

@Service
public class ParkingLotServiceImpl implements FloorService, SlotService, ReservationService, AvailabilityService {

	private static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$");
    private static final int MAX_RESERVATION_HOURS = 24;
    
    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VehicleRateRepository vehicleRateRepository;
    
//..................Availability Service methods........................................ 
	@Override
	public Page<Slot> getAvailableSlots(LocalDateTime startTime, LocalDateTime endTime, int page, int size,
			String sortBy) {
		if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
		List<Slot> allSlots = slotRepository.findAll();
		List<Slot> availableSlots = allSlots.stream()
	                .filter(slot -> {
	                    List<Reservation> overlaps = reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(
	                            slot, endTime, startTime);
	                    return overlaps.isEmpty();}).collect(Collectors.toList());
		  int start = (int) pageable.getOffset();
	        int end = Math.min(start + pageable.getPageSize(), availableSlots.size());
	        List<Slot> pageContent = availableSlots.subList(start, end);

	        return new PageImpl<>(pageContent, pageable, availableSlots.size());
	    
	}

//..................Reservation Service methods.......................................................
	@Override
	@Transactional
    public Reservation reserveSlot(@Valid Reservation reservation) {
        if (!reservation.getStartTime().isBefore(reservation.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        long durationInMinutes = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
        if (durationInMinutes <= 0) {
            throw new IllegalArgumentException("Reservation duration must be positive");
        }
        if (durationInMinutes > MAX_RESERVATION_HOURS * 60) {
            throw new IllegalArgumentException("Reservation duration must not exceed 24 hours");
        }

        if (!VEHICLE_NUMBER_PATTERN.matcher(reservation.getVehicleNumber()).matches()) {
            throw new IllegalArgumentException("Vehicle number must follow the format XX00XX0000");
        }

        Slot slot = slotRepository.findById(reservation.getSlot().getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found with id: " + reservation.getSlot().getSlotId()));

        VehicleRate rate = vehicleRateRepository.findById(reservation.getVehicleRate().getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("VehicleTypeRate not found with id: " + reservation.getVehicleRate().getVehicleId()));

        reservation.setSlot(slot);
        reservation.setVehicleRate(rate);

        List<Reservation> overlaps = reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(
                slot, reservation.getEndTime(), reservation.getStartTime());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Time slot is already booked for the selected parking slot");
        }

        long hours = (durationInMinutes + 59) / 60;
        reservation.setTotalFee((int) (hours * rate.getHourlyRate()));

        return reservationRepository.save(reservation);
	    
	}

	@Override
	public Reservation getReservation(Long id) {
		return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));	}

	@Override
	public void cancelReservation(Long id) {
		 if (!reservationRepository.existsById(id)) {
	            throw new IllegalArgumentException("Reservation not found with id: " + id);
		 }
		 reservationRepository.deleteById(id);
		
	}
	@Override
	@Transactional
	public Reservation rescheduleReservation(Long oldReservationId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
		
		// Fetch the old reservation
	    Reservation oldReservation = reservationRepository.findById(oldReservationId)
	    		.orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + oldReservationId));
		
	 // Delete the old reservation
	    reservationRepository.deleteById(oldReservationId);
		
		if (!newStartTime.isBefore(newEndTime)) {
	        throw new IllegalArgumentException("Start time must be before end time");
	    }
		// Check for overlap excluding current reservation
	    List<Reservation> overlaps = reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(
	    		oldReservation.getSlot(), newEndTime, newStartTime);
	    overlaps.removeIf(r -> r.getId().equals(oldReservation.getId()));
	   

	    if (!overlaps.isEmpty()) {
	        throw new IllegalStateException("Time slot is already booked for the selected parking slot");
	    }

	 // Prepare new reservation
	    Reservation newReservation = new Reservation();
	    newReservation.setSlot(oldReservation.getSlot());
	    newReservation.setVehicleNumber(oldReservation.getVehicleNumber());
	    newReservation.setVehicleRate(oldReservation.getVehicleRate());
	    newReservation.setStartTime(newStartTime);
	    newReservation.setEndTime(newEndTime);
	 // Recalculate total fee based on updated time
	    long durationInMinutes = Duration.between(newStartTime, newEndTime).toMinutes();
	    long hours = (durationInMinutes + 59) / 60; // round up hours
	    newReservation.setTotalFee((int) (hours * newReservation.getVehicleRate().getHourlyRate()));
	    // Save the New reservation in New ID
	    return reservationRepository.save(newReservation);
	}

//....................SlotService methods.................................................
	
	@Override
	public Slot createSlot(Long floorId, @Valid Slot slot) {
		Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new IllegalArgumentException("Floor not found with id: " + floorId));
		slot.setFloor(floor);
		  if (slot.getVehicleRate() == null || slot.getVehicleRate().getVehicleId() == null) {
	            throw new IllegalArgumentException("VehicleRate must be set for slot");
	        }
	        VehicleRate rate = vehicleRateRepository.findById(slot.getVehicleRate().getVehicleId())
	        		.orElseThrow(() -> new IllegalArgumentException("VehicleRate not found with id: " + slot.getVehicleRate().getVehicleId()));
	        slot.setVehicleRate(rate);

	        return slotRepository.save(slot);
		
	}

	@Override
	public Slot getSlot(Long id) {
		 return slotRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Slot not found with id: " + id));
	}
	@Override
	public Slot updateSlot(Long id, SlotDto slotDetails) {
		 Slot slot = slotRepository.findById(id).orElseThrow(
				 () -> new IllegalArgumentException("Floor not found with id " + id));
		    slot.setSlotNumber(slotDetails.getSlotNumber());
		    slot.setFloor(slotDetails.getFloor());
		    slot.setVehicleRate(slotDetails.getVehicleRate());
		    return slotRepository.save(slot);
	}
	
//...................FloorService methods.............................................................
	
	@Override
	public Floor createFloor(@Valid Floor floor) {
		return floorRepository.save(floor);
	}

	@Override
	public Floor getFloor(Long id) {
		return floorRepository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("Floor not found with id: " + id));				
               
	}
	public Floor updateFloor(Long id, Floor floorDetails) {
	    Floor floor = floorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Floor not found with id " + id));
	    floor.setFloorName(floorDetails.getFloorName());
	    // Update other properties as needed
	    return floorRepository.save(floor);
	}

}
