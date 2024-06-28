package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetUserResourceIT extends AbstractIntegrationTest {

    private static final String USER_V1_ENDPOINT = "/v1/user/%d";

    private final MockMvc mockMvc;

    @Autowired
    GetUserResourceIT(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("""
            DADO uma busca de usuário por identificador único
            QUANDO for encontrado
            DEVE retornar informações detalhadas do mesmo
            E retornar 200 (OK)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldGetUserByCode() throws Exception {
        final var userCode = 1;
        mockMvc.perform(get(USER_V1_ENDPOINT.formatted(userCode))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.username").value("user001"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.person.code").value(1))
                .andExpect(jsonPath("$.person.first_name").value("John"))
                .andExpect(jsonPath("$.person.last_name").value("Allister"))
                .andExpect(jsonPath("$.person.email").value("john.allister@gmail.com"))
                .andExpect(jsonPath("$.person.created_at").exists())
                .andExpect(jsonPath("$.person.updated_at").exists())
                .andExpect(jsonPath("$.department").exists())
                .andExpect(jsonPath("$.department.name").value("FINANCEIRO"))
                .andExpect(jsonPath("$.department.description").value("Setor responsável pela contabilidade e prestação de contas"))
                .andExpect(jsonPath("$.department.created_at").exists())
                .andExpect(jsonPath("$.department.updated_at").exists());
    }

    @DisplayName("""
            DADO uma busca de usuário por identificador único
            QUANDO não for encontrado
            DEVE lançar exceção específica
            E retornar 404 (NOT FOUND)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldThrowWhenUserNotFoundByCode() throws Exception {
        final var userCode = 3;
        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(get(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User with identifier %d not found".formatted(userCode)))
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
