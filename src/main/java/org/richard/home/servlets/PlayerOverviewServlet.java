package org.richard.home.servlets;

import org.richard.home.model.dto.PlayerWithAddress;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class PlayerOverviewServlet extends HttpServlet {

    private TemplateEngine templateEngine;

    public PlayerOverviewServlet() {
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        WebContext context = new WebContext(req, resp, getServletContext());
        context.setVariable("model", (PlayerWithAddress)session.getAttribute("model"));
        templateEngine.process("playerOverview", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
