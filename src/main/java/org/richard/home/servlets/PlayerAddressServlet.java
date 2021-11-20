package org.richard.home.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.richard.home.model.Address;
import org.richard.home.model.Country;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

@Component
public class PlayerAddressServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PlayerAddressServlet.class);

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

                    persistSessionInfosForPlayer(req, foundPlayer);

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
        log.debug("req reached doPost PlayerAddressServlet with session info: {}, {}",
                req.getSession().getAttribute("name"), req.getSession().getAttribute("alter"));

        try (OutputStream out = resp.getOutputStream()) {

            HttpSession session = req.getSession();
            String name = String.valueOf(session.getAttribute("name"));
            Integer alter = Integer.parseInt(String.valueOf(session.getAttribute("alter")));
            log.debug("player with name {} and alter {} found", name, alter);

            Player foundPlayer = playerService.fetchSinglePlayer(name);

            Address tmpAddr = populateAddressWithFormData(req);
            boolean result = playerService.saveAddressForPlayer(foundPlayer, tmpAddr);

            log.debug("result success?: {}", result);

            WebContext context = new WebContext(req, resp, getServletContext());
            context.setVariable("player", foundPlayer);
            templateEngine.process("players", context, resp.getWriter());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("req reached doPut PlayerAddressServlet");

        try (OutputStream out = resp.getOutputStream()) {

            HttpSession session = req.getSession();
            String name = String.valueOf(session.getAttribute("name"));
            Integer alter = Integer.parseInt(String.valueOf(session.getAttribute("alter")));
            log.debug("player with name {} and alter {} found", name, alter);

            Player foundPlayer = playerService.fetchSinglePlayer(name);

            Address tmpAddr = populateAddressWithFormData(req);
            boolean result = playerService.saveAddressForPlayer(foundPlayer, tmpAddr);

            log.debug("result success?: {}", result);

            WebContext context = new WebContext(req, resp, getServletContext());
            context.setVariable("player", foundPlayer);
            templateEngine.process("players", context, resp.getWriter());

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    private Address populateAddressWithFormData(HttpServletRequest req){
        log.debug("req params: {}" + List.copyOf(Collections.list(req.getParameterNames())).toString());
        Address tmpAddress = new Address();
        tmpAddress.setCity(req.getParameter("city"));
        tmpAddress.setPlz(req.getParameter("plz"));
        tmpAddress.setStreet(req.getParameter("street"));
        tmpAddress.setCountry(Country.valueOf(req.getParameter("country")));
        return tmpAddress;
    }

    private void persistSessionInfosForPlayer(HttpServletRequest req, Player foundPlayer) {
        HttpSession session = req.getSession();
        if (session.getAttribute("name") == null || session.getAttribute("name").equals("")){
            session.setAttribute("name", foundPlayer.getName());
            session.setAttribute("alter", foundPlayer.getAlter());
        }
    }
}
