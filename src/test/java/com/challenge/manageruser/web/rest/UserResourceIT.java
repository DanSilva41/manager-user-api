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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserResourceIT extends AbstractIntegrationTest {

    private static final String USER_V1_ENDPOINT = "/v1/user";

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    UserResourceIT(final MockMvc mockMvc, final JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for houver campos inválidos
            NÃO DEVE criar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @ParameterizedTest
    @MethodSource("invalidPayloadsUserCreation")
    void shouldFailInUserCreationWhenInvalidPayload(String invalidPayload, List<String> errorMessages) throws Exception {
        mockMvc.perform(post(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request content"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").doesNotExist())
                .andExpect(jsonPath("$.instance").value(USER_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.messages").value(Matchers.containsInAnyOrder(errorMessages.toArray())));

        Assertions.assertEquals(0, JdbcTestUtils.countRowsInTable(
                jdbcTemplate,
                "security.user"
        ));
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido e não existente com o mesmo username e email
            DEVE criar o usuário
            E retornar os detalhes do mesmo
            """)
    @Sql("/db/scripts/created-departments.sql")
    @Test
    void shouldCreateNewUser() throws Exception {
        final var validPayload = """
                {
                  "username": "user001",
                  "password": "p@ssw0rd",
                  "department_name": "FINANCEIRO",
                  "person": {
                    "first_name": "John",
                    "last_name": "Allister",
                    "email": "john.allister@gmail.com"
                  }
                }
                """;

        mockMvc.perform(post(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isCreated())
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

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username = 'user001'
                        AND active = true
                        """
        ));


        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """
        ));
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido porém com username já existente
            NÃO DEVE criar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/already-existent-user.sql")
    @Test
    void shouldFailInUserCreationWhenAlreadyExistsUserByUsername() throws Exception {
        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username = 'user001'
                        AND active = true
                        """
        ));

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """
        ));

        final var validPayload = """
                {
                  "username": "user001",
                  "password": "p@ssw0rd",
                  "department_name": "FINANCEIRO",
                  "person": {
                    "first_name": "Paul",
                    "last_name": "Walker",
                    "email": "paul.walker@gmail.com"
                  }
                }
                """;

        mockMvc.perform(post(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("There's already a user with this username or person email"))
                .andExpect(jsonPath("$.instance").value(USER_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username = 'user001'
                        AND active = true
                        """
        ));

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """
        ));
    }

    @DisplayName("""
            DADO um novo usuário
            QUANDO for válido porém com email já existente
            NÃO DEVE criar o usuário
            E retornar 400 (BAD REQUEST)
            """)
    @Sql("/db/scripts/already-existent-user.sql")
    @Test
    void shouldFailInUserCreationWhenAlreadyExistsUserByEmail() throws Exception {
        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username = 'user001'
                        AND active = true
                        """
        ));

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """
        ));

        final var validPayload = """
                {
                  "username": "user001",
                  "password": "p@ssw0rd",
                  "department_name": "FINANCEIRO",
                  "person": {
                    "first_name": "John",
                    "last_name": "Allister",
                    "email": "john.allister@gmail.com"
                  }
                }
                """;

        mockMvc.perform(post(USER_V1_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("There's already a user with this username or person email"))
                .andExpect(jsonPath("$.instance").value(USER_V1_ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists());

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "security.user",
                """
                        username = 'user001'
                        AND active = true
                        """
        ));

        Assertions.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate,
                "backing.person",
                """
                        first_name = 'John'
                        AND last_name = 'Allister'
                        AND email = 'john.allister@gmail.com'
                        """
        ));
    }

    private static Stream<Arguments> invalidPayloadsUserCreation() {
        return Stream.of(
                Arguments.of("""
                                {
                                  "username": "  ",
                                  "password": "  ",
                                  "department_name": "  ",
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
                                "department_name: Cannot be empty",
                                "department_name: Must have between 3 and 60 characters",
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
                                  "department_name": null,
                                  "person": null
                                }
                                """,
                        List.of(
                                "username: Cannot be empty",
                                "password: Cannot be empty",
                                "department_name: Cannot be empty",
                                "person: Cannot be null"
                        )
                ),
                Arguments.of("""
                                {
                                  "username": "user",
                                  "password": "p@ssw",
                                  "department_name": "GL",
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
                                "department_name: Must have between 3 and 60 characters",
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
                                  "department_name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit block",
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
                                "department_name: Must have between 3 and 60 characters",
                                "person.first_name: Must have between 2 and 60 characters",
                                "person.last_name: Must have between 2 and 60 characters",
                                "person.email: Must have between 5 and 100 characters",
                                "person.email: must be a well-formed email address"
                        )
                )
        );
    }
}
