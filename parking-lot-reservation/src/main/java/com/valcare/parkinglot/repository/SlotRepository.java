package com.valcare.parkinglot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.valcare.parkinglot.model.Floor;
import com.valcare.parkinglot.model.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> { 
	List<Slot> findByFloor(Floor floor);
}
