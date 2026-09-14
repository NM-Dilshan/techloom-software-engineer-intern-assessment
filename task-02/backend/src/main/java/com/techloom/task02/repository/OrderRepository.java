package com.techloom.task02.repository;
import com.techloom.task02.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
public interface OrderRepository extends JpaRepository<Order, Long> {
 List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select o from Order o where o.id = :id") Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
