package org.richard.home.config;

import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.richard.home.dao.PlayerDAO;
import org.richard.home.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MyConfiguration {

    @Bean(name = "dataSourceMulti")
    public DataSource multiThreadDataSource(){
        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        dataSource.setServerName("localhost");
        dataSource.setInitialConnections(3);
        dataSource.setMaxConnections(10);
        dataSource.setLoginTimeout(10);
        dataSource.setUser("richard");
        dataSource.setPassword("test123");
        dataSource.setUrl("jdbc:postgresql://meinedb:5432/playerdb");
        return dataSource;
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

    @Bean(name = "dataSourceSingle")
    public DataSource singleThreadDataSource(){
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser("richard");
        dataSource.setPassword("test123");
        dataSource.setUrl("jdbc:postgresql://meinedb:5432/playerdb");
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

    //@Qualifier("multiThreadDataSource") funktioniert nicht
//    @Bean
//    public PostgresPlayerDAO playerDAO( DataSource dataSource){
//        return new PostgresPlayerDAO(dataSource);
//    }

    @Bean
    @Autowired
    public PlayerService playerService(PlayerDAO playerDAO){
        return new PlayerService(playerDAO);
    }
}
