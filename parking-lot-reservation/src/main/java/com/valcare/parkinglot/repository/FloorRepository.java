package com.valcare.parkinglot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.valcare.parkinglot.model.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

}
