package com.vibego.logistics.service;

import com.vibego.logistics.dto.RequestDto;
import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.enums.VehicleStatus;
import com.vibego.logistics.model.*;
import com.vibego.logistics.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public RequestDto createRequest(RequestDto requestDto) {
        Request request = mapToEntity(requestDto);
        request.setStatus(RequestStatus.PENDING);
        Request saved = requestRepository.save(request);
        // Trigger async processing
        processRequestAsync(saved.getId());
        return mapToDto(saved);
    }

    @Async
    public void processRequestAsync(Long requestId) {
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) return;
        Request request = optionalRequest.get();
        request.setStatus(RequestStatus.PROCESSING);
        requestRepository.save(request);

        // Simulate callback if provided
        if (request.getCallbackUrl() != null) {
            // In real, use RestTemplate or WebClient to POST status
            System.out.println("Callback to " + request.getCallbackUrl() + " with status: " + request.getStatus());
        }

        // Find suitable vehicles/drivers
        List<Vehicle> suitableVehicles = findSuitableVehicles(request);
        List<Driver> availableDrivers = findAvailableDrivers(suitableVehicles);

        // Notify drivers
        notifyDrivers(request, availableDrivers);

        request.setStatus(RequestStatus.DRIVERS_NOTIFIED);
        request.setDriversNotified(availableDrivers.size());
        request.setDriversFound(availableDrivers.size());
        requestRepository.save(request);

        // Simulate callback again
        if (request.getCallbackUrl() != null) {
            System.out.println("Callback to " + request.getCallbackUrl() + " with status: " + request.getStatus());
        }
    }

    private List<Vehicle> findSuitableVehicles(Request request) {
        // Simple matching based on capacity and type
        String requiredType = determineVehicleType(request.getWeight(), request.getPackageType());
        return vehicleRepository.findByTypeAndStatus(requiredType, VehicleStatus.AVAILABLE);
    }

    private String determineVehicleType(Double weight, String packageType) {
        if (weight != null && weight > 1000) return "TRUCK";
        if (weight != null && weight > 100) return "VAN";
        return "BIKE";
    }

    private List<Driver> findAvailableDrivers(List<Vehicle> vehicles) {
        // Find drivers linked to these vehicles who are available
        Set<Long> vehicleIds = vehicles.stream().map(Vehicle::getId).collect(Collectors.toSet());
        List<VehicleDriver> assignments = vehicleDriverRepository.findAll().stream()
                .filter(vd -> vehicleIds.contains(vd.getVehicleId()) && vd.getStatus() == DriverStatus.AVAILABLE)
                .toList();
        Set<Long> driverIds = assignments.stream().map(VehicleDriver::getDriverId).collect(Collectors.toSet());
        return driverRepository.findAllById(driverIds).stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    private void notifyDrivers(Request request, List<Driver> drivers) {
        for (Driver driver : drivers) {
            Notification notification = new Notification();
            notification.setDriverId(driver.getId());
            notification.setRequestId(request.getId());
            notification.setMessage("New delivery request: " + request.getDescription() + ". Weight: " + request.getWeight() + "kg. Accept?");
            notification.setType("NEW_REQUEST");
            notification.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            notificationRepository.save(notification);
            request.getNotifiedDrivers().add(driver.getId());
        }
    }

    @Transactional
    public RequestDto getRequestStatus(Long id) {
        return requestRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    @Transactional
    public RequestDto updateRequestStatus(Long id, RequestStatus status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        Request saved = requestRepository.save(request);
        return mapToDto(saved);
    }

    // Map methods
    private Request mapToEntity(RequestDto dto) {
        Request entity = new Request();
        entity.setUserId(dto.getUserId());
        entity.setPackageType(dto.getPackageType());
        entity.setWeight(dto.getWeight());
        entity.setFragility(dto.getFragility());
        entity.setDescription(dto.getDescription());
        entity.setUrgency(dto.getUrgency());
        entity.setCategory(dto.getCategory());
        entity.setPickupLocation(dto.getPickupLocation());
        entity.setDeliveryLocation(dto.getDeliveryLocation());
        entity.setCallbackUrl(dto.getCallbackUrl());
        // etc.
        return entity;
    }

    private RequestDto mapToDto(Request entity) {
        RequestDto dto = new RequestDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setPackageType(entity.getPackageType());
        dto.setWeight(entity.getWeight());
        dto.setFragility(entity.getFragility());
        dto.setDescription(entity.getDescription());
        dto.setUrgency(entity.getUrgency());
        dto.setCategory(entity.getCategory());
        dto.setPickupLocation(entity.getPickupLocation());
        dto.setDeliveryLocation(entity.getDeliveryLocation());
        dto.setOrderId(entity.getOrderId());
        dto.setStatus(entity.getStatus());
        dto.setDriversNotified(entity.getDriversNotified());
        dto.setDriversAccepted(entity.getDriversAccepted());
        dto.setDriversFound(entity.getDriversFound());
        dto.setCallbackUrl(entity.getCallbackUrl());
        dto.setNotifiedDrivers(entity.getNotifiedDrivers());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
