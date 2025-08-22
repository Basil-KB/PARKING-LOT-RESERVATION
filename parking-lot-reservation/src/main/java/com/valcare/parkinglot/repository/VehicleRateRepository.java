package com.valcare.parkinglot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.valcare.parkinglot.model.VehicleRate;

@Repository
public interface VehicleRateRepository extends JpaRepository<VehicleRate, Long> {
	 Optional<VehicleRate> findByVehicleType(String vehicleType);
}
