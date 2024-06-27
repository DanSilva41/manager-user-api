package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetUsersResourceIT extends AbstractIntegrationTest {

    private static final String USER_V1_ENDPOINT = "/v1/user";

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    GetUsersResourceIT(final MockMvc mockMvc, final JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @DisplayName("""
            DADO uma busca de usuários
            QUANDO os parâmetros de busca forem inválido
            DEVE retornar uma lista paginada vazia
            E retornar 200 (OK)
            """)
    @ParameterizedTest
    @MethodSource("invalidPayloadsGetUsers")
    void shouldFailInUsersSearchWhenInvalidParameters(Integer page, Integer size, List<String> errorMessages) throws Exception {
        mockMvc.perform(get(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", page.toString())
                        .queryParam("size", size.toString())
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request content"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").doesNotExist())
                .andExpect(jsonPath("$.instance").value(USER_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.messages").value(Matchers.containsInAnyOrder(errorMessages.toArray())));
    }

    @DisplayName("""
            DADO uma busca de usuários
            QUANDO não forem encontrados usuários para os parâmetros informados
            DEVE retornar uma lista paginada vazia
            E retornar 200 (OK)
            """)
    @Test
    void shouldGetEmptyUsersList() throws Exception {
        mockMvc.perform(get(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", "0")
                        .queryParam("size", "100")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(100))
                .andExpect(jsonPath("$.page.total_elements").value(0))
                .andExpect(jsonPath("$.page.total_pages").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @DisplayName("""
            DADO uma busca de usuários
            QUANDO forem encontrados usuários para os parâmetros informados
            DEVE retornar uma lista paginada
            E retornar 200 (OK)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldGetUsersList() throws Exception {
        mockMvc.perform(get(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.total_elements").value(2))
                .andExpect(jsonPath("$.page.total_pages").value(1))
                .andExpect(jsonPath("$.content").isNotEmpty())
                // first element
                .andExpect(jsonPath("$.content[0].code").value(1))
                .andExpect(jsonPath("$.content[0].username").value("user001"))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.content[0].full_name").value("John Allister"))
                .andExpect(jsonPath("$.content[0].department_name").value("FINANCEIRO"))
                .andExpect(jsonPath("$.content[0].created_at").value("2024-06-26T18:51:25.985665"))
                .andExpect(jsonPath("$.content[0].updated_at").value("2024-06-26T18:51:25.985683"))
                // second element
                .andExpect(jsonPath("$.content[1].code").value(2))
                .andExpect(jsonPath("$.content[1].username").value("paulwalker"))
                .andExpect(jsonPath("$.content[1].active").value(true))
                .andExpect(jsonPath("$.content[1].full_name").value("Paul Walker"))
                .andExpect(jsonPath("$.content[1].department_name").value("MARKETING"))
                .andExpect(jsonPath("$.content[1].created_at").value("2024-06-27T18:51:25.985665"))
                .andExpect(jsonPath("$.content[1].updated_at").value("2024-06-27T18:51:25.985683"));
    }

    @DisplayName("""
            DADO uma busca de usuários
            QUANDO forem encontrados usuários para os parâmetros informados limitando número da busca
            DEVE retornar uma lista paginada
            E retornar 200 (OK)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldGetUsersListWithSize() throws Exception {
        mockMvc.perform(get(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", "0")
                        .queryParam("size", "1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.total_elements").value(2))
                .andExpect(jsonPath("$.page.total_pages").value(2))
                .andExpect(jsonPath("$.content").isNotEmpty())
                // first element
                .andExpect(jsonPath("$.content[0].code").value(1))
                .andExpect(jsonPath("$.content[0].username").value("user001"))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.content[0].full_name").value("John Allister"))
                .andExpect(jsonPath("$.content[0].department_name").value("FINANCEIRO"))
                .andExpect(jsonPath("$.content[0].created_at").value("2024-06-26T18:51:25.985665"))
                .andExpect(jsonPath("$.content[0].updated_at").value("2024-06-26T18:51:25.985683"));
    }

    private static Stream<Arguments> invalidPayloadsGetUsers() {
        return Stream.of(
                Arguments.of(
                        -1, // page
                        0, // size
                        List.of(
                                "page: must be greater than or equal to 0",
                                "size: must be greater than or equal to 1"
                        )
                )
        );
    }
}
