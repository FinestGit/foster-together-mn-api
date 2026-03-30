package org.fostertogethermn.api.agency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.fostertogethermn.api.agency.dto.AgencyResponse;
import org.fostertogethermn.api.exception.AgencyInUseException;
import org.fostertogethermn.api.exception.AgencyNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
public class AgencyServiceTest {
    @Mock
    private JdbcTemplate template;

    @InjectMocks
    private AgencyService agencyService;

    @Test
    void findAll_returnsAgenciesInOrder() {
        // Arrange
        OffsetDateTime created = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime updated = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        AgencyResponse agency1 = new AgencyResponse(1L, "Agency 1", created, updated);
        AgencyResponse agency2 = new AgencyResponse(2L, "Agency 2", created, updated);
        AgencyResponse agency3 = new AgencyResponse(3L, "Agency 3", created, updated);
        List<AgencyResponse> expected = List.of(agency1, agency2, agency3);
        when(template.query(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any())).thenReturn(expected);

        // Act
        List<AgencyResponse> result = agencyService.findAll();

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findById_returnsAgency() {
        // Arrange
        long id = 1;
        OffsetDateTime created = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime updated = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        AgencyResponse agency = new AgencyResponse(id, "Agency 1", created, updated);
        when(template.queryForObject(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any(), anyLong()))
                .thenReturn(agency);

        // Act
        Optional<AgencyResponse> result = agencyService.findById(id);

        // Assert
        assertThat(result).isEqualTo(Optional.of(agency));
    }

    @Test
    void findById_returnsEmpty() {
        // Arrange
        long id = 1;
        when(template.queryForObject(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any(), anyLong()))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act
        Optional<AgencyResponse> result = agencyService.findById(id);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void create_returnsAgency() {
        // Arrange
        String name = "test agency";
        OffsetDateTime created = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime updated = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        AgencyResponse agency = new AgencyResponse(1L, name, created, updated);
        when(template.queryForObject(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any(), anyString()))
                .thenReturn(agency);

        // Act
        AgencyResponse result = agencyService.create(name);

        // Assert
        assertThat(result).isEqualTo(agency);
    }

    @Test
    void update_returnsAgency() {
        // Arrange
        long id = 1;
        String name = "test agency";
        OffsetDateTime created = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime updated = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        AgencyResponse agency = new AgencyResponse(id, name, created, updated);
        when(template.queryForObject(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any(), anyString(),
                anyLong()))
                .thenReturn(agency);

        // Act
        Optional<AgencyResponse> result = agencyService.update(id, name);

        // Assert
        assertThat(result).isEqualTo(Optional.of(agency));
    }

    @Test
    void update_returnsEmpty() {
        // Arrange
        long id = 1;
        String name = "test agency";
        when(template.queryForObject(anyString(), ArgumentMatchers.<RowMapper<AgencyResponse>>any(), anyString(),
                anyLong()))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act
        Optional<AgencyResponse> result = agencyService.update(id, name);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void delete_returnsNoContent() {
        // Arrange
        long id = 1;
        when(template.update(anyString(), anyLong())).thenReturn(1);

        // Act
        agencyService.delete(id);

        // Assert
        verify(template, times(1)).update(anyString(), anyLong());
    }

    @Test
    void delete_throwsAgencyNotFoundException() {
        // Arrange
        long id = 1;
        when(template.update(anyString(), anyLong())).thenReturn(0);

        // Act / Assert
        assertThatThrownBy(() -> agencyService.delete(id)).isInstanceOf(AgencyNotFoundException.class);
    }

    @Test
    void delete_throwsAgencyInUseException() {
        // Arrange
        long id = 1;
        when(template.update(anyString(), anyLong())).thenThrow(new DataIntegrityViolationException("Agency in use"));

        // Act / Assert
        assertThatThrownBy(() -> agencyService.delete(id)).isInstanceOf(AgencyInUseException.class);
    }
}
