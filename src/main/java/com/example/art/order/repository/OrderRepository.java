package com.example.art.order.repository;

import com.example.art.order.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {

    Optional<Orders> findBySavedDateAndSavedHour(LocalDate savedDate, LocalTime savedHour);
}
