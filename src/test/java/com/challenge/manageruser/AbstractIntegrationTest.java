package com.challenge.manageruser;

import com.challenge.manageruser.config.PostgreSQLCleaner;
import com.challenge.manageruser.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ExtendWith({PostgreSQLCleaner.class})
@SpringBootTest
public class AbstractIntegrationTest {
}
