package com.valcare.parkinglot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Floor {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long floorId;

    @NotBlank(message = "Floor name cannot be blank")
    private String floorName;

   
    

}
