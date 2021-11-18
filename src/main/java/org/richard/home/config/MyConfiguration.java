package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.richard.home.dao.AddressDAO;
import org.richard.home.dao.PlayerDAO;
import org.richard.home.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MyConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("cookbook");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 256);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://slave:5432/playerdb");
        dataSource.setUsername("richard");
        dataSource.setPassword("test123");

        return dataSource;
    }

    @Bean(name = "hikariDataSource")
    public DataSource hikari(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("cookbook");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 256);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://meinedb:5432/playerdb");
        dataSource.setUsername("richard");
        dataSource.setPassword("test123");

        return dataSource;
    }

    @Bean
    @Autowired
    public PlayerService playerService(PlayerDAO playerDAO, AddressDAO addressDAO){
        return new PlayerService(playerDAO, addressDAO);
    }
}
