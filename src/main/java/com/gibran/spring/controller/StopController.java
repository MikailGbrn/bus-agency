package com.gibran.spring.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gibran.spring.model.Agency;
import com.gibran.spring.model.Stop;
import com.gibran.spring.payload.request.AgencyRequest;
import com.gibran.spring.payload.request.GetTripByStopRequest;
import com.gibran.spring.payload.response.MessageResponse;
import com.gibran.spring.repository.StopRepository;

import io.swagger.annotations.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/stop")
public class StopController {

	@Autowired
	StopRepository stopRepository;

	@GetMapping("/")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getAllStops() {
		return ResponseEntity.ok(stopRepository.findAll());
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getStopById(@PathVariable(value = "id") Long id) {
		return ResponseEntity.ok(stopRepository.findById(id));
	}

	@PostMapping("/")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> addStop(@Valid @RequestBody Stop stop) {
		return ResponseEntity.ok(stopRepository.save(stop));
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "", authorizations = { @Authorization(value = "apiKey") })
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteStop(@PathVariable(value = "id") Long id) {
		String result = "";
		try {
			stopRepository.findById(id).get();

			result = "Success Deleting Data with Id: " + id;
			stopRepository.deleteById(id);

			return ResponseEntity.ok(new MessageResponse<Stop>(true, result));
		} catch (Exception e) {
			result = "Data with Id: " + id + " Not Found";
			return ResponseEntity.ok(new MessageResponse<Stop>(false, result));
		}
	}

}
