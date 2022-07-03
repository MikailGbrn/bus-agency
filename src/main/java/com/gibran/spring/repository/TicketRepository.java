package com.gibran.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gibran.spring.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
