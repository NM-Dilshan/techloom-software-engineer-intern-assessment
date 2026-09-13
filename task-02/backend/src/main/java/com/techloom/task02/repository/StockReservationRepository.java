package com.techloom.task02.repository;
import com.techloom.task02.entity.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> { List<StockReservation> findByActiveTrueAndExpiresAtBefore(Instant now); List<StockReservation> findByOrder(Order order); }
