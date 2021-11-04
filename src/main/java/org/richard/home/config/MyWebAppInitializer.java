package org.richard.home.config;

import org.richard.home.service.PlayerService;
import org.richard.home.servlets.MeinServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;

public class MyWebAppInitializer implements WebApplicationInitializer {
    Logger log = LoggerFactory.getLogger(MyWebAppInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.debug("entering on startup");
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext("org.richard.home");
        springContext.start();

        DataSource myDataSource = (DataSource) springContext.getBean("dataSource");
        if (myDataSource == null) log.debug("dataSource is null");
        PlayerService playerService = (PlayerService) springContext.getBean("playerService");
        if (playerService == null) log.debug("playerService is null");

        MeinServlet meinServlet = new MeinServlet();
        meinServlet.setPlayerService(playerService);
        ServletRegistration.Dynamic result = servletContext.addServlet("mein", meinServlet);
        result.addMapping("/mein");
    }
}
