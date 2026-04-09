package com.example.dianping.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class UserAccountSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UserAccountSchemaMigration.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public UserAccountSchemaMigration(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!columnExists("user_accounts", "password_hash")) {
            jdbcTemplate.execute("ALTER TABLE user_accounts ADD COLUMN password_hash VARCHAR(255)");
            log.info("已为 user_accounts 表补充 password_hash 字段");
        }
    }

    private boolean columnExists(String tableName, String columnName) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            String catalog = connection.getCatalog();

            if (hasColumn(metadata, catalog, tableName, columnName)) {
                return true;
            }

            return hasColumn(metadata, catalog, tableName.toUpperCase(), columnName.toUpperCase());
        }
    }

    private boolean hasColumn(DatabaseMetaData metadata, String catalog, String tableName, String columnName) throws Exception {
        try (ResultSet resultSet = metadata.getColumns(catalog, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
