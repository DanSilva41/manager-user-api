package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteUserResourceIT extends AbstractIntegrationTest {

    private static final String USER_V1_ENDPOINT = "/v1/user/%d";

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    DeleteUserResourceIT(final MockMvc mockMvc, final JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @DisplayName("""
            DADO uma exclusão de usuário
            QUANDO for existente
            DEVE excluí-lo da base de dados
            E retornar 204 (NO CONTENT)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldDeleteUserByCode() throws Exception {
        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));

        final var userCode = 1;
        mockMvc.perform(delete(USER_V1_ENDPOINT.formatted(userCode))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));
    }

    @DisplayName("""
            DADO uma exclusão de usuário
            QUANDO não for encontrado por identificador único
            DEVE lançar exceção específica
            E retornar 404 (NOT FOUND)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldThrowWhenUserNotFoundByCode() throws Exception {
        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));

        final var userCode = 3;
        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(delete(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User with identifier %d not found".formatted(userCode)))
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));
    }
}
