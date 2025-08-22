package com.valcare.parkinglot.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.valcare.parkinglot.dto.SlotDto;
import com.valcare.parkinglot.dto.VehicleRateDto;
import com.valcare.parkinglot.model.*;
import com.valcare.parkinglot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Parking Lot Reservation API", description = "Manage floors, slots, reservations")
public class ParkingLotController {
	@Autowired
    private AvailabilityService availabilityService;
	@Autowired
	private FloorService floorService;
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private SlotService slotService;
	@Autowired
	private VehicleRateService vehicleRateService;
	
	
	// --- Floor APIs ---
    @PostMapping("/floors")
    @Operation(summary = "Create a parking floor")
    public Floor createFloor(@Valid @RequestBody Floor floor) {
    	return floorService.createFloor(floor);
    }
    @GetMapping("/floors/{id}")
    @Operation(summary = "Get floor details by ID")
    public Floor getFloor(@PathVariable Long id) {
        return floorService.getFloor(id);
    }
    @PutMapping("/floors/{id}")
    @Operation(summary = "Update floor details by ID")
    public ResponseEntity<Floor> updateFloor(@PathVariable Long id,@Valid @RequestBody Floor floorDetails) {
        Floor updatedFloor = floorService.updateFloor(id, floorDetails);
        return ResponseEntity.ok(updatedFloor);
    }
 // --- Slot APIs ---
    @PostMapping("/slots")
    @Operation(summary = "Create a parking slot under a floor")
    public Slot createSlot(@RequestParam Long floorId, @Valid @RequestBody Slot slot) {
        return slotService.createSlot(floorId, slot);
    }
    @GetMapping("/slots/{id}")
    @Operation(summary = "Get slot details by ID")
    public Slot getSlot(@PathVariable Long id) {
        return slotService.getSlot(id);
    }
    @PutMapping("/slots/{id}")
    @Operation(summary = "Update Parking Slot details by id")
    public ResponseEntity<Slot> updateSlot(@PathVariable Long id, @Valid @RequestBody SlotDto slotDtoDetails) {
    	 Slot updatedSlot = slotService.updateSlot(id, slotDtoDetails);
        return ResponseEntity.ok(updatedSlot);
    }
 // --- Reservation APIs ---
    @PostMapping("/reserve")
    @Operation(summary = "Reserve a parking slot")
    public Reservation reserveSlot(@Valid @RequestBody Reservation reservation) {
        return reservationService.reserveSlot(reservation);
    }
    @GetMapping("/reservations/{id}")
    @Operation(summary = "Get reservation details by ID")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id);
    }

    @DeleteMapping("/reservations/{id}")
    @Operation(summary = "Cancel a reservation by ID")
    public void cancelReservation(@PathVariable Long id) {
    	reservationService.cancelReservation(id);
    }
    @PutMapping("/reservations/{id}/reschedule")
    @Operation(summary = "reschedule the reservation by ID")
    public ResponseEntity<Reservation> rescheduleReservation(
        @PathVariable Long id,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {

        Reservation updated = reservationService.rescheduleReservation(id, newStartTime, newEndTime);
        return ResponseEntity.ok(updated);
    }
 // --- Availability API ---
    @GetMapping("/availability")
    @Operation(summary = "Get available slots for given time range with pagination and sorting")
    public Page<Slot> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "slotNumber") String sortBy) {
        return availabilityService.getAvailableSlots(startTime, endTime, page, size, sortBy);
    }
  //  ---Vehicle Rate API-----
   
    @PostMapping("/vehicle-rates")
    @Operation(summary = "Add Vehicle Type and Rate")
        public ResponseEntity<VehicleRate> createVehicleRate(@Valid @RequestBody VehicleRate vehicleRate) {
            VehicleRate created = vehicleRateService.createVehicleRate(vehicleRate);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @PutMapping("/vehicle-rates/{id}")
        @Operation(summary = "Update Vehicle Type or Rate")
        public ResponseEntity<VehicleRate> updateVehicleRate(@PathVariable Long id,@Valid @RequestBody VehicleRateDto vehicleRateDto) {
            VehicleRate updated = vehicleRateService.updateVehicleRate(id, vehicleRateDto);
            return ResponseEntity.ok(updated);
        }
    
}
