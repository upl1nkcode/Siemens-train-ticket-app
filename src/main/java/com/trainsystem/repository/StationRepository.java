package com.trainsystem.repository;

import com.trainsystem.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
