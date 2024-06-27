package com.challenge.manageruser.config;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PostgreSQLCleaner implements AfterEachCallback {

    private static final String TRUNCATE = "TRUNCATE $1 CASCADE";

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        cleaningDatabase(extensionContext);
    }

    private void cleaningDatabase(final ExtensionContext extensionContext) {
        var jdbcTemplate = SpringExtension.getApplicationContext(extensionContext).getBean(JdbcTemplate.class);
        var tables = jdbcTemplate.queryForList(getSelect(extensionContext), String.class);
        tables.forEach(table -> tryTruncate(jdbcTemplate, table, 1));
    }

    private String getSelect(final ExtensionContext extensionContext) {
        var environment = SpringExtension.getApplicationContext(extensionContext).getBean(Environment.class);

        final var schemasForTruncate = collectToString(getProperty(environment, "test.schemas-for-truncate"));
        final var tablesIgnoredOnTruncate = collectToString(getProperty(environment, "test.tables-ignored-on-truncate"));
        return """
                 SELECT CONCAT(table_schema, '.',table_name)
                 FROM information_schema.tables
                 WHERE table_schema IN (%s)
                 AND table_name NOT IN (%s)
                 ORDER BY table_name
                """.formatted(schemasForTruncate, tablesIgnoredOnTruncate);
    }

    private static void tryTruncate(final JdbcTemplate jdbcTemplate, final String table, final int attempt) {
        try {
            jdbcTemplate.execute(TRUNCATE.replace("$1", table));
        } catch (DataAccessException e) {
            if (attempt >= 3) {
                throw e;
            }
            tryTruncate(jdbcTemplate, table, attempt + 1);
        }
    }

    private List<?> getProperty(final Environment environment, final String keyProperty) {
        return assertDoesNotThrow(() ->
                Optional.ofNullable(environment.getProperty(keyProperty, List.class))
                        .orElseThrow(() ->
                                new IllegalStateException("Property %s not mapped in application-test.yml. Check to run integrated tests correctly!".formatted(keyProperty))
                        )
        );
    }

    private String collectToString(final List<?> values) {
        return values.stream()
                .map(Object::toString)
                .collect(Collectors.joining("','", "'", "'"));
    }
}
