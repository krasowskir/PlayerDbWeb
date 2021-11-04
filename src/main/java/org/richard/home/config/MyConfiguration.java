package org.richard.home.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.richard.home.dao.PostgresPlayerDAO;
import org.richard.home.service.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MyConfiguration {

    @Bean(name = "dataSource")
    public DataSource myDataSource(){
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser("richard");
        dataSource.setPassword("test123");
        dataSource.setUrl("jdbc:postgresql://meinedb:5432/playerdb");
        return dataSource;
    }

    @Bean
    public PostgresPlayerDAO playerDAO(){
        return new PostgresPlayerDAO(myDataSource());
    }

    @Bean
    public PlayerService playerService(){
        return new PlayerService(playerDAO());
    }
}
