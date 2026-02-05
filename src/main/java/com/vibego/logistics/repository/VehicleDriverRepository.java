package com.vibego.logistics.repository;

import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.model.VehicleDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, Long> {
    List<VehicleDriver> findByDriverIdAndStatus(Long driverId, DriverStatus status);
    List<VehicleDriver> findByVehicleId(Long vehicleId);
}
