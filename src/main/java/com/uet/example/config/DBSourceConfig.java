package com.uet.example.config;

import com.uet.example.domain.group.GroupEntity;
import com.uet.example.domain.user.UserEntity;
import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

import java.io.InputStream;
import java.util.Properties;

/**
 * hibernate.connection.provider_class: Định nghĩa Hibernate sử dụng HikariConnectionProvider để quản lý kết nối.
 *
 * hibernate.hikari.dataSourceClassName: Lớp trình điều khiển JDBC được sử dụng để tạo đối tượng kết nối của HikariCP. Ví dụ, nếu bạn sử dụng MySQL, bạn có thể đặt giá trị là com.mysql.cj.jdbc.MysqlDataSource.
 *
 * hibernate.hikari.dataSource.url: URL kết nối tới cơ sở dữ liệu.
 *
 * hibernate.hikari.dataSource.user: Tên người dùng kết nối tới cơ sở dữ liệu.
 *
 * hibernate.hikari.dataSource.password: Mật khẩu kết nối tới cơ sở dữ liệu.
 *
 * hibernate.hikari.minimumIdle: Số lượng kết nối tối thiểu được giữ trong bể kết nối.
 *
 * hibernate.hikari.maximumPoolSize: Số lượng kết nối tối đa trong bể kết nối.
 *
 * hibernate.hikari.idleTimeout: Thời gian tối đa một kết nối được giữ trong bể kết nối khi không có hoạt động nào xảy ra.
 *
 * hibernate.hikari.maxLifetime: Thời gian tối đa một kết nối được giữ trong bể kết nối.
 *
 * hibernate.hikari.connectionTimeout: Thời gian tối đa để kết nối tới cơ sở dữ liệu.
 *
 * hibernate.hikari.validationTimeout: Thời gian tối đa để kiểm tra tính khả dụng của một kết nối.
 *
 * hibernate.hikari.leakDetectionThreshold: Thời gian tối đa để xác định một kết nối đã rò rỉ bộ nhớ.
 */

public class DBSourceConfig {

    private final ConfigLoader configLoader;

    public DBSourceConfig(ConfigLoader configLoader) { this.configLoader = configLoader; }

    public SessionFactory makeSessionFactory() {
        // Hibernate core config
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.provider_class", HikariCPConnectionProvider.class.getName());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.dialect2", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.show_sql", "true");

        // Hibernate with HikariCP config
        configuration.setProperty("hibernate.hikari.dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        configuration.setProperty("hibernate.hikari.dataSource.url", configLoader.db.url);
        configuration.setProperty("hibernate.hikari.dataSource.user", configLoader.db.username);
        configuration.setProperty("hibernate.hikari.dataSource.password", configLoader.db.password);
        configuration.setProperty("hibernate.hikari.maximumPoolSize", "10");
        configuration.setProperty("hibernate.hikari.minimumIdle", "2");

        // HikariCP config is only for MySQL
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        configuration.setProperty("hibernate.hikari.dataSource.cachePrepStmts", "true");
        // giới hạn số lượng prepared statement được lưu trữ trong bộ nhớ cache
        configuration.setProperty("hibernate.hikari.dataSource.prepStmtCacheSize", "250");
        // giới hạn kích thước của chuỗi SQL được lưu trong bộ nhớ cache của prepared statement.
        configuration.setProperty("hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
        configuration.setProperty("hibernate.hikari.dataSource.useServerPrepStmts", "true");
        configuration.setProperty("hibernate.hikari.dataSource.useLocalSessionState", "true");
        configuration.setProperty("hibernate.hikari.dataSource.rewriteBatchedStatements", "true");
        configuration.setProperty("hibernate.hikari.dataSource.cacheResultSetMetadata", "true");
        configuration.setProperty("hibernate.hikari.dataSource.cacheServerConfiguration", "true");
        configuration.setProperty("hibernate.hikari.dataSource.elideSetAutoCommits", "true");
        configuration.setProperty("hibernate.hikari.dataSource.maintainTimeStats", "true");

        // Register Record using annotation
        configuration.addAnnotatedClass(UserEntity.class);
        configuration.addAnnotatedClass(GroupEntity.class);

        return configuration.buildSessionFactory();
    }

    @SneakyThrows
    public SessionFactory loadPropertiesFile() {
        Properties props = new Properties();

        InputStream is = getClass().getClassLoader().getResourceAsStream("app.properties");
        props.load(is);

        Configuration config = new Configuration();
        config.addProperties(props);
        config.addAnnotatedClass(UserEntity.class);
        config.addAnnotatedClass(GroupEntity.class);

        return config.buildSessionFactory();
    }
}
