package org.fostertogethermn.api.agency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.fostertogethermn.api.agency.dto.AgencyResponse;
import org.fostertogethermn.api.exception.AgencyInUseException;
import org.fostertogethermn.api.exception.AgencyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AgencyService {
    private static final Logger log = LoggerFactory.getLogger(AgencyService.class);
    private final JdbcTemplate template;

    public AgencyService(JdbcTemplate template) {
        this.template = template;
    }

    public List<AgencyResponse> findAll() {
        final String query = """
                SELECT id, name, created_at, updated_at
                FROM agency
                ORDER BY id
                """;
        return template.query(query, (resultSet, rowNum) -> mapAgency(resultSet));
    }

    public Optional<AgencyResponse> findById(long id) {
        final String query = """
                SELECT id, name, created_at, updated_at
                FROM agency
                WHERE id = ?
                """;
        try {
            AgencyResponse response = template.queryForObject(query, (resultSet, rowNum) -> mapAgency(resultSet), id);
            return Optional.of(response);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Agency not found: id={}", id);
            return Optional.empty();
        }
    }

    public AgencyResponse create(String name) {
        final String query = """
                INSERT INTO agency (name)
                VALUES (?)
                RETURNING id, name, created_at, updated_at
                """;
        AgencyResponse response = template.queryForObject(query, (resultSet, rowNum) -> mapAgency(resultSet), name);
        return response;
    }

    public Optional<AgencyResponse> update(long id, String name) {
        final String query = """
                UPDATE agency
                SET name = ?, updated_at = NOW()
                WHERE id = ?
                RETURNING id, name, created_at, updated_at
                """;
        try {
            AgencyResponse response = template.queryForObject(query, (resultSet, rowNum) -> mapAgency(resultSet), name,
                    id);
            return Optional.of(response);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Agency not found: id={}", id);
            return Optional.empty();
        }
    }

    public void delete(long id) {
        final String query = """
                DELETE FROM agency
                WHERE id = ?
                """;
        try {
            int rows = template.update(query, id);
            if (rows == 0) {
                throw new AgencyNotFoundException(String.format("Agency not found: id=%d", id));
            }
        } catch (DataIntegrityViolationException e) {
            throw new AgencyInUseException("Agency in use", e);
        }
    }

    private AgencyResponse mapAgency(ResultSet resultSet) throws SQLException {
        return new AgencyResponse(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getObject("created_at", OffsetDateTime.class),
                resultSet.getObject("updated_at", OffsetDateTime.class));
    }
}
