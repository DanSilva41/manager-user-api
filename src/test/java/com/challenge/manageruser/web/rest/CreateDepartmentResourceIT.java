package com.challenge.manageruser.web.rest;

import com.challenge.manageruser.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateDepartmentResourceIT extends AbstractIntegrationTest {

    private static final String DEPARTMENT_V1_ENDPOINT = "/v1/department";

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    CreateDepartmentResourceIT(final MockMvc mockMvc, final JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @DisplayName("""
            DADO um novo departamento
            QUANDO for houver campos inválidos
            NÃO DEVE criar o departamento
            E retornar 400 (BAD REQUEST)
            """)
    @ParameterizedTest
    @MethodSource("invalidPayloadsDepartmentCreation")
    void shouldFailInDepartmentCreationWhenInvalidPayload(String invalidPayload, List<String> errorMessages) throws Exception {
        mockMvc.perform(post(DEPARTMENT_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request content"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").doesNotExist())
                .andExpect(jsonPath("$.instance").value(DEPARTMENT_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.messages").value(Matchers.containsInAnyOrder(errorMessages.toArray())));

        Assertions.assertEquals(0, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "backing.department"
        ));
    }

    @DisplayName("""
            DADO um novo departamento
            QUANDO for válido e não existente com o mesmo nome
            DEVE criar o departamento
            E retornar os detalhes do mesmo
            """)
    @Test
    void shouldCreateNewDepartment() throws Exception {
        final var validPayload = """
                {
                  "name": "Financeiro",
                  "description": "Setor responsável pela contabilidade e prestação de contas"
                }
                """;

        mockMvc.perform(post(DEPARTMENT_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("FINANCEIRO"))
                .andExpect(jsonPath("$.description").value("Setor responsável pela contabilidade e prestação de contas"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.department",
                """
                        name = 'FINANCEIRO'
                        """
        ));
    }

    @DisplayName("""
            DADO um novo departamento
            QUANDO for válido porém com nome já existente
            NÃO DEVE criar o departamento
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/already-existent-name-department.sql")
    @Test
    void shouldFailInDepartmentCreationWhenAlreadyExistsDepartmentByName() throws Exception {
        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.department",
                """
                        name = 'FINANCEIRO'
                        """
        ));

        final var validPayload = """
                {
                  "name": "Financeiro",
                  "description": "Setor responsável pela contabilidade e prestação de contas"
                }
                """;

        mockMvc.perform(post(DEPARTMENT_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("There's already a department with this name"))
                .andExpect(jsonPath("$.instance").value(DEPARTMENT_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.department",
                """
                        name = 'FINANCEIRO'
                        """
        ));
    }

    private static Stream<Arguments> invalidPayloadsDepartmentCreation() {
        return Stream.of(
                Arguments.of("""
                                {
                                  "name": "  ",
                                  "description": "  "
                                }
                                """,
                        List.of(
                                "name: Cannot be empty",
                                "name: Must have between 3 and 60 characters",
                                "description: Cannot be empty",
                                "description: Must have between 5 and 100 characters"
                        )
                ),
                Arguments.of("""
                                {
                                  "name": null,
                                  "description": null
                                }
                                """,
                        List.of(
                                "name: Cannot be empty",
                                "description: Cannot be empty"
                        )
                ),
                Arguments.of("""
                                {
                                  "name": "FN",
                                  "description": "Algo"
                                }
                                """,
                        List.of(
                                "name: Must have between 3 and 60 characters",
                                "description: Must have between 5 and 100 characters"
                        )
                ),
                Arguments.of("""
                                {
                                  "name": "MJDTMgBnJJiXRrRSjmjmijQgCkZBJUSkxiNmaiiQqbtKEjZuESUDKygmgtiHQ",
                                  "description": " Integer lobortis tincidunt velit ut sollicitudin. Suspendisse congue odio a orci euismod, nec ultrices urna sollicitudin."
                                }
                                """,
                        List.of(
                                "name: Must have between 3 and 60 characters",
                                "description: Must have between 5 and 100 characters"
                        )
                )
        );
    }
}
