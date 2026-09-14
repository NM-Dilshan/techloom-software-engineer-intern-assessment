package com.techloom.task01.repository;

import com.techloom.task01.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.id = :id")
	java.util.Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
