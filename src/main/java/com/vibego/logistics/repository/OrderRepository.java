package com.vibego.logistics.repository;

import com.vibego.logistics.model.Order;
import com.vibego.logistics.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRequestId(Long requestId);
    List<Order> findByDriverIdAndStatus(Long driverId, OrderStatus status);
}
