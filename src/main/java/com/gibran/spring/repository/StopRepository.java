package com.gibran.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gibran.spring.model.Stop;

public interface StopRepository extends JpaRepository<Stop, Long> {
	List<Stop> findByName(String name);

	List<Stop> findByCode(String code);
}
