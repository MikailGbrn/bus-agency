package com.gibran.spring.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gibran.spring.model.Agency;
import com.gibran.spring.model.Bus;
import com.gibran.spring.model.Stop;
import com.gibran.spring.model.Trip;
import com.gibran.spring.model.User;
import com.gibran.spring.payload.request.AgencyRequest;
import com.gibran.spring.payload.request.TripRequest;
import com.gibran.spring.payload.response.MessageResponse;
import com.gibran.spring.repository.AgencyRepository;
import com.gibran.spring.repository.BusRepository;
import com.gibran.spring.repository.StopRepository;
import com.gibran.spring.repository.TripRepository;

import io.swagger.annotations.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/trip")
public class TripController {
	@Autowired
	TripRepository tripRepository;

	@Autowired
	AgencyRepository agencyRepository;

	@Autowired
	BusRepository busRepository;

	@Autowired
	StopRepository stopRepository;
	
	@GetMapping("/")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<?> getAll() {
		List<TripRequest> dataArrResult = new ArrayList<>();
		for (Trip dataArr : tripRepository.findAll()) {
			dataArrResult.add(new TripRequest(dataArr.getId(), dataArr.getFare(), dataArr.getJourneyTime(),
					dataArr.getAgency().getId(), dataArr.getBus().getId(), dataArr.getSourceStop().getId(),
					dataArr.getDestStop().getId()));
		}
		return ResponseEntity.ok(new MessageResponse<TripRequest>(true, "Success Retrieving Data", dataArrResult));
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateTrip(@PathVariable(value = "id") Long id,
			@Valid @RequestBody TripRequest tripDetail) {
		Trip trip = tripRepository.findById(id).get();
		Bus bus = busRepository.findById(tripDetail.getBusId()).get();
		Agency agency = agencyRepository.findById(tripDetail.getAgencyId()).get();
		Stop sourceStop = stopRepository.findById(tripDetail.getSourceStopId()).get();
		Stop destStop = stopRepository.findById(tripDetail.getDestStopId()).get();
		if (trip == null) {
			return ResponseEntity.notFound().build();
		}
		trip.setFare(tripDetail.getFare());
		trip.setJourneyTime(tripDetail.getJourneyTime());
		trip.setBus(bus);
		trip.setAgency(agency);
		trip.setSourceStop(sourceStop);
		trip.setDestStop(destStop);

		Trip updatedTrip = tripRepository.save(trip);

		return ResponseEntity.ok(new MessageResponse<Trip>(true, "Success Updating Data", updatedTrip));
	}

	@PostMapping("/")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addTrip(@Valid @RequestBody TripRequest tripRequest) {
		Agency agency = agencyRepository.findById(tripRequest.getAgencyId()).get();
		Bus bus = busRepository.findById(tripRequest.getBusId()).get();
		Stop sourceStop = stopRepository.findById(tripRequest.getSourceStopId()).get();
		Stop destStop = stopRepository.findById(tripRequest.getDestStopId()).get();
		Trip trip = new Trip(tripRequest.getFare(), tripRequest.getJourneyTime(), sourceStop, destStop, bus, agency);
		return ResponseEntity.ok(tripRepository.save(trip));
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<?> getTripByAgencyId(@PathVariable(value = "id") Long id) {
		List<Trip> trip = tripRepository.findByAgencyId(id);
		return ResponseEntity.ok(trip);
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteTrip(@PathVariable(value = "id") Long id) {
		String result = "";
		try {
			tripRepository.findById(id).get();

			result = "Success Deleting Data with Id: " + id;
			tripRepository.deleteById(id);

			return ResponseEntity.ok(new MessageResponse<Trip>(true, result));
		} catch (Exception e) {
			result = "Data with Id: " + id + " Not Found";
			return ResponseEntity.ok(new MessageResponse<Trip>(false, result));
		}
	}
}
