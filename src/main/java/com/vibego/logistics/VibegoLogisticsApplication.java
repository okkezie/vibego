package com.vibego.logistics;

import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.enums.VehicleStatus;
import com.vibego.logistics.model.*;
import com.vibego.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDate;

@SpringBootApplication
@EnableAsync
@Slf4j
public class VibegoLogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibegoLogisticsApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                               VehicleDriverRepository vehicleDriverRepository, UserRepository userRepository,
                               org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed users (passwords encoded)
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password"));
            user1.setEmail("user1@example.com");
            user1.setRole("USER");
            userRepository.save(user1);

            User driverUser1 = new User();
            driverUser1.setUsername("driver1");
            driverUser1.setPassword(passwordEncoder.encode("password"));
            driverUser1.setEmail("driver1@example.com");
            driverUser1.setRole("DRIVER");
            userRepository.save(driverUser1);

            // Seed drivers with email and userId
            Driver driver1 = new Driver();
            driver1.setName("John Doe");
            driver1.setGender("Male");
            driver1.setDob(LocalDate.of(1990, 1, 1));
            driver1.setAddress("123 Main St");
            driver1.setAddressCity("New York");
            driver1.setAddressState("NY");
            driver1.setIdentityType("Passport");
            driver1.setIdentityNumber("P123456");
            driver1.setIdentityVerified(true);
            driver1.setPhone("1234567890");
            driver1.setEmail("john.doe@example.com");
            driver1.setUserId(driverUser1.getId());
            driver1.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver1);

            Driver driver2 = new Driver();
            driver2.setName("Jane Smith");
            driver2.setGender("Female");
            driver2.setDob(LocalDate.of(1985, 5, 15));
            driver2.setAddress("456 Elm St");
            driver2.setAddressCity("New York");
            driver2.setAddressState("NY");
            driver2.setIdentityType("Driver License");
            driver2.setIdentityNumber("DL987654");
            driver2.setIdentityVerified(true);
            driver2.setPhone("0987654321");
            driver2.setEmail("jane.smith@example.com");
            driver2.setUserId(user1.getId());
            driver2.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver2);

            // Seed vehicles
            Vehicle vehicle1 = new Vehicle();
            vehicle1.setName("Bike1");
            vehicle1.setCapacity(50.0);
            vehicle1.setMake("Honda");
            vehicle1.setModel("CBR");
            vehicle1.setYear(2022);
            vehicle1.setCondition("Excellent");
            vehicle1.setType("BIKE");
            vehicle1.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle1);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setName("Van1");
            vehicle2.setCapacity(500.0);
            vehicle2.setMake("Toyota");
            vehicle2.setModel("Hiace");
            vehicle2.setYear(2021);
            vehicle2.setCondition("Good");
            vehicle2.setType("VAN");
            vehicle2.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle2);

            // Associate
            VehicleDriver vd1 = new VehicleDriver();
            vd1.setVehicleId(vehicle1.getId());
            vd1.setDriverId(driver1.getId());
            vd1.setStatus(DriverStatus.AVAILABLE);
            vehicleDriverRepository.save(vd1);

            VehicleDriver vd2 = new VehicleDriver();
            vd2.setVehicleId(vehicle2.getId());
            vd2.setDriverId(driver2.getId());
            vd2.setStatus(DriverStatus.AVAILABLE);
            vehicleDriverRepository.save(vd2);

            log.info("Sample data seeded for users, drivers, vehicles and associations.");
        };
    }
}
