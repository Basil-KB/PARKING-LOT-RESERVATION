package com.valcare.parkinglot.dto;

import com.valcare.parkinglot.model.Floor;
import com.valcare.parkinglot.model.VehicleRate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotDto {
	@NotBlank
    private String slotNumber;

    @Valid
    @NotNull
    private VehicleRate vehicleRate;

    @Valid
    @NotNull
    private Floor floor;
}
