package com.vibego.logistics.repository;

import com.vibego.logistics.model.Request;
import com.vibego.logistics.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatus(RequestStatus status);
    List<Request> findByUserId(String userId);
}
