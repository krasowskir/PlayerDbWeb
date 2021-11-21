package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.richard.home.service.PlayerService;
import org.richard.home.servlets.PlayerAddressServlet;
import org.richard.home.servlets.PlayerOverviewServlet;
import org.richard.home.servlets.PlayerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MyWebAppInitializer implements WebApplicationInitializer {
    Logger log = LoggerFactory.getLogger(MyWebAppInitializer.class);

    private TemplateEngine templateEngine;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.debug("entering on startup");
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext("org.richard.home");
        springContext.start();

        PlayerServlet playerServlet = springContext.getBean("playerServlet", PlayerServlet.class);
        PlayerAddressServlet addressServlet = springContext.getBean("playerAddressServlet", PlayerAddressServlet.class);
        PlayerOverviewServlet playerOverviewServlet = springContext.getBean("playerOverviewServlet", PlayerOverviewServlet.class);

        this.templateEngine = provideTemplateEngine(servletContext);
        addressServlet.setTemplateEngine(templateEngine);
        playerOverviewServlet.setTemplateEngine(templateEngine);

        ServletRegistration.Dynamic playerS = servletContext.addServlet("mein", playerServlet);
        playerS.addMapping("/mein");

        ServletRegistration.Dynamic addressS = servletContext.addServlet("address", addressServlet);
        addressS.addMapping("/address");

        ServletRegistration.Dynamic playerOverviewS = servletContext.addServlet("playerOverview", playerOverviewServlet);
        playerOverviewS.addMapping("/playerOverview");
    }

    private TemplateEngine provideTemplateEngine(ServletContext servletContext){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return  templateEngine;
    }
}
