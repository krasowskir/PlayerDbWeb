package org.richard.home.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.richard.home.exception.InvalidInputException;
import org.richard.home.model.Player;
import org.richard.home.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PlayerServlet extends HttpServlet {

    Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private ObjectMapper objectMapper;
    private PlayerService playerService;

    public PlayerServlet() {
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("=== params ===");
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
            resp.addCookie(cookie);
        } else {
            Cookie[] cookies = req.getCookies();
            if (cookies == null || cookies.length == 0) {
                log.debug("no cookies found");
            } else {
                log.debug("cookie: " + cookies[0].getName() + " val: " + cookies[0].getValue());
            }
        }
        Player foundPlayer = null;
        if (req.getParameter("player") != null) {
            foundPlayer = playerService.fetchSinglePlayer(req.getParameter("player"));
            log.debug("found player {} in servlet", foundPlayer);
        }

        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");


        resp.setStatus(HttpServletResponse.SC_OK);
        String content = provideHtmlTemplate();
        if (foundPlayer != null) {
            out.write(new StringBuilder(content).insert(173, foundPlayer.toString()).toString().getBytes());
            out.flush();
        } else {
            out.write(new StringBuilder(content).insert(173, sessVal).toString().getBytes());
            out.flush();
        }

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
            boolean result = playerService.savePlayer(newPlayer);
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

    private String provideHtmlTemplate() {
        try {
            String pathIndxHtml = "/WEB-INF/classes/index.html";
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    public Player mapToPlayer(String from) throws JsonProcessingException {
        return objectMapper.readValue(from, Player.class);
    }
}
