package org.richard.home.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.richard.home.model.Player;
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
import java.io.PrintWriter;

@Component
public class PlayerAddressServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PlayerServlet.class);

    private ObjectMapper objectMapper;
    private PlayerService playerService;
    private TemplateEngine templateEngine;

    @Autowired
    public PlayerAddressServlet(ObjectMapper objectMapper, PlayerService playerService) {
        this.objectMapper = objectMapper;
        this.playerService = playerService;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("req reached PlayerAddressServlet {}", req.getParameter("playerName"));
        if (req.getParameter("playerName") != null) {
            try (OutputStream out = resp.getOutputStream()) {
                try (PrintWriter pout = new PrintWriter(out)) {
                    String playerName = req.getParameter("playerName");
                    Player foundPlayer = playerService.fetchSinglePlayer(playerName);
                    log.debug("foundPlayer {}", foundPlayer);

                    WebContext context = new WebContext(req, resp, getServletContext());
                    context.setVariable("player", foundPlayer);
                    templateEngine.process("players", context, pout);
                }
            }
        }
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
