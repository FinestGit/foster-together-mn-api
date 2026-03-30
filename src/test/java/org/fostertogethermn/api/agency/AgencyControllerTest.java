package org.fostertogethermn.api.agency;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fostertogethermn.api.agency.dto.AgencyCreateRequest;
import org.fostertogethermn.api.agency.dto.AgencyResponse;
import org.fostertogethermn.api.agency.dto.AgencyUpdateRequest;
import org.fostertogethermn.api.exception.AgencyInUseException;
import org.fostertogethermn.api.exception.AgencyNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AgencyController.class)
public class AgencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgencyService agencyService;

    @Test
    void listAgencies_returnsOk() throws Exception {
        // Arrange
        List<AgencyResponse> mockResponse = new ArrayList<>();
        long id = 1;
        String name = "test agency";
        OffsetDateTime createdAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime updatedAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        AgencyResponse agency = new AgencyResponse(id, name, createdAt, updatedAt);
        mockResponse.add(agency);
        when(agencyService.findAll()).thenReturn(mockResponse);

        // Act
        ResultActions results = mockMvc.perform(get("/api/v1/agencies"));

        // Assert
        results.andExpect(status().isOk());
        results.andExpect(jsonPath("$[0].id").value(id));
        results.andExpect(jsonPath("$[0].name").value(name));
        results.andExpect(jsonPath("$[0].createdAt").value(createdAt.toInstant().toString()));
        results.andExpect(jsonPath("$[0].updatedAt").value(updatedAt.toInstant().toString()));
    }

    @Test
    void getAgencyById_returnsOk() throws Exception {
        // Arrange
        List<AgencyResponse> mockResponse = new ArrayList<>();
        long id = 1;
        String name = "test agency";
        OffsetDateTime createdAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime updatedAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        AgencyResponse agency = new AgencyResponse(id, name, createdAt, updatedAt);
        mockResponse.add(agency);
        when(agencyService.findById(id)).thenReturn(Optional.of(agency));

        // Act
        ResultActions results = mockMvc.perform(get("/api/v1/agencies/{id}", id));
        // Assert
        results.andExpect(status().isOk());
        results.andExpect(jsonPath("$.id").value(id));
        results.andExpect(jsonPath("$.name").value(name));
        results.andExpect(jsonPath("$.createdAt").value(createdAt.toInstant().toString()));
        results.andExpect(jsonPath("$.updatedAt").value(updatedAt.toInstant().toString()));
    }

    @Test
    void getAgencyById_returnsNotFound() throws Exception {
        // Arrange
        long id = 1;
        when(agencyService.findById(id)).thenReturn(Optional.empty());

        // Act
        ResultActions results = mockMvc.perform(get("/api/v1/agencies/{id}", id));
        // Assert
        results.andExpect(status().isNotFound());
    }

    @Test
    void createAgency_returnsCreated() throws Exception {
        // Arrange
        AgencyCreateRequest request = new AgencyCreateRequest("test agency");
        long id = 1;
        String name = "test agency";
        OffsetDateTime createdAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime updatedAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        AgencyResponse response = new AgencyResponse(id, name, createdAt, updatedAt);
        when(agencyService.create(request.name())).thenReturn(response);

        // Act
        ResultActions results = mockMvc
                .perform(post("/api/v1/agencies").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)));
        // Assert
        results.andExpect(status().isCreated());
        results.andExpect(jsonPath("$.id").value(response.id()));
        results.andExpect(jsonPath("$.name").value(response.name()));
        results.andExpect(jsonPath("$.createdAt").value(response.createdAt().toInstant().toString()));
        results.andExpect(jsonPath("$.updatedAt").value(response.updatedAt().toInstant().toString()));
    }

    @Test
    void updateAgency_returnsOk() throws Exception {
        // Arrange
        AgencyUpdateRequest request = new AgencyUpdateRequest("test agency");
        long id = 1;
        String name = "test agency";
        OffsetDateTime createdAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime updatedAt = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        AgencyResponse response = new AgencyResponse(id, name, createdAt, updatedAt);
        when(agencyService.update(id, request.name())).thenReturn(Optional.of(response));

        // Act
        ResultActions results = mockMvc
                .perform(put("/api/v1/agencies/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)));
        // Assert
        results.andExpect(status().isOk());
        results.andExpect(jsonPath("$.id").value(response.id()));
        results.andExpect(jsonPath("$.name").value(response.name()));
        results.andExpect(jsonPath("$.createdAt").value(response.createdAt().toInstant().toString()));
        results.andExpect(jsonPath("$.updatedAt").value(response.updatedAt().toInstant().toString()));
    }

    @Test
    void updateAgency_returnsNotFound() throws Exception {
        // Arrange
        AgencyUpdateRequest request = new AgencyUpdateRequest("test agency");
        long id = 1;
        when(agencyService.update(id, request.name())).thenReturn(Optional.empty());

        // Act
        ResultActions results = mockMvc
                .perform(put("/api/v1/agencies/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)));
        // Assert
        results.andExpect(status().isNotFound());
    }

    @Test
    void deleteAgencyById_returnsNoContent() throws Exception {
        // Arrange
        long id = 1;
        doNothing().when(agencyService).delete(id);

        // Act
        ResultActions results = mockMvc.perform(delete("/api/v1/agencies/{id}", id));
        // Assert
        results.andExpect(status().isNoContent());
    }

    @Test
    void deleteAgencyById_returnsNotFound() throws Exception {
        // Arrange
        long id = 1;
        doThrow(new AgencyNotFoundException(String.format("Agency not found: id=%d", id))).when(agencyService)
                .delete(id);

        // Act
        ResultActions results = mockMvc.perform(delete("/api/v1/agencies/{id}", id));
        // Assert
        results.andExpect(status().isNotFound());
    }

    @Test
    void deleteAgencyById_returnsConflict() throws Exception {
        // Arrange
        long id = 1;
        doThrow(new AgencyInUseException(String.format("Agency in use: id=%d", id))).when(agencyService).delete(id);

        // Act
        ResultActions results = mockMvc.perform(delete("/api/v1/agencies/{id}", id));
        // Assert
        results.andExpect(status().isConflict());
    }
}
