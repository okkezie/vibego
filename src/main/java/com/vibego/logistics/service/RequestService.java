package com.vibego.logistics.service;

import com.vibego.logistics.dto.RequestDto;
import com.vibego.logistics.enums.DriverStatus;
import com.vibego.logistics.enums.RequestStatus;
import com.vibego.logistics.enums.VehicleStatus;
import com.vibego.logistics.event.NotificationEvent;
import com.vibego.logistics.event.RequestEvent;
import com.vibego.logistics.model.*;
import com.vibego.logistics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;

    private final VehicleRepository vehicleRepository;

    private final DriverRepository driverRepository;

    private final VehicleDriverRepository vehicleDriverRepository;

    private final NotificationRepository notificationRepository;

    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public RequestDto createRequest(RequestDto requestDto) {
        log.info("Creating request for user: {}", requestDto.getUserId());
        Request request = mapToEntity(requestDto);
        request.setStatus(RequestStatus.PENDING);
        Request saved = requestRepository.save(request);
        eventPublisher.publishEvent(new RequestEvent(this, saved.getId()));
        log.info("Request created with ID: {} and status: {}", saved.getId(), saved.getStatus());
        return mapToDto(saved);
    }

    @Async
    @Transactional
    public void processRequest(Long requestId) {
        log.info("Starting processing for request ID: {}", requestId);
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            log.warn("Request ID {} not found, skipping processing", requestId);
            return;
        }
        Request request = optionalRequest.get();
        request.setStatus(RequestStatus.PROCESSING);
        requestRepository.save(request);
        log.info("Request {} status updated to PROCESSING", requestId);

        if (request.getCallbackUrl() != null) {
            log.info("Callback to {} with status: {}", request.getCallbackUrl(), request.getStatus());
        }

        List<Vehicle> suitableVehicles = findSuitableVehicles(request);
        log.info("Found {} suitable vehicles for request {}", suitableVehicles.size(), requestId);
        List<Driver> availableDrivers = findAvailableDrivers(suitableVehicles);
        log.info("Found {} available drivers for request {}", availableDrivers.size(), requestId);

        eventPublisher.publishEvent(new NotificationEvent(this, request, availableDrivers));
        log.info("Published notification event for request {}", requestId);

        request.setStatus(RequestStatus.DRIVERS_NOTIFIED);
        request.setDriversNotified(availableDrivers.size());
        request.setDriversFound(availableDrivers.size());
        requestRepository.save(request);
        log.info("Request {} status updated to DRIVERS_NOTIFIED with {} drivers notified", requestId, availableDrivers.size());

        if (request.getCallbackUrl() != null) {
            log.info("Callback to {} with status: {}", request.getCallbackUrl(), request.getStatus());
        }
        log.info("Completed processing for request ID: {}", requestId);
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
