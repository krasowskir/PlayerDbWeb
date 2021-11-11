package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.richard.home.service.PlayerService;
import org.richard.home.servlets.PlayerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MyWebAppInitializer implements WebApplicationInitializer {
    Logger log = LoggerFactory.getLogger(MyWebAppInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.debug("entering on startup");
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext("org.richard.home");
        springContext.start();

        Object myDataSource = springContext.getBean("hikariDataSource", HikariDataSource.class);
        if (myDataSource == null) log.error("dataSource is null");
        PlayerService playerService = (PlayerService) springContext.getBean("playerService");
        if (playerService == null) log.error("playerService is null");
        ObjectMapper objectMapper = (ObjectMapper)springContext.getBean("objectMapper", ObjectMapper.class);
        if (objectMapper == null) log.error("objectMapper is null");

        PlayerServlet playerServlet = new PlayerServlet();
        playerServlet.setPlayerService(playerService);
        playerServlet.setObjectMapper(objectMapper);
        ServletRegistration.Dynamic result = servletContext.addServlet("mein", playerServlet);
        result.addMapping("/mein");
    }
}
