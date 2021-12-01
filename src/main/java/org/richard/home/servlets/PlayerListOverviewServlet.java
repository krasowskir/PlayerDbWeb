package org.richard.home.servlets;

import org.richard.home.model.Player;
import org.richard.home.model.dto.PlayerWithAddress;
import org.richard.home.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class PlayerListOverviewServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PlayerListOverviewServlet.class);

    private PlayerService playerService;
    private TemplateEngine templateEngine;

    @Autowired
    public PlayerListOverviewServlet(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<PlayerWithAddress> playerList = this.playerService.getAllPlayers().get();

        WebContext context = new WebContext(req, resp, getServletContext());
        context.setVariable("allPlayers", playerList);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        templateEngine.process("playerListOverview", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

}
