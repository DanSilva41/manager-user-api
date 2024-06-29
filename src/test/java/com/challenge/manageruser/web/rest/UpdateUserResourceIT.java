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

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateUserResourceIT extends AbstractIntegrationTest {

    private static final String USER_V1_ENDPOINT = "/v1/user/%d";

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    UpdateUserResourceIT(final MockMvc mockMvc, final JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO for houver campos inválidos
            NÃO DEVE atualizar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/users-list.sql")
    @ParameterizedTest
    @MethodSource("invalidPayloadsUserUpdate")
    void shouldFailInUserUpdateWhenInvalidPayload(String invalidPayload, List<String> errorMessages) throws Exception {
        final var userCode = 1;
        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        code = %d
                        AND version = 1
                        """.formatted(userCode)
        ));

        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(put(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request content"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").doesNotExist())
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.messages").value(Matchers.containsInAnyOrder(errorMessages.toArray())));

        Assertions.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        code = %d
                        AND version = 2
                        """.formatted(userCode)
        ));
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO for válido e não existente com o mesmo username e email
            DEVE atualizar o usuário
            E retornar os detalhes do mesmo
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldUpdateUser() throws Exception {
        final var userCode = 1;
        final var personCode = 1;

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        code = %d
                        AND version = 1
                        """.formatted(userCode)
        ));

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        code = %d
                        AND first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """.formatted(personCode)
        ));

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "backing.person"
        ));

        final var validPayload = """
                {
                  "username": "user099",
                  "password": "strong-p@ssw0rd",
                  "department_code": 2,
                  "person": {
                    "first_name": "Johnny",
                    "last_name": "Allister",
                    "email": "john.allister@gmail.com"
                  }
                }
                """;

        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(put(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.username").value("user099"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.updated_at").exists())
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.person.code").value(1))
                .andExpect(jsonPath("$.person.first_name").value("Johnny"))
                .andExpect(jsonPath("$.person.last_name").value("Allister"))
                .andExpect(jsonPath("$.person.email").value("john.allister@gmail.com"))
                .andExpect(jsonPath("$.person.created_at").exists())
                .andExpect(jsonPath("$.person.updated_at").exists())
                .andExpect(jsonPath("$.department").exists())
                .andExpect(jsonPath("$.department.name").value("MARKETING"))
                .andExpect(jsonPath("$.department.description").value("Setor responsável pelas mídias sociais e marca da empresa"))
                .andExpect(jsonPath("$.department.created_at").exists())
                .andExpect(jsonPath("$.department.updated_at").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                            code = %d
                            AND username = 'user099'
                            AND password = 'strong-p@ssw0rd'
                            AND department_code = 2
                            AND active = true
                            AND version = 2
                        """.formatted(userCode)
        ));


        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        code = %d
                        AND first_name = 'Johnny'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """.formatted(personCode)
        ));

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "backing.person"
        ));
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO for válido porém com username já existente
            NÃO DEVE atualizar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldFailInUserUpdateWhenAlreadyExistsUserByUsername() throws Exception {
        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username IN ('user001', 'paulwalker')
                        AND version = 1
                        """
        ));

        final var validPayload = """
                {
                  "username": "user001",
                  "password": "p@ssw0rd",
                  "department_code": 1,
                  "person": {
                    "first_name": "Paul",
                    "last_name": "Walker",
                    "email": "paul.walker@gmail.com"
                  }
                }
                """;

        final var userCode = 2;
        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(put(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("There's already a user with this username or person email"))
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username IN ('user001', 'paulwalker')
                        AND version = 1
                        """
        ));
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO for válido porém com email já existente
            NÃO DEVE atualizar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldFailInUserUpdateWhenAlreadyExistsUserByEmail() throws Exception {
        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        email IN ('john.allister@gmail.com', 'paul.walker@gmail.com')
                        AND version = 1
                        """
        ));

        final var validPayload = """
                {
                  "username": "paulwalker",
                  "password": "p@ssw0rd",
                  "department_code": 1,
                  "person": {
                    "first_name": "Paul",
                    "last_name": "Walker",
                    "email": "john.allister@gmail.com"
                  }
                }
                """;

        final var userCode = 2;
        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(put(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("There's already a user with this username or person email"))
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        email IN ('john.allister@gmail.com', 'paul.walker@gmail.com')
                        AND version = 1
                        """
        ));
    }

    @DisplayName("""
            DADO uma atualização de usuário
            QUANDO for válido porém com identificador de departamento não existente
            NÃO DEVE atualizar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/users-list.sql")
    @Test
    void shouldFailInUserUpdateWhenDepartmentNotFound() throws Exception {
        final var userCode = 1;
        final var currentDepartmentCode = 1;
        final var desirableDepartmentCode = 3;

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        code = %d
                        AND department_code = %d
                        AND version = 1
                        """.formatted(userCode, currentDepartmentCode)
        ));

        final var validPayload = """
                {
                  "username": "user001",
                  "password": "p@ssw0rd",
                  "department_code": %d,
                  "person": {
                    "first_name": "John",
                    "last_name": "Allister",
                    "email": "john.allister@gmail.com"
                  }
                }
                """.formatted(desirableDepartmentCode);

        final var formattedUrl = USER_V1_ENDPOINT.formatted(userCode);
        mockMvc.perform(put(formattedUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Department %d not found".formatted(desirableDepartmentCode)))
                .andExpect(jsonPath("$.instance").value(formattedUrl))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        code = %d
                        AND department_code = %d
                        AND version = 1
                        """.formatted(userCode, currentDepartmentCode)
        ));
    }

    private static Stream<Arguments> invalidPayloadsUserUpdate() {
        return Stream.of(
                Arguments.of("""
                                {
                                  "username": "  ",
                                  "password": "  ",
                                  "department_code": -1,
                                  "person": {
                                    "first_name": "  ",
                                    "last_name": "  ",
                                    "email": "  "
                                  }
                                }
                                """,
                        List.of(
                                "username: Cannot be empty",
                                "username: Must have between 5 and 20 characters",
                                "password: Cannot be empty",
                                "password: Must have between 6 and 60 characters",
                                "department_code: must be greater than 0",
                                "person.first_name: Cannot be empty",
                                "person.last_name: Cannot be empty",
                                "person.email: Cannot be empty",
                                "person.email: Must have between 5 and 100 characters",
                                "person.email: must be a well-formed email address"
                        )
                ),
                Arguments.of("""
                                {
                                  "username": null,
                                  "password": null,
                                  "department_code": null,
                                  "person": null
                                }
                                """,
                        List.of(
                                "username: Cannot be empty",
                                "password: Cannot be empty",
                                "department_code: Cannot be null",
                                "person: Cannot be null"
                        )
                ),
                Arguments.of("""
                                {
                                  "username": "user",
                                  "password": "p@ssw",
                                  "department_code": -1,
                                  "person": {
                                    "first_name": "J",
                                    "last_name": "A",
                                    "email": "john"
                                  }
                                }
                                """,
                        List.of(
                                "username: Must have between 5 and 20 characters",
                                "password: Must have between 6 and 60 characters",
                                "department_code: must be greater than 0",
                                "person.first_name: Must have between 2 and 60 characters",
                                "person.last_name: Must have between 2 and 60 characters",
                                "person.email: Must have between 5 and 100 characters",
                                "person.email: must be a well-formed email address"
                        )
                ),
                Arguments.of("""
                                {
                                  "username": "S!Uz-%nqgmK%uQ4QC2&)m",
                                  "password": "4G;KU5;2SZYRu,XBey3am+[F0du1hf8bUw_,*n@YqXiqC2T*L_c[$_V?6a,-*",
                                  "department_code": -1,
                                  "person": {
                                    "first_name": "mbJqEanCYNxdVPdbqdpdJvjeWTXnGhVbUqTYwtHVjYQnaQcLnvPDqnwDkSUXN",
                                    "last_name": "cNrgjjURHcfYeaPSFqLBdqKqHaywpYDYCMUpEkwWiHqDSRNXnFLeGEPBGXFXx",
                                    "email": "&u(MU&a+/ugumc:qwMm:&.iYghgTMB(D@Zu-_kCeQNTuTJ.]iNBn=!dudQyezUQeujr}fkW;$.Gijjxt:];)va(VCi?Yx}wixF+_E"
                                  }
                                }
                                """,
                        List.of(
                                "username: Must have between 5 and 20 characters",
                                "password: Must have between 6 and 60 characters",
                                "department_code: must be greater than 0",
                                "person.first_name: Must have between 2 and 60 characters",
                                "person.last_name: Must have between 2 and 60 characters",
                                "person.email: Must have between 5 and 100 characters",
                                "person.email: must be a well-formed email address"
                        )
                )
        );
    }
}
