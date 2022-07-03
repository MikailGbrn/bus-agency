package com.gibran.spring.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gibran.spring.model.Trip;
import com.gibran.spring.model.TripSchedule;
import com.gibran.spring.payload.request.GetTripScheduleRequest;
import com.gibran.spring.payload.response.MessageResponse;
import com.gibran.spring.repository.TicketRepository;
import com.gibran.spring.repository.TripRepository;
import com.gibran.spring.repository.TripScheduleRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/tripschedule")
public class TripScheduleController {
	
	@Autowired
	TicketRepository ticketRepository;
	
	@Autowired
	TripRepository tripRepository;
	
	@Autowired
	TripScheduleRepository tripScheduleRepository;
	
	@GetMapping("/")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getAll() {
		List<GetTripScheduleRequest> dataArrResult = new ArrayList<>();
		for (TripSchedule dataArr : tripScheduleRepository.findAll()) {
			dataArrResult.add(new GetTripScheduleRequest(dataArr.getId(), dataArr.getAvailableSeats(),
					dataArr.getTripDate(), dataArr.getTripDetail().getId()));
		}
		return ResponseEntity.ok(new MessageResponse<GetTripScheduleRequest>(true, "Success Retrieving Data", dataArrResult));
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> getTripScheduleById(@PathVariable(value = "id") Long id) {
		TripSchedule tripschedule = tripScheduleRepository.findById(id).get();
		if (tripschedule == null) {
			return ResponseEntity.notFound().build();
		} else {
			GetTripScheduleRequest dataResult = new GetTripScheduleRequest(tripschedule.getId(), tripschedule.getAvailableSeats(),
					tripschedule.getTripDate(),tripschedule.getTripDetail().getId());
			return ResponseEntity.ok(new MessageResponse<GetTripScheduleRequest>(true, "Success Retrieving Data", dataResult));
		}
	}
	
	@PostMapping("/")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addTripSchedule(@Valid @RequestBody GetTripScheduleRequest tripScheduleRequest) {
		Trip trip = tripRepository.findById(tripScheduleRequest.getTrip_detail()).get();
		TripSchedule tripschedule = new TripSchedule(tripScheduleRequest.getTripDate(),
				tripScheduleRequest.getAvailable_seats(), trip);
		return ResponseEntity
				.ok(new MessageResponse<TripSchedule>(true, "Success Adding Data", tripScheduleRepository.save(tripschedule)));
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateTripSchedule(@PathVariable(value = "id") Long id,
			@Valid @RequestBody GetTripScheduleRequest tripScheduleDetail) {
		TripSchedule tripschedule = tripScheduleRepository.findById(id).get();
		Trip trip = tripRepository.findById(tripScheduleDetail.getTrip_detail()).get();
		if (tripschedule == null) {
			return ResponseEntity.notFound().build();
		}
		tripschedule.setAvailableSeats(tripScheduleDetail.getAvailable_seats());
		tripschedule.setTripDate(tripScheduleDetail.getTripDate());
		tripschedule.setTripDetail(trip);

		TripSchedule updatedTripSchedule = tripScheduleRepository.save(tripschedule);

		return ResponseEntity.ok(new MessageResponse<TripSchedule>(true, "Success Updating Data", updatedTripSchedule));
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteTripSchedule(@PathVariable(value = "id") Long id) {
		String result = "";
		try {
			tripScheduleRepository.findById(id).get();

			result = "Success Deleting Data with Id: " + id;
			tripScheduleRepository.deleteById(id);

			return ResponseEntity.ok(new MessageResponse<TripSchedule>(true, result));
		} catch (Exception e) {
			result = "Data with Id: " + id + " Not Found";
			return ResponseEntity.ok(new MessageResponse<TripSchedule>(false, result));
		}
	}
	
}
