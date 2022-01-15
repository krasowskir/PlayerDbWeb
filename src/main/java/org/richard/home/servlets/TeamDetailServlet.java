package org.richard.home.servlets;

import org.richard.home.model.Player;
import org.richard.home.model.Team;
import org.richard.home.service.TeamService;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class TeamDetailServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamDetailServlet.class);

    private TemplateEngine templateEngine;
    private TeamService teamService;

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    public TeamDetailServlet(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("received reaqquest on TeamDetailServlet GET endpoint");

        List<Team> foundTeams = teamService.getAllTeams();
        Team bayernMuenchen = foundTeams.get(0);
        List<Player> players = teamService.getPlayersOfTeam(bayernMuenchen.getId());
        String logoBase64Encoded = "data:image/png;base64, " + Base64.getEncoder().encodeToString(bayernMuenchen.getLogo());
        WebContext context = new WebContext(req, resp, getServletContext());
        context.setVariable("team", bayernMuenchen);
        context.setVariable("logoBase64Encoded", logoBase64Encoded);
        context.setVariable("players", players);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        templateEngine.process("teamDetail", context, resp.getWriter());
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
