package org.richard.home.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.model.Player;
import org.richard.home.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class PlayerServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private ObjectMapper objectMapper;
    private PlayerService playerService;

    @Autowired
    public PlayerServlet(ObjectMapper objectMapper, PlayerService playerService) {
        this.objectMapper = objectMapper;
        this.playerService = playerService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("=== params ===");
        req.getParameterMap()
                .forEach((key, value) -> log.debug("name: " + key + " values: " + Arrays.toString(value)));

        log.debug("=== header ==="); //deprecated shit
        Enumeration<String> headers = req.getHeaderNames();
        Iterator<String> headerIter = headers.asIterator();
        while (headerIter.hasNext()) {
            String header = headerIter.next();
            System.out.println("header: " + header + " value: " + req.getHeader(header));
        }

        log.info("=== session info ===");
        Random rand = new Random();
        HttpSession session = req.getSession();
        String sessVal = "";
        if (req.getParameter("clear") != null) {
            session.invalidate();
        } else {
            Enumeration<String> sessNames = session.getAttributeNames();

            if (!sessNames.hasMoreElements()) {
                System.out.println("session contains no information!");
                sessVal = "sess-" + rand.nextInt(10000);
                session.setAttribute("richSession", sessVal);
            } else {
                String name = sessNames.nextElement();
                sessVal = session.getAttribute(name).toString();
                System.out.println("session name: " + name + " value: " + sessVal);

            }
        }

        if (req.getParameter("setcookie") != null) {
            Cookie cookie = new Cookie("myCookie", "richard-123");
            cookie.setMaxAge(30);
            cookie.setHttpOnly(true);
//            cookie.setSecure(true); nachdem wir https haben
            resp.addCookie(cookie);
        } else {
            Cookie[] cookies = req.getCookies();
            if (cookies == null || cookies.length == 0) {
                log.debug("no cookies found");
            } else {
                log.debug("cookie: " + cookies[0].getName() + " val: " + cookies[0].getValue());
            }
        }
        List<Player> foundPlayers = new ArrayList<>();
        if (req.getParameter("player") != null) {
            foundPlayers.add(playerService.fetchSinglePlayer(req.getParameter("player")));
            log.debug("found players {} in servlet", foundPlayers);
        } else if (req.getParameter("alter") != null){
            foundPlayers = playerService.fetchPlayersByAlter(Integer.parseInt(req.getParameter("alter")));
            log.debug("found players {} in servlet", foundPlayers);
        }

        provideResponse(resp, sessVal, foundPlayers);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> params = req.getParameterMap();
        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");
        String content = provideHtmlTemplate();

        try (BufferedReader bin = req.getReader()){
            String playerStr = readRequestBody(bin);
            Player newPlayer = mapToPlayer(playerStr);
            Player savedPlayer = playerService.savePlayer(newPlayer);
            log.info("result of call to playerService.savePlayer was {}", savedPlayer != null);
            if (savedPlayer != null){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.write(new StringBuilder(content).insert(173, newPlayer.toString()).toString().getBytes());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("error with processing player".getBytes());
            }
        } catch (JsonProcessingException e){
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("error with provided input values".getBytes());
        } catch (InvalidInputException e){
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("error with processing player".getBytes());
        } catch (DatabaseAccessFailed de) {
            log.error(de.getStackTrace().toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("error with processing player".getBytes());
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");
        String content = provideHtmlTemplate();

        log.debug("=== params ===");
        req.getParameterMap()
                .forEach((key, value) -> log.debug("name: " + key + " values: " + Arrays.toString(value)));
        String playerNameToUpdate = req.getParameter("name");

        try (BufferedReader bin = req.getReader()){
            String playerStr = readRequestBody(bin);
            Player newPlayer = mapToPlayer(playerStr);
            boolean result = playerService.updatePlayer(newPlayer, playerNameToUpdate);
            log.info("result of call to playerService.savePlayer was {}", result);
            if (result){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.write(new StringBuilder(content).insert(173, newPlayer.toString()).toString().getBytes());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("error with processing player".getBytes());
            }
        } catch (JsonProcessingException e){
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("error with provided input values".getBytes());
        } catch (InvalidInputException e){
            log.error(e.getClass().getName());
            log.error(Arrays.toString(e.getStackTrace()));
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("error with processing player".getBytes());
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        super.doDelete(req, resp);
    }

    private void provideResponse(HttpServletResponse resp, String sessVal, List<Player> foundPlayers) throws IOException {
        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");

        resp.setStatus(HttpServletResponse.SC_OK);
        String content = provideHtmlTemplate();
        if (foundPlayers != null && foundPlayers.size() > 0) {
            out.write(new StringBuilder(content).insert(173, List.of(foundPlayers).toString()).toString().getBytes());
            out.flush();
        } else {
            out.write(new StringBuilder(content).insert(173, sessVal).toString().getBytes());
            out.flush();
        }
    }

    private Player mapToPlayer(String from) throws JsonProcessingException {
        return objectMapper.readValue(from, Player.class);
    }

    private String provideHtmlTemplate() {
        try {
            String pathIndxHtml = "/WEB-INF/templates/index.html";
            return new String(Files.readAllBytes(Path.of(getServletContext().getResource(pathIndxHtml).toURI())));
        } catch (URISyntaxException | IOException e){
            log.error ("{} by looking for index.html",e.getClass().getName());
        }
        return "";
    }

    private String readRequestBody(BufferedReader bin) throws IOException {
        char[] data = new char[1024];
        StringBuilder strB = new StringBuilder();
        while (bin.read(data) != -1){
            strB.append(data);
        }
        return strB.toString();
    }
}
