package org.richard.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.SpringServletContainerInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

@Component
public class Start extends SpringServletContainerInitializer {
    Logger log = LoggerFactory.getLogger(Start.class);

    @Override
    public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {
        log.debug("loaded SpringServletContainerInitializer");
        super.onStartup(webAppInitializerClasses, servletContext);
    }

}
