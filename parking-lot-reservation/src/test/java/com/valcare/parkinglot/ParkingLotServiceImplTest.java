package com.valcare.parkinglot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import com.valcare.parkinglot.dto.SlotDto;
import com.valcare.parkinglot.dto.VehicleRateDto;
import com.valcare.parkinglot.model.Floor;
import com.valcare.parkinglot.model.Reservation;
import com.valcare.parkinglot.model.Slot;
import com.valcare.parkinglot.model.VehicleRate;
import com.valcare.parkinglot.repository.FloorRepository;
import com.valcare.parkinglot.repository.ReservationRepository;
import com.valcare.parkinglot.repository.SlotRepository;
import com.valcare.parkinglot.repository.VehicleRateRepository;
import com.valcare.parkinglot.service.ParkingLotServiceImpl;
import com.valcare.parkinglot.service.VehicleRateServiceImpl;

public class ParkingLotServiceImplTest {
	@InjectMocks
    private ParkingLotServiceImpl parkingLotService;
	@InjectMocks
	private VehicleRateServiceImpl VehicleRateService;

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private VehicleRateRepository vehicleRateRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Floor Service ---

    @Test
    void createFloor_shouldSaveFloor() {
        Floor floor = Floor.builder().floorName("Test Floor").build();
        when(floorRepository.save(floor)).thenReturn(floor);
        Floor saved = parkingLotService.createFloor(floor);
        assertEquals("Test Floor", saved.getFloorName());
        verify(floorRepository, times(1)).save(floor);
    }

    @Test
    void getFloor_shouldReturnFloor_whenExists() {
        Floor floor = new Floor();
        floor.setFloorId(1L);
        floor.setFloorName("Test");
        when(floorRepository.findById(1L)).thenReturn(Optional.of(floor));
        Floor found = parkingLotService.getFloor(1L);
        assertNotNull(found);
        assertEquals(1L, found.getFloorId());
    }

    @Test
    void getFloor_shouldThrowException_whenNotFound() {
        when(floorRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> parkingLotService.getFloor(999L));
    }

    // --- Slot Service ---

    @Test
    void createSlot_shouldSaveSlot() {
        Floor floor = Floor.builder().floorId(1L).floorName("Test Floor").build();
        VehicleRate rate = new VehicleRate();
        rate.setVehicleId(1L);
        rate.setVehicleType("Car");
        rate.setHourlyRate(20);
        Slot slot = new Slot();
        slot.setSlotNumber("A1");
        slot.setVehicleRate(rate);
        when(floorRepository.findById(1L)).thenReturn(Optional.of(floor));
        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Slot saved = parkingLotService.createSlot(1L, slot);
        assertEquals("A1", saved.getSlotNumber());
        assertEquals(floor, saved.getFloor());
        verify(slotRepository).save(slot);
    }

    // --- Reservation Service ---

    @Test
    void reserveSlot_shouldSaveReservation() {
        VehicleRate rate = new VehicleRate();
        rate.setVehicleId(1L);
        rate.setHourlyRate(30);
        rate.setVehicleType("Car");

        Floor floor = Floor.builder().floorId(1L).floorName("Floor1").build();
        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setFloor(floor);
        slot.setVehicleRate(rate);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(3);

        Reservation reservation = new Reservation();
        reservation.setSlot(slot);
        reservation.setVehicleRate(rate);
        reservation.setVehicleNumber("AB12CD1234");
        reservation.setStartTime(start);
        reservation.setEndTime(end);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(slot, end, start))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Reservation saved = parkingLotService.reserveSlot(reservation);

        assertNotNull(saved);
        assertEquals(90, saved.getTotalFee()); // 3 hours * 30 rate (rounded)
    }

    @Test
    void reserveSlot_shouldThrowException_whenOverlapping() {
        VehicleRate rate = new VehicleRate();
        rate.setVehicleId(1L);
        rate.setHourlyRate(30);
        rate.setVehicleType("Car");

        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setVehicleRate(rate);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(3);

        Reservation reservation = new Reservation();
        reservation.setSlot(slot);
        reservation.setVehicleRate(rate);
        reservation.setVehicleNumber("AB12CD1234");
        reservation.setStartTime(start);
        reservation.setEndTime(end);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(rate));
        when(reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(slot, end, start))
                .thenReturn(List.of(reservation)); // overlapping reservation

        assertThrows(IllegalStateException.class, () -> parkingLotService.reserveSlot(reservation));
    }

    // --- Availability Service ---

    @Test
    void getAvailableSlots_shouldReturnPage() {
        VehicleRate rate = new VehicleRate();
        rate.setVehicleId(1L);
        rate.setHourlyRate(20);

        Floor floor = Floor.builder().floorId(1L).floorName("Floor1").build();

        Slot slot1 = new Slot();
        slot1.setSlotId(1L);
        slot1.setFloor(floor);
        slot1.setVehicleRate(rate);

        Slot slot2 = new Slot();
        slot2.setSlotId(2L);
        slot2.setFloor(floor);
        slot2.setVehicleRate(rate);

        List<Slot> allSlots = List.of(slot1, slot2);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        when(slotRepository.findAll()).thenReturn(allSlots);
        when(reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Page<Slot> page = parkingLotService.getAvailableSlots(start, end, 0, 10, "slotNumber");
        assertEquals(2, page.getContent().size());
    }
    
 // Test createVehicleRate normal and duplicate vehicle type
    @Test
    void createVehicleRate_shouldSave_whenNewVehicleType() {
        VehicleRate rate = new VehicleRate();
        rate.setVehicleType("Bike");
        rate.setHourlyRate(10);

        when(vehicleRateRepository.findByVehicleType("Bike")).thenReturn(Optional.empty());
        when(vehicleRateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        VehicleRate created = VehicleRateService.createVehicleRate(rate);

        assertEquals("Bike", created.getVehicleType());
        verify(vehicleRateRepository).save(rate);
    }

    @Test
    void createVehicleRate_shouldThrowException_whenDuplicateVehicleType() {
        VehicleRate existing = new VehicleRate();
        existing.setVehicleType("Car");

        VehicleRate newRate = new VehicleRate();
        newRate.setVehicleType("Car");

        when(vehicleRateRepository.findByVehicleType("Car")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> VehicleRateService.createVehicleRate(newRate));
    }
 // Test updateVehicleRate success and not found
    @Test
    void updateVehicleRate_shouldUpdate_whenExists() {
        VehicleRate existing = new VehicleRate();
        existing.setVehicleId(1L);
        existing.setVehicleType("Car");
        existing.setHourlyRate(20);

        VehicleRateDto update = new VehicleRateDto();
        update.setVehicleType("SUV");
        update.setHourlyRate(30);

        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        VehicleRate updated = VehicleRateService.updateVehicleRate(1L, update);

        assertEquals("SUV", updated.getVehicleType());
        assertEquals(30, updated.getHourlyRate());
    }

    @Test
    void updateVehicleRate_shouldThrowException_whenNotFound() {
        when(vehicleRateRepository.findById(999L)).thenReturn(Optional.empty());

        VehicleRateDto update = new VehicleRateDto();
        update.setVehicleType("SUV");
        update.setHourlyRate(30);

        assertThrows(IllegalArgumentException.class, () -> VehicleRateService.updateVehicleRate(999L, update));
    }
 // --- Floor update test ---
    @Test
    void updateFloor_shouldUpdateAndReturnFloor() {
        Floor existing = new Floor(1L, "Old Floor");
        Floor updateDetails = new Floor(null, "Updated Floor");

        when(floorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(floorRepository.save(any(Floor.class))).thenAnswer(i -> i.getArgument(0));

        Floor updated = parkingLotService.updateFloor(1L, updateDetails);

        assertEquals("Updated Floor", updated.getFloorName());
        verify(floorRepository).save(existing);
    }

    @Test
    void updateFloor_shouldThrowException_whenFloorNotFound() {
        when(floorRepository.findById(999L)).thenReturn(Optional.empty());
        Floor updateDetails = new Floor(null, "Updated Floor");

        assertThrows(IllegalArgumentException.class,
            () -> parkingLotService.updateFloor(999L, updateDetails));
    }
 // --- Slot get and update tests ---

    @Test
    void getSlot_shouldReturnSlot_whenExists() {
        Slot slot = new Slot();
        slot.setSlotId(1L);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        Slot result = parkingLotService.getSlot(1L);

        assertNotNull(result);
        assertEquals(1L, result.getSlotId());
    }

    @Test
    void getSlot_shouldThrowException_whenNotFound() {
        when(slotRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> parkingLotService.getSlot(999L));
    }

    @Test
    void updateSlot_shouldUpdateSlotFields() {
        Slot existing = new Slot();
        existing.setSlotId(1L);
        existing.setSlotNumber("A1");

        Floor floor = new Floor(1L, "Floor1");
        VehicleRate rate = new VehicleRate(1L, "Car", 20);

        SlotDto slotDto = new SlotDto();
        slotDto.setSlotNumber("B2");
        slotDto.setFloor(floor);
        slotDto.setVehicleRate(rate);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Slot updated = parkingLotService.updateSlot(1L, slotDto);

        assertEquals("B2", updated.getSlotNumber());
        assertEquals(floor, updated.getFloor());
        assertEquals(rate, updated.getVehicleRate());
        verify(slotRepository).save(existing);
    }
 // --- Reservation cancel tests ---

    @Test
    void cancelReservation_shouldDelete_whenExists() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1L);

        assertDoesNotThrow(() -> parkingLotService.cancelReservation(1L));
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void cancelReservation_shouldThrowException_whenNotFound() {
        when(reservationRepository.existsById(999L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
            () -> parkingLotService.cancelReservation(999L));
    }

    // --- Reservation reschedule tests ---

    @Test
    void rescheduleReservation_shouldUpdateTimesAndFee() {
        VehicleRate rate = new VehicleRate(1L, "Car", 20);
        Floor floor = new Floor(1L, "Floor1");
        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setFloor(floor);
        slot.setVehicleRate(rate);

        Reservation oldReservation = new Reservation();
        oldReservation.setId(1L);
        oldReservation.setSlot(slot);
        oldReservation.setVehicleRate(rate);
        oldReservation.setVehicleNumber("AB12CD1234");
        oldReservation.setStartTime(LocalDateTime.now().plusHours(1));
        oldReservation.setEndTime(oldReservation.getStartTime().plusHours(2));

        LocalDateTime newStart = oldReservation.getStartTime().plusDays(1);
        LocalDateTime newEnd = newStart.plusHours(3);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(oldReservation));
        when(reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(slot, newEnd, newStart))
            .thenReturn(Collections.emptyList());
        doNothing().when(reservationRepository).deleteById(1L);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Reservation rescheduled = parkingLotService.rescheduleReservation(1L, newStart, newEnd);

        assertEquals(newStart, rescheduled.getStartTime());
        assertEquals(newEnd, rescheduled.getEndTime());

        long expectedHours = 3; // newEnd - newStart duration
        assertEquals(expectedHours * rate.getHourlyRate(), rescheduled.getTotalFee());
    }

    @Test
    void rescheduleReservation_shouldThrowException_whenOverlap() {
        Reservation existingOverlap = new Reservation();
        existingOverlap.setId(2L);

        VehicleRate rate = new VehicleRate(1L, "Car", 20);
        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setVehicleRate(rate);

        Reservation oldReservation = new Reservation();
        oldReservation.setId(1L);
        oldReservation.setSlot(slot);
        oldReservation.setVehicleRate(rate);
        oldReservation.setVehicleNumber("AB12CD1234");
        oldReservation.setStartTime(LocalDateTime.now().plusHours(1));
        oldReservation.setEndTime(LocalDateTime.now().plusHours(2));

        LocalDateTime newStart = LocalDateTime.now().plusHours(3);
        LocalDateTime newEnd = LocalDateTime.now().plusHours(5);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(oldReservation));
        when(reservationRepository.findBySlotAndStartTimeLessThanAndEndTimeGreaterThan(slot, newEnd, newStart))
            .thenReturn(new ArrayList<>(List.of(existingOverlap)));

        assertThrows(IllegalStateException.class,
            () -> parkingLotService.rescheduleReservation(1L, newStart, newEnd));
    }
 // --- Edge case: reservation exceeding max hours ---

    @Test
    void reserveSlot_shouldThrowException_whenDurationExceedsMax() {
        VehicleRate rate = new VehicleRate(1L, "Car", 20);
        Floor floor = new Floor(1L, "Floor1");
        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setFloor(floor);
        slot.setVehicleRate(rate);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(25); // exceeds 24 hours max

        Reservation reservation = new Reservation();
        reservation.setSlot(slot);
        reservation.setVehicleRate(rate);
        reservation.setVehicleNumber("AB12CD1234");
        reservation.setStartTime(start);
        reservation.setEndTime(end);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(rate));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> parkingLotService.reserveSlot(reservation));
        assertTrue(ex.getMessage().contains("must not exceed 24 hours"));
    }
 // --- Edge case: invalid vehicle number format ---

    @Test
    void reserveSlot_shouldThrowException_whenVehicleNumberInvalid() {
        VehicleRate rate = new VehicleRate(1L, "Car", 20);
        Floor floor = new Floor(1L, "Floor1");
        Slot slot = new Slot();
        slot.setSlotId(1L);
        slot.setFloor(floor);
        slot.setVehicleRate(rate);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(3);

        Reservation reservation = new Reservation();
        reservation.setSlot(slot);
        reservation.setVehicleRate(rate);
        reservation.setVehicleNumber("INVALID123"); // invalid format
        reservation.setStartTime(start);
        reservation.setEndTime(end);

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(vehicleRateRepository.findById(1L)).thenReturn(Optional.of(rate));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> parkingLotService.reserveSlot(reservation));
        assertTrue(ex.getMessage().contains("must follow the format"));
    }

}