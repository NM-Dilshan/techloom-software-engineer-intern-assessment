package com.techloom.task02.repository;
import com.techloom.task02.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> { List<Order> findByUserIdOrderByCreatedAtDesc(String userId); }
