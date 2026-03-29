package org.fostertogethermn.api.agency;

import java.util.List;
import java.util.Optional;

import org.fostertogethermn.api.agency.dto.AgencyCreateRequest;
import org.fostertogethermn.api.agency.dto.AgencyResponse;
import org.fostertogethermn.api.agency.dto.AgencyUpdateRequest;
import org.fostertogethermn.api.exception.AgencyInUseException;
import org.fostertogethermn.api.exception.AgencyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/agencies")
public class AgencyController {
    private final AgencyService agencyService;

    public AgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @GetMapping
    public List<AgencyResponse> listAgencies() {
        return agencyService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getAgencyById(@PathVariable long id) {
        Optional<AgencyResponse> response = agencyService.findById(id);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyResponse> updateAgency(@RequestBody @Valid AgencyUpdateRequest request,
            @PathVariable long id) {
        Optional<AgencyResponse> response = agencyService.update(id, request.name());
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<AgencyResponse> createAgency(@RequestBody @Valid AgencyCreateRequest request) {
        AgencyResponse response = agencyService.create(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgencyById(@PathVariable long id) {
        try {
            agencyService.delete(id);
        } catch (AgencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AgencyInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}
