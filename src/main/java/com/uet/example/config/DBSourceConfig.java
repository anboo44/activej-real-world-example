package com.uet.example.config;

import com.uet.example.domain.group.GroupEntity;
import com.uet.example.domain.user.UserEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

public class DBSourceConfig {

    private final ConfigLoader configLoader;

    public DBSourceConfig(ConfigLoader configLoader) { this.configLoader = configLoader; }

    public SessionFactory makeSessionFactory() {
        // Hibernate core config
        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

        // No use hikari
//        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
//        configuration.setProperty("hibernate.connection.url", configLoader.db.url);
//        configuration.setProperty("hibernate.connection.username", configLoader.db.username);
//        configuration.setProperty("hibernate.connection.password", configLoader.db.password);
//        configuration.setProperty("hibernate.show_sql", "false");
//        configuration.setProperty("org.hibernate.SQL_SLOW", "info");

        // Hibernate with HikariCP config
        configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getName());
//        configuration.setProperty("hibernate.hikari.dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        configuration.setProperty("hibernate.hikari.dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
        configuration.setProperty("hibernate.hikari.dataSource.url", configLoader.db.url);
        configuration.setProperty("hibernate.hikari.dataSource.user", configLoader.db.username);
        configuration.setProperty("hibernate.hikari.dataSource.password", configLoader.db.password);
        configuration.setProperty("hibernate.hikari.maximumPoolSize", "10");
        configuration.setProperty("hibernate.hikari.minimumIdle", "10");

        // HikariCP config is only for MySQL
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
//        configuration.setProperty("hibernate.hikari.dataSource.cachePrepStmts", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.prepStmtCacheSize", "250");
//        configuration.setProperty("hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
//        configuration.setProperty("hibernate.hikari.dataSource.useServerPrepStmts", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.useLocalSessionState", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.rewriteBatchedStatements", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.cacheResultSetMetadata", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.cacheServerConfiguration", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.elideSetAutoCommits", "true");
//        configuration.setProperty("hibernate.hikari.dataSource.maintainTimeStats", "false");

        // Register Record using annotation
        configuration.addAnnotatedClass(UserEntity.class);
        configuration.addAnnotatedClass(GroupEntity.class);

        return configuration.buildSessionFactory();
    }
}
