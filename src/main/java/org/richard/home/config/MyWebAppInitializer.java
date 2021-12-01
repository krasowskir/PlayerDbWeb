package org.richard.home.config;

import org.richard.home.servlets.PlayerAddressServlet;
import org.richard.home.servlets.PlayerDetailServlet;
import org.richard.home.servlets.PlayerListOverviewServlet;
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

        PlayerServlet playerServlet = springContext.getBean(PlayerServlet.class);
        PlayerAddressServlet addressServlet = springContext.getBean(PlayerAddressServlet.class);
        PlayerDetailServlet playerDetailServlet = springContext.getBean(PlayerDetailServlet.class);
        PlayerListOverviewServlet playerListOverviewServlet = springContext.getBean(PlayerListOverviewServlet.class);

        this.templateEngine = provideTemplateEngine(servletContext);
        addressServlet.setTemplateEngine(templateEngine);
        playerDetailServlet.setTemplateEngine(templateEngine);
        playerListOverviewServlet.setTemplateEngine(templateEngine);

        ServletRegistration.Dynamic playerS = servletContext.addServlet("mein", playerServlet);
        playerS.addMapping("/mein");

        ServletRegistration.Dynamic addressS = servletContext.addServlet("address", addressServlet);
        addressS.addMapping("/address");

        ServletRegistration.Dynamic playerOverviewS = servletContext.addServlet("playerOverview", playerDetailServlet);
        playerOverviewS.addMapping("/playerOverview");

        ServletRegistration.Dynamic playerListOverviewS = servletContext.addServlet("playerListOverviewServlet", playerListOverviewServlet);
        playerListOverviewS.addMapping("/playerListOverview");
    }

    private TemplateEngine provideTemplateEngine(ServletContext servletContext){
        var templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCharacterEncoding("utf-8");
        templateResolver.setCacheable(true);

        var templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return  templateEngine;
    }
}
