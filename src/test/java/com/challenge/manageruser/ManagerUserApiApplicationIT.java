package com.challenge.manageruser;

import com.challenge.manageruser.repository.UserRepository;
import com.challenge.manageruser.service.ManageUserService;
import com.challenge.manageruser.web.rest.UserResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerUserApiApplicationIT extends AbstractIntegrationTest {

    private static final String HEALTH_ENDPOINT = "/actuator/health";

    private final MockMvc mockMvc;

    @Autowired
    ManagerUserApiApplicationIT(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("""
            DADO o início da aplicação
            QUANDO iniciar sem falhas
            DEVE carregar o contexto
            E os componentes principais
            """)
    @Test
    void shouldContextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
        assertThat(context.getBean(UserRepository.class)).isNotNull();
        assertThat(context.getBean(ManageUserService.class)).isNotNull();
        assertThat(context.getBean(UserResource.class)).isNotNull();
    }

    @DisplayName("""
            DADO o início da aplicação
            QUANDO iniciar sem falhas
            DEVE expor o endpoint /actuator/health com sucesso (OK - 200)
            E os demais componentes com sucesso (UP)
            """)
    @Test
    void shouldUpAppWithHealth() throws Exception {
        mockMvc.perform(get(HEALTH_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.db").exists())
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.db.details").exists())
                .andExpect(jsonPath("$.components.db.details.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.components.db.details.validationQuery").value("isValid()"))
                .andExpect(jsonPath("$.components.ping").exists())
                .andExpect(jsonPath("$.components.ping.status").value("UP"));
    }

}
