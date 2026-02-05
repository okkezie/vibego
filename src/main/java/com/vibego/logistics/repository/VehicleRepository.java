package com.vibego.logistics.repository;

import com.vibego.logistics.model.Vehicle;
import com.vibego.logistics.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByTypeAndStatus(String type, VehicleStatus status);
}
