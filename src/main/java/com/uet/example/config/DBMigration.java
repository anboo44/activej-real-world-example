package com.uet.example.config;

import io.activej.inject.annotation.Inject;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBMigration {

    private final ConfigLoader configLoader;

    @Inject
    public DBMigration(ConfigLoader configLoader) { this.configLoader = configLoader; }

    public void migrate() {
        try {
            log.info("Run liquibase migration....");
            var con       = DriverManager.getConnection(configLoader.db.url, configLoader.db.username, configLoader.db.password);
            var database  = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            var liquibase = new liquibase.Liquibase("db/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            con.commit();
            con.close();
            log.info("Run liquibase successfully");
        } catch (SQLException | LiquibaseException e) {
            log.error("Run liquibase error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
